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

package org.teiid.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.teiid.connector.DataPlugin;

/**
 * Simple holder for metadata.
 */
public class MetadataStore implements Serializable {

	private static final long serialVersionUID = -3130247626435324312L;
	protected Map<String, Schema> schemas = new TreeMap<String, Schema>(String.CASE_INSENSITIVE_ORDER);
	protected List<Schema> schemaList = new ArrayList<Schema>(); //used for a stable ordering
	protected Collection<Datatype> datatypes = new LinkedHashSet<Datatype>();
	protected Map<String, String> namespaces = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	
	public Map<String, Schema> getSchemas() {
		return schemas;
	}
	
	public Schema getSchema(String name) {
		Schema s = this.schemas.get(name);
		if (s == null) {
			s = this.schemas.get(name.toUpperCase());
		}
		return s;
	}
	
	public void addSchema(Schema schema) {
		if (this.schemas.put(schema.getCanonicalName(), schema) != null) {
			throw new DuplicateRecordException(DataPlugin.Util.gs(DataPlugin.Event.TEIID60012, schema.getName()));
		}		
		this.schemaList.add(schema);
	}
	
	public List<Schema> getSchemaList() {
		return schemaList;
	}
	
	void addDataTypes(Collection<Datatype> types) {
		this.datatypes.addAll(types);
	}
	
	public void addDatatype(Datatype datatype) {
		this.datatypes.add(datatype);
	}
		
	/**
	 * Get the datatypes defined in this store
	 * @return
	 */
	public Collection<Datatype> getDatatypes() {
		return datatypes;
	}
	
	public void addNamespace(String prefix, String uri) {
		this.namespaces.put(prefix, uri);
	}	
	
	public Map<String, String> getNamespaces() {
		return this.namespaces;
	}
	
	void addNamespaces(Map<String, String> namespaces) {
		this.namespaces.putAll(namespaces);
	}	
	
	public void merge(MetadataStore store) {
		if (store != null) {
			for (Schema s:store.getSchemaList()) {
				addSchema(s);
			}
			this.datatypes.addAll(store.getDatatypes());
		}
	}
}
