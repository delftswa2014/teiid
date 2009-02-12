/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package com.metamatrix.query.processor.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.api.exception.MetaMatrixProcessingException;
import com.metamatrix.common.buffer.BlockedException;
import com.metamatrix.common.buffer.BufferManager;
import com.metamatrix.common.buffer.TupleSource;
import com.metamatrix.common.buffer.TupleSourceID;
import com.metamatrix.common.buffer.TupleSourceNotFoundException;
import com.metamatrix.common.buffer.BufferManager.TupleSourceType;
import com.metamatrix.common.log.LogManager;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.query.execution.QueryExecPlugin;
import com.metamatrix.query.mapping.xml.ResultSetInfo;
import com.metamatrix.query.processor.ProcessorDataManager;
import com.metamatrix.query.processor.ProcessorPlan;
import com.metamatrix.query.processor.QueryProcessor;
import com.metamatrix.query.sql.symbol.ElementSymbol;
import com.metamatrix.query.sql.symbol.Expression;
import com.metamatrix.query.sql.symbol.Reference;
import com.metamatrix.query.util.CommandContext;
import com.metamatrix.query.util.ErrorMessageKeys;
import com.metamatrix.query.util.LogConstants;
import com.metamatrix.query.util.TypeRetrievalUtil;


/** 
 * This is a helper class which can execute a supplied relational plan and supply
 * resulting query results to the caller.
 * Note: in future we would want to replace this class with submitting the requests directly to
 * process worker queue.
 */
class RelationalPlanExecutor implements PlanExecutor {
    
    QueryProcessor internalProcessor;
    TupleSourceID internalResultID;

    // information about the result set.
    ResultSetInfo resultInfo;
    // buffer store
    BufferManager bufferMgr;
    // flag to denote the end of rows
    boolean endOfRows = false;
    // results after the execution bucket.
    TupleSource tupleSource;
    // cached current row of results.
    List currentRow;
    int currentRowNumber = 0;
    
    public RelationalPlanExecutor (ResultSetInfo resultInfo, CommandContext context, ProcessorDataManager dataMgr, BufferManager bufferMgr) 
        throws MetaMatrixComponentException{
        
        this.resultInfo = resultInfo;
        this.bufferMgr = bufferMgr;
        
        ProcessorPlan plan = (ProcessorPlan)resultInfo.getPlan();
        List schema = plan.getOutputElements();
        CommandContext subContext = (CommandContext)context.clone();
        
        this.internalResultID = bufferMgr.createTupleSource(schema, TypeRetrievalUtil.getTypeNames(schema), subContext.getConnectionID(), TupleSourceType.PROCESSOR);
        subContext.setTupleSourceID(internalResultID);
        subContext.setProcessorID(subContext.getProcessorID());
        this.internalProcessor = new QueryProcessor(plan, subContext, bufferMgr, dataMgr);
    }
    
    /** 
     * @see com.metamatrix.query.processor.xml.PlanExecutor#getOutputElements()
     */
    public List getOutputElements() throws MetaMatrixComponentException {
        ProcessorPlan plan = (ProcessorPlan)resultInfo.getPlan();
        return plan.getOutputElements();
    }      

    /**
     * @throws MetaMatrixProcessingException 
     * @see com.metamatrix.query.processor.xml.PlanExecutor#execute(java.util.Map)
     */
    public void execute(Map referenceValues) throws MetaMatrixComponentException, BlockedException, MetaMatrixProcessingException {        
        try {
            // set the dynamic reference values
            setReferenceValues(referenceValues);
            
            // execute
            this.internalProcessor.process(Integer.MAX_VALUE); //TODO: but an actual value here
        } catch (MetaMatrixComponentException e) {
            throw e;
        } catch (MetaMatrixProcessingException e) {
        	throw e;
        } catch (MetaMatrixCoreException e) {
            throw new MetaMatrixComponentException(e, ErrorMessageKeys.PROCESSOR_0047,QueryExecPlugin.Util.getString(ErrorMessageKeys.PROCESSOR_0047,e.getMessage()));
        }
    }    
    
