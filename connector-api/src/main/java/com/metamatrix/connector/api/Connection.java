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

package com.metamatrix.connector.api;

import com.metamatrix.connector.exception.ConnectorException;
import com.metamatrix.connector.language.ICommand;
import com.metamatrix.connector.metadata.runtime.RuntimeMetadata;
import com.metamatrix.connector.pool.PoolAwareConnection;

/**
 * <p>Represents a connection to this connector.  A connection will be obtained
 * from the connector for every query that is executed, then closed after the query has completed.
 * </p>
 * <p>If pooling is enabled, see {@link PoolAwareConnection} to optionally implement pooling specific behavior.
 * </p>  
 */
public interface Connection {

    /**
     * Get the capabilities of this connector.  The capabilities affect what kinds of 
     * queries (and other commands) will be sent to the connector.
     * @return Connector capabilities, may return null if the Connector returns globally scoped capabilities {@link Connector#getCapabilities()}
     */
    ConnectorCapabilities getCapabilities();

    /**
     * Create an execution object for the specified command  
     * @param command the command
     * @param executionContext Provides information about the context that this command is
     * executing within, such as the identifiers for the MetaMatrix command being executed
     * @param metadata Access to runtime metadata if needed to translate the command
     * @return An execution object that MetaMatrix can use to execute the command
     */
    Execution createExecution(ICommand command, ExecutionContext executionContext, RuntimeMetadata metadata ) throws ConnectorException;

    /**
     * Release the connection.  This will be called when MetaMatrix has completed 
     * using the connection for an execution.
     */
    void close();
}

