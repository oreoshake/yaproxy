/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2012 The YAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yaproxy.yap.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import org.parosproxy.paros.Constant;

public class Tech implements Comparable<Tech> {

    // Tech hierarchy inspired by this article:
    // http://java.dzone.com/articles/enum-tricks-hierarchical-data
    // even though I've gone with a class instead on an enum;)
    public static final Tech Db = new Tech("Db", "technologies.db");
    public static final Tech MySQL = new Tech(Db, "MySQL");
    public static final Tech PostgreSQL = new Tech(Db, "PostgreSQL");
    public static final Tech MsSQL = new Tech(Db, "Microsoft SQL Server");
    public static final Tech Oracle = new Tech(Db, "Oracle");
    public static final Tech SQLite = new Tech(Db, "SQLite");
    public static final Tech Access = new Tech(Db, "Microsoft Access");
    public static final Tech Firebird = new Tech(Db, "Firebird");
    public static final Tech MaxDB = new Tech(Db, "SAP MaxDB");
    public static final Tech Sybase = new Tech(Db, "Sybase");
    public static final Tech Db2 = new Tech(Db, "IBM DB2");
    public static final Tech HypersonicSQL = new Tech(Db, "HypersonicSQL");
    public static final Tech MongoDB = new Tech(Db, "MongoDB");
    public static final Tech CouchDB = new Tech(Db, "CouchDB");

    public static final Tech Lang = new Tech("Language", "technologies.lang");
    public static final Tech ASP = new Tech(Lang, "ASP");
    public static final Tech C = new Tech(Lang, "C");
    public static final Tech JAVA = new Tech(Lang, "Java");
    public static final Tech SPRING = new Tech(JAVA, "Spring");
    public static final Tech JAVASCRIPT = new Tech(Lang, "JavaScript");
    public static final Tech JSP_SERVLET = new Tech(Lang, "JSP/Servlet");
    public static final Tech PHP = new Tech(Lang, "PHP");
    public static final Tech PYTHON = new Tech(Lang, "Python");
    public static final Tech RUBY = new Tech(Lang, "Ruby");
    public static final Tech XML = new Tech(Lang, "XML");

    public static final Tech OS = new Tech("OS", "technologies.os");
    public static final Tech Linux = new Tech(OS, "Linux");
    public static final Tech MacOS = new Tech(OS, "MacOS");
    public static final Tech Windows = new Tech(OS, "Windows");

    public static final Tech SCM = new Tech("SCM", "technologies.scm");
    public static final Tech Git = new Tech(SCM, "Git");
    public static final Tech SVN = new Tech(SCM, "SVN");

    public static final Tech WS = new Tech("WS", "technologies.ws");
    public static final Tech Apache = new Tech(WS, "Apache");
    public static final Tech IIS = new Tech(WS, "IIS");
    public static final Tech Tomcat = new Tech(WS, "Tomcat");

    private static final TreeSet<Tech> allTech =
            new TreeSet<>(
                    Arrays.asList(
                            Db,
                            MySQL,
                            PostgreSQL,
                            MsSQL,
                            Oracle,
                            SQLite,
                            Access,
                            Firebird,
                            MaxDB,
                            Sybase,
                            Db2,
                            HypersonicSQL,
                            MongoDB,
                            CouchDB,
                            Lang,
                            ASP,
                            C,
                            JAVA,
                            SPRING,
                            JAVASCRIPT,
                            JSP_SERVLET,
                            PHP,
                            PYTHON,
                            RUBY,
                            XML,
                            OS,
                            Linux,
                            MacOS,
                            Windows,
                            SCM,
                            Git,
                            SVN,
                            WS,
                            Apache,
                            IIS,
                            Tomcat));

    private static final TreeSet<Tech> topLevelTech =
            new TreeSet<>(Arrays.asList(Db, Lang, OS, SCM, WS));

    /**
     * @deprecated Not for public use. Replaced by {@link #getAll()}.
     */
    @Deprecated public static final Tech[] builtInTech = allTech.toArray(new Tech[] {});

    /**
     * @deprecated Not for public use. Replaced by {@link #getTopLevel()}.
     */
    @Deprecated
    public static final Tech[] builtInTopLevelTech = topLevelTech.toArray(new Tech[] {});

    private Tech parent = null;
    private String name = null;
    private String keyUiName;

    public Tech(String name) {
        this(name, null);
    }

    public Tech(String name, String keyUiName) {
        if (name.indexOf(".") > 0) {
            this.name = name.substring(name.lastIndexOf(".") + 1);
            this.parent = new Tech(name.substring(0, name.lastIndexOf(".")));

        } else {
            this.name = name;
        }
        this.keyUiName = keyUiName;
    }

    public Tech(Tech parent, String name) {
        this(parent, name, null);
    }

    public Tech(Tech parent, String name, String keyUiName) {
        this.parent = parent;
        this.name = name;
        this.keyUiName = keyUiName;
    }

    @Override
    public String toString() {
        if (parent == null) {
            return this.name;
        } else {
            return parent.toString() + "." + this.name;
        }
    }

    @Override
    public boolean equals(Object tech) {
        if (!(tech instanceof Tech)) {
            return false;
        }
        return this.toString().equals(tech.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean is(Tech other) {
        if (other == null) {
            return false;
        }

        for (Tech t = this; t != null; t = t.parent) {
            if (other == t) {
                return true;
            }
        }

        return false;
    }

    public Tech getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String getUiName() {
        if (keyUiName == null) {
            return getName();
        }
        return Constant.messages.getString(keyUiName);
    }

    @Override
    public int compareTo(Tech o) {
        if (o == null) {
            return -1;
        }
        return this.toString().compareTo(o.toString());
    }

    /**
     * Returns all Tech
     *
     * @return all Tech
     * @since 2.10.0
     */
    public static Set<Tech> getAll() {
        return Collections.unmodifiableSet(allTech);
    }

    /**
     * Returns all top level Tech
     *
     * @return top level Tech
     * @since 2.10.0
     */
    public static Set<Tech> getTopLevel() {
        return Collections.unmodifiableSet(topLevelTech);
    }

    /**
     * Adds a new Tech, if parent is omitted it is treated as top level Tech.
     *
     * @param tech to add
     * @since 2.10.0
     */
    public static void add(Tech tech) {
        if (tech != null) {
            if (tech.getParent() == null) {
                topLevelTech.add(tech);
            }
            allTech.add(tech);
        }
    }

    /**
     * Remove entry from Tech
     *
     * @param tech to remove
     * @since 2.10.0
     */
    public static void remove(Tech tech) {
        if (tech != null) {
            if (tech.getParent() == null) {
                topLevelTech.remove(tech);
            }
            allTech.remove(tech);
        }
    }

    /**
     * Gets the Tech that matches the name or null
     *
     * @param name the name of the Tech
     * @return the matching Tech
     * @since 2.10.0
     */
    public static Tech get(String name) {
        if (name == null) {
            return null;
        }

        String trimmedTechName = name.trim();
        if (trimmedTechName.isEmpty()) {
            return null;
        }

        for (Tech tech : allTech) {
            if (tech.toString().equalsIgnoreCase(trimmedTechName)) {
                return tech;
            }
        }
        return null;
    }
}
