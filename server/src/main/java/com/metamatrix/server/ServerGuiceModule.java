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

package com.metamatrix.server;

import org.jboss.cache.Cache;
import org.jgroups.mux.Multiplexer;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.metamatrix.cache.CacheFactory;
import com.metamatrix.cache.jboss.JBossCacheFactory;
import com.metamatrix.common.comm.platform.socket.SocketVMController;
import com.metamatrix.common.config.CurrentConfiguration;
import com.metamatrix.common.config.api.Host;
import com.metamatrix.common.config.api.exceptions.ConfigurationException;
import com.metamatrix.common.id.dbid.DBIDGenerator;
import com.metamatrix.common.id.dbid.DBIDGeneratorException;
import com.metamatrix.common.messaging.MessageBus;
import com.metamatrix.common.messaging.VMMessageBus;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.platform.registry.ClusteredRegistryState;
import com.metamatrix.platform.registry.VMMonitor;
import com.metamatrix.platform.service.proxy.ProxyManager;
import com.metamatrix.platform.util.PlatformProxyHelper;
import com.metamatrix.platform.vm.api.controller.VMControllerInterface;
import com.metamatrix.platform.vm.controller.ServerEvents;
import com.metamatrix.platform.vm.controller.VMControllerID;

class ServerGuiceModule extends AbstractModule {

	Host host;
	String vmName;

	public ServerGuiceModule(Host host, String vmName) {
		this.host = host;
		this.vmName = vmName;
	}
	
	@Override
	protected void configure() {

		long vmID = 1;
		try {
			vmID = DBIDGenerator.getInstance().getID(DBIDGenerator.VM_ID);
		} catch (DBIDGeneratorException e1) {
			throw new MetaMatrixRuntimeException(e1);
		}
		
		String systemName = null;
		try {
		    systemName = CurrentConfiguration.getInstance().getClusterName();
		} catch (ConfigurationException err) {
		    systemName = "Federate-Cluster"; //$NON-NLS-1$
		}
		
		bindConstant().annotatedWith(Names.named(Configuration.HOSTNAME)).to(host.getFullName());
		bindConstant().annotatedWith(Names.named(Configuration.VMNAME)).to(vmName);
		bindConstant().annotatedWith(Names.named(Configuration.VMID)).to(vmID);
		bind(Host.class).annotatedWith(Names.named(Configuration.HOST)).toInstance(host);
		bindConstant().annotatedWith(Names.named(Configuration.CLUSTERNAME)).to(systemName);
				
		Names.bindProperties(binder(), CurrentConfiguration.getInstance().getProperties());
		
		bind(VMControllerID.class).toInstance(new VMControllerID(vmID, host.getFullName()));
		bind(Multiplexer.class).toProvider(JGroupsProvider.class).in(Scopes.SINGLETON);
		bind(ChannelProvider.class).in(Scopes.SINGLETON);
		bind(Cache.class).toProvider(CacheProvider.class).in(Scopes.SINGLETON);
		bind(CacheFactory.class).to(JBossCacheFactory.class).in(Scopes.SINGLETON);
		bind(ClusteredRegistryState.class).in(Scopes.SINGLETON);
		bind(ProxyManager.class).in(Scopes.SINGLETON);
		bind(MessageBus.class).to(VMMessageBus.class).in(Scopes.SINGLETON); // VM Message bus is in common-internal
		bind(VMControllerInterface.class).to(SocketVMController.class).in(Scopes.SINGLETON);
		bind(ServerEvents.class).to(VMMonitor.class).in(Scopes.SINGLETON);
		bind(HostManagement.class).toProvider(HostManagementProvider.class).in(Scopes.SINGLETON);
		
		// this needs to be removed.
		binder().requestStaticInjection(PlatformProxyHelper.class);
	}

}
