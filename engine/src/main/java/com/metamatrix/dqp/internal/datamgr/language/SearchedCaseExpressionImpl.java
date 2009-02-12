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

package com.metamatrix.dqp.internal.datamgr.language;

import java.util.List;

import com.metamatrix.connector.language.ICriteria;
import com.metamatrix.connector.language.IExpression;
import com.metamatrix.connector.language.ISearchedCaseExpression;
import com.metamatrix.connector.visitor.framework.LanguageObjectVisitor;

public class SearchedCaseExpressionImpl extends BaseLanguageObject implements ISearchedCaseExpression {

    private List whenExpressions;
    private List thenExpressions;
    private IExpression elseExpression;
    private Class type;
    
    public SearchedCaseExpressionImpl(List whens, List thens, IExpression elseExpression, Class type) {
        this.whenExpressions = whens;
        this.thenExpressions = thens;
        this.elseExpression = elseExpression;
        this.type = type;
    }
    /**
     * @see com.metamatrix.connector.language.ISearchedCaseExpression#getElseExpression()
     */
    public IExpression getElseExpression() {
        return elseExpression;
    }

    /**
     * @see com.metamatrix.connector.language.ISearchedCaseExpression#getThenExpression(int)
     */
    public IExpression getThenExpression(int index) {
        return (IExpression)thenExpressions.get(index);
    }

    /**
     * @see com.metamatrix.connector.language.ISearchedCaseExpression#getWhenCount()
     */
    public int getWhenCount() {
        return whenExpressions.size();
    }

    /**
     * @see com.metamatrix.connector.language.ISearchedCaseExpression#getWhenCriteria(int)
     */
    public ICriteria getWhenCriteria(int index) {
        return (ICriteria)whenExpressions.get(index);
    }

    /**
     * @see com.metamatrix.connector.language.ILanguageObject#acceptVisitor(com.metamatrix.data.visitor.LanguageObjectVisitor)
     */
    public void acceptVisitor(LanguageObjectVisitor visitor) {
        visitor.visit(this);
    }
    /* 
     * @see com.metamatrix.data.language.ISearchedCaseExpression#setWhenCriteria(int, com.metamatrix.data.language.ICriteria)
     */
    public void setWhenCriteria(int index, ICriteria criteria) {
        this.whenExpressions.set(index, criteria);
    }
    /* 
     * @see com.metamatrix.data.language.ISearchedCaseExpression#setThenExpression(int, com.metamatrix.data.language.IExpression)
     */
    public void setThenExpression(int index, IExpression expression) {
        this.thenExpressions.set(index, expression);
    }
    /* 
     * @see com.metamatrix.data.language.ISearchedCaseExpression#setElseExpression(com.metamatrix.data.language.IExpression)
     */
    public void setElseExpression(IExpression expression) {
        this.elseExpression = expression;
    }
    /* 
     * @see com.metamatrix.data.language.IExpression#getType()
     */
    public Class getType() {
        return this.type;
    }
    /* 
     * @see com.metamatrix.data.language.IExpression#setType(java.lang.Class)
     */
    public void setType(Class type) {
        this.type = type;
    }

}