    void setReferenceValues(Map referencesValues) throws MetaMatrixComponentException {
        if (this.resultInfo.hasReferences() && (referencesValues != null && !referencesValues.isEmpty()) ) {
            for (final Iterator i = this.resultInfo.getReferences().iterator(); i.hasNext();) {
                Reference ref = (Reference)i.next();
                Expression expr = ref.getExpression();
                if(expr instanceof ElementSymbol) {
                    ElementSymbol element = (ElementSymbol) expr;
                    if(referencesValues.containsKey(element)) {
                        setReferenceValue(referencesValues.get(element), element, ref);
                    }
                    else {
                        throw new MetaMatrixComponentException(QueryExecPlugin.Util.getString("unmapped_reference", element.getName())); //$NON-NLS-1$
                    }
                }
            }
        }
    }    
    
    static void setReferenceValue(Object value, ElementSymbol element, Reference ref) {
        Map elmntMap = new HashMap();
        elmntMap.put(element, new Integer(0));
        List tuple = new ArrayList(1);
        tuple.add(value);
        ref.setData(elmntMap, tuple);
    }    
    
    /**
     * Get the next row from the result set
     * @return
     * @throws MetaMatrixComponentException
     */
    public List nextRow() throws MetaMatrixComponentException, MetaMatrixProcessingException {
        if (this.tupleSource == null) {
            try {
                this.tupleSource = this.bufferMgr.getTupleSource(this.internalResultID);
            } catch (TupleSourceNotFoundException e) {
                throw new MetaMatrixComponentException(e, QueryExecPlugin.Util.getString("tuple_not_found", new String[] {this.resultInfo.getResultSetName()})); //$NON-NLS-1$                
            }
        }
        
        // if we not already closed the tuple source; look for more results.
        if (!endOfRows) {

            // get the next row
            this.currentRow = this.tupleSource.nextTuple();     
            this.currentRowNumber++;

            // check if we walked over the row limit
            if (this.currentRow != null && this.resultInfo.getUserRowLimit() > 0 && this.currentRowNumber > this.resultInfo.getUserRowLimit()) {
                if (this.resultInfo.exceptionOnRowlimit()) {
                    throw new MetaMatrixProcessingException(QueryExecPlugin.Util.getString("row_limit_passed", new Object[] { new Integer(this.resultInfo.getUserRowLimit()), this.resultInfo.getResultSetName()})); //$NON-NLS-1$                
                }
                // well, we did not throw a exception, that means we need to limit it to current row
                this.currentRow = null;
            }
        }
        
        if (this.currentRow == null) {
            this.endOfRows = true;
        }
        return this.currentRow;
    }
    
    /**
     * Get the current row. 
     * @return
     */
    public List currentRow() throws MetaMatrixComponentException, MetaMatrixProcessingException {
        // automatically forward to the very first row.
        if (this.currentRow == null && !endOfRows) {
            return nextRow();
        }

        return this.currentRow;
    }
    
    /**
     * Close the executor and release all the resources.
     */
    public void close() throws MetaMatrixComponentException {
        removeTupleSource(this.resultInfo.getResultSetName(), this.internalResultID);
    }
    
    /**
     * Remove a tuple source when it is done with it.
     */
    void removeTupleSource(String rsName, TupleSourceID tupleSourceID)
        throws MetaMatrixComponentException {
        try {
            this.bufferMgr.removeTupleSource(tupleSourceID);
        } catch (TupleSourceNotFoundException e) {
            throw new MetaMatrixComponentException(ErrorMessageKeys.PROCESSOR_0021, QueryExecPlugin.Util.getString(ErrorMessageKeys.PROCESSOR_0021, rsName));
        } catch (MetaMatrixComponentException e) {
            throw new MetaMatrixComponentException(ErrorMessageKeys.PROCESSOR_0022, QueryExecPlugin.Util.getString(ErrorMessageKeys.PROCESSOR_0022, rsName));
        }
        LogManager.logTrace(LogConstants.CTX_XML_PLAN, new Object[]{"removed tuple source", tupleSourceID, "for result set", rsName}); //$NON-NLS-1$ //$NON-NLS-2$
    }
  
}
