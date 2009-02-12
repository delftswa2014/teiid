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

package com.metamatrix.dqp.internal.process;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import com.metamatrix.dqp.message.RequestID;
import com.metamatrix.platform.security.api.MetaMatrixSessionID;
import com.metamatrix.platform.security.api.SessionToken;

public class DQPWorkContext implements Externalizable {
	
	private static final long serialVersionUID = -6389893410233192977L;
	
	private static ThreadLocal<DQPWorkContext> CONTEXTS = new ThreadLocal<DQPWorkContext>() {
		protected DQPWorkContext initialValue() {
			return new DQPWorkContext();
		}
	};

	public static DQPWorkContext getWorkContext() {
		return CONTEXTS.get();
	}
	
	public static void setWorkContext(DQPWorkContext context) {
		CONTEXTS.set(context);
	}
	
    private String connectionID;
    private MetaMatrixSessionID sessionId;
    private String userName;
    private Serializable trustedPayload;
    private String vdbName;
    private String vdbVersion;
    private String appName;
    private SessionToken sessionToken;
    
    public DQPWorkContext() {
	}

    /**
     * @return
     */
    public Serializable getTrustedPayload() {
        return trustedPayload;
    }

    /**
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return
     */
    public String getVdbName() {
        return vdbName;
    }

    /**
     * @return
     */
    public String getVdbVersion() {
        return vdbVersion;
    }

    /**
     * @param serializable
     */
    public void setTrustedPayload(Serializable trustedPayload) {
        this.trustedPayload = trustedPayload;
    }

    /**
     * @param string
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @param string
     */
    public void setVdbName(String vdbName) {
        this.vdbName = vdbName;
    }

    /**
     * @param string
     */
    public void setVdbVersion(String vdbVersion) {
        this.vdbVersion = vdbVersion;
    }

	public String getConnectionID() {
		return connectionID;
	}
	
	public MetaMatrixSessionID getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(MetaMatrixSessionID sessionId) {
		this.sessionId = sessionId;
		this.connectionID = sessionId.toString();
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppName() {
		return appName;
	}
	
	public RequestID getRequestID(long exeuctionId) {
		return new RequestID(this.getConnectionID(), exeuctionId);
	}
	
	public void setSessionToken(SessionToken sessionToken) {
		this.sessionToken = sessionToken;
	}

	public SessionToken getSessionToken() {
		return sessionToken;
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		this.setSessionId((MetaMatrixSessionID)in.readObject());
		this.setUserName((String)in.readObject());
		this.setTrustedPayload((Serializable)in.readObject());
		this.setVdbName((String)in.readObject());
		this.setVdbVersion((String)in.readObject());
		this.setAppName((String)in.readObject());
		this.setSessionToken((SessionToken)in.readObject());
	}

	public void writeExternal(ObjectOutput out) throws IOException {
	    out.writeObject(sessionId);
	    out.writeObject(userName);
	    out.writeObject(trustedPayload);
	    out.writeObject(vdbName);
	    out.writeObject(vdbVersion);
	    out.writeObject(appName);
	    out.writeObject(sessionToken);
	}

}
