/*
 *
 * Paros and its related class files.
 *
 * Paros is an HTTP/HTTPS proxy for assessing web application security.
 * Copyright (C) 2003-2004 Chinotec Technologies Company
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Clarified Artistic License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Clarified Artistic License for more details.
 *
 * You should have received a copy of the Clarified Artistic License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
// YAP: 2012/04/25 Added type arguments to generic types, removed variables,
// added logger and other minor changes.
// YAP: 2012/05/04 Catch CloneNotSupportedException whenever an Uri is cloned,
//              as introduced with version 3.1 of HttpClient
// YAP: 2016/09/20 JavaDoc tweaks
// YAP: 2018/02/14 Remove unnecessary boxing / unboxing
// YAP: 2019/06/01 Normalise line endings.
// YAP: 2019/06/05 Normalise format/style.
// YAP: 2020/11/26 Use Log4j 2 classes for logging.
// YAP: 2022/02/08 Use isEmpty where applicable.
// YAP: 2023/01/10 Tidy up logger.
package org.parosproxy.paros.core.scanner;

import java.util.TreeMap;
import java.util.Vector;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Knowledge base records the properties or result found during a scan. It is mainly used to share
 * result among plugin when dependency arise.
 *
 * <p>There are 2 types of Kb: 1. key = name. result = value. This represents kb applicable over the
 * entire host. 2. key = url (path without query) and name. result = value. This represents kb
 * applicable for specific path only.
 */
public class Kb {

    // YAP: Added logger.
    private static final Logger LOGGER = LogManager.getLogger(Kb.class);

    // KB related
    // YAP: Added the type arguments.
    private TreeMap<String, Object> mapKb = new TreeMap<>();
    // YAP: Added the type arguments.
    private TreeMap<String, TreeMap<String, Object>> mapURI = new TreeMap<>();

    /**
     * Get a list of the values matching the key.
     *
     * @param key the key for the knowledge base list entry
     * @return null if there is no previous values.
     */
    // YAP: Added the type argument.
    public synchronized Vector<Object> getList(String key) {
        return getList(mapKb, key);
    }

    /**
     * Add the key value pair to KB. Only unique value will be added to KB.
     *
     * @param key the key for the knowledge base entry
     * @param value the value of the new entry
     */
    public synchronized void add(String key, Object value) {
        add(mapKb, key, value);
    }

    public synchronized Object get(String key) {
        // YAP: Added the type argument.
        Vector<Object> v = getList(key);
        if (v == null || v.isEmpty()) {
            return null;
        }

        return v.get(0);
    }

    /**
     * Get the first item in KB matching the key as a String.
     *
     * @param key the key for the knowledge base entry
     * @return the entry, or {@code null} if not a {@code String} or does not exist
     */
    public String getString(String key) {
        Object obj = get(key);
        if (obj != null && obj instanceof String) {
            return (String) obj;
        }
        return null;
    }

    public boolean getBoolean(String key) {
        Object obj = get(key);
        if (obj != null && obj instanceof Boolean) {
            return (Boolean) obj;
        }
        return false;
    }

    public synchronized void add(URI uri, String key, Object value) {
        // YAP: catch CloneNotSupportedException as introduced with version 3.1 of HttpClient
        try {
            uri = (URI) uri.clone();
        } catch (CloneNotSupportedException e1) {
            return;
        }

        // YAP: Removed variable (TreeMap map).
        try {
            uri.setQuery(null);
        } catch (URIException e) {
            // YAP: Added logging.
            LOGGER.error(e.getMessage(), e);
            return;
        }
        // YAP: Moved to after the try catch block.
        String uriKey = uri.toString();
        // YAP: Added the type arguments.
        TreeMap<String, Object> map = mapURI.get(uriKey);
        if (map == null) {
            // YAP: Added the type argument.
            map = new TreeMap<>();
            mapURI.put(uriKey, map);
        } // YAP: Removed else branch.

        add(map, key, value);
    }

    public synchronized Vector<Object> getList(URI uri, String key) {
        // YAP: catch CloneNotSupportedException as introduced with version 3.1 of HttpClient
        try {
            uri = (URI) uri.clone();
        } catch (CloneNotSupportedException e1) {
            return null;
        }

        // YAP: Removed variable (TreeMap map).
        try {
            uri.setQuery(null);
        } catch (URIException e) {
            // YAP: Added logging.
            LOGGER.error(e.getMessage(), e);
            return null;
        }
        // YAP: Moved to after the try catch block.
        String uriKey = uri.toString();
        // YAP: Added the type argument and removed the instanceof.
        TreeMap<String, Object> map = mapURI.get(uriKey);
        if (map == null) {
            return null;
        } // YAP: Removed else branch.

        return getList(map, key);
    }

    public synchronized Object get(URI uri, String key) {
        // YAP: Added the type argument.
        Vector<Object> v = getList(uri, key);
        if (v == null || v.isEmpty()) {
            return null;
        }

        return v.get(0);
    }

    public String getString(URI uri, String key) {
        Object obj = get(uri, key);
        if (obj != null && obj instanceof String) {
            return (String) obj;
        }
        return null;
    }

    public boolean getBoolean(URI uri, String key) {
        Object obj = get(uri, key);
        if (obj != null && obj instanceof Boolean) {
            return (Boolean) obj;
        }
        return false;
    }

    /**
     * Generic method for adding into a map
     *
     * @param map the map of the knowledge base entries
     * @param key the key for the knowledge base entry
     * @param value the value of the entry
     */
    // YAP: Added the type arguments.
    private void add(TreeMap<String, Object> map, String key, Object value) {
        // YAP: Added the type argument.
        Vector<Object> v = getList(map, key);
        if (v == null) {
            // YAP: Added the type argument.
            v = new Vector<>();
            synchronized (map) {
                map.put(key, v);
            }
        }
        if (!v.contains(value)) {
            v.add(value);
        }
    }

    /**
     * Generic method for getting values out of a map
     *
     * @param map the map of the knowledge base entries
     * @param key the key for the knowledge base entry
     * @return the values of the entry, might be {@code null}
     */
    // YAP: Added the type arguments and @SuppressWarnings annotation.
    @SuppressWarnings("unchecked")
    private Vector<Object> getList(TreeMap<String, Object> map, String key) {
        Object obj = null;
        synchronized (map) {
            obj = map.get(key);
        }

        if (obj != null && obj instanceof Vector) {
            // YAP: Added the type argument.
            return (Vector<Object>) obj;
        }
        return null;
    }
}
