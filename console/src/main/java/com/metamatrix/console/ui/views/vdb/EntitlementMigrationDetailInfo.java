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

package com.metamatrix.console.ui.views.vdb;

import com.metamatrix.console.ui.views.entitlements.DataNodePermissions;

/**
 * Data class to represent, for a single data node in a single entitlement,
 * indicator as to whether the node was successfully migrated from one
 * entitlement to another, and the authorizations for the node.
 */
public class EntitlementMigrationDetailInfo {
    public final static int MATCHED = 1;
    public final static int DROPPED = 2;
    public final static int NEW = 3;

    private String nodeName;
    //One of MATCHED, DROPPED, or NEW:
    private int migrationResult;
    private DataNodePermissions permissions;

    public EntitlementMigrationDetailInfo(String node, int result,
            DataNodePermissions perm) {
        super();
        nodeName = node;
        migrationResult = result;
        permissions = perm;
    }

    public String getNodeName() {
        return nodeName;
    }

    public int getMigrationResult() {
        return migrationResult;
    }

    public DataNodePermissions getPermissions() {
        return permissions;
    }
}
