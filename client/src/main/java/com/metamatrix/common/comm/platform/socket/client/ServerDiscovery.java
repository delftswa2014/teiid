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

package com.metamatrix.common.comm.platform.socket.client;

import java.util.List;
import java.util.Properties;

import com.metamatrix.common.api.HostInfo;
import com.metamatrix.common.api.MMURL;
import com.metamatrix.platform.security.api.LogonResult;

/**
 * Customizable ServerDiscovery interface
 * 
 * TODO: add knowledge of the cluster/ServerConnection in the getKnownHosts calls 
 */
public interface ServerDiscovery {
	
	void init(MMURL url, Properties p);
	
	List<HostInfo> getKnownHosts();
	
	void connectionSuccessful(HostInfo info, SocketServerInstance instance);
	
	void markInstanceAsBad(HostInfo info);
	
	void shutdown();
	
	/**
	 * Sets the {@link LogonResult} after authentication.  The {@link LogonResult} will contain information
	 * such as the cluster name that can be used for more efficient discovery.
	 * @param result
	 * @return <code>true</code> if the connection should select another instance after logon.
	 */
	boolean setLogonResult(LogonResult result);
	
}
