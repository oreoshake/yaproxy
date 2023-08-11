/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2014 The YAP Development Team
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
package org.yaproxy.yap.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.yaproxy.yap.Version;
import org.yaproxy.yap.utils.YapXmlConfiguration;

/**
 * Base class that reads common {@code YapAddOn} XML data.
 *
 * <p>Reads:
 *
 * <ul>
 *   <li>name;
 *   <li>status (since 2.6.0);
 *   <li>version;
 *   <li>semver (since 2.7.0, deprecated in favour of using version);
 *   <li>description;
 *   <li>author;
 *   <li>url;
 *   <li>repo (URL of browsable repository);
 *   <li>changes;
 *   <li>not-before-version;
 *   <li>not-from-version;
 *   <li>dependencies:
 *       <ul>
 *         <li>javaversion;
 *         <li>addon:
 *             <ul>
 *               <li>id;
 *               <li>version;
 *               <li>not-before-version (since 2.7.0, deprecated in favour of using version);
 *               <li>not-from-version (since 2.7.0, deprecated in favour of using version);
 *               <li>semver (since 2.7.0, deprecated in favour of using version).
 *             </ul>
 *       </ul>
 *   <li>extensions
 *       <ul>
 *         <li>extension
 *         <li>extension v=1:
 *             <ul>
 *               <li>classname;
 *               <li>dependencies:
 *                   <ul>
 *                     <li>javaversion;
 *                     <li>addon:
 *                         <ul>
 *                           <li>id;
 *                           <li>version;
 *                           <li>not-before-version (since 2.7.0, deprecated in favour of using
 *                               version);
 *                           <li>not-from-version (since 2.7.0, deprecated in favour of using
 *                               version);
 *                           <li>semver (since 2.7.0, deprecated in favour of using version).
 *                         </ul>
 *                   </ul>
 *             </ul>
 *       </ul>
 * </ul>
 *
 * @since 2.4.0
 */
public abstract class BaseYapAddOnXmlData {

    private static final Logger LOGGER = LogManager.getLogger(BaseYapAddOnXmlData.class);

    private static final List<String> ADDON_STATUSES = Arrays.asList("alpha", "beta", "release");

    private static final String NAME_ELEMENT = "name";
    private static final String STATUS = "status";
    private static final String VERSION_ELEMENT = "version";
    private static final String SEM_VER_ELEMENT = "semver";
    private static final String DESCRIPTION_ELEMENT = "description";
    private static final String AUTHOR_ELEMENT = "author";
    private static final String URL_ELEMENT = "url";
    private static final String CHANGES_ELEMENT = "changes";
    private static final String REPO_ELEMENT = "repo";
    private static final String NOT_BEFORE_VERSION_ELEMENT = "not-before-version";
    private static final String NOT_FROM_VERSION_ELEMENT = "not-from-version";

    private static final String DEPENDENCIES_ELEMENT = "dependencies";
    private static final String DEPENDENCIES_JAVA_VERSION_ELEMENT = "javaversion";
    private static final String DEPENDENCIES_ADDONS_ALL_ELEMENTS = "addons.addon";
    private static final String YAPADDON_ID_ELEMENT = "id";
    private static final String YAPADDON_NOT_BEFORE_VERSION_ELEMENT = "not-before-version";
    private static final String YAPADDON_NOT_FROM_VERSION_ELEMENT = "not-from-version";
    private static final String YAPADDON_VERSION_ELEMENT = "version";

    /**
     * Alias of {@link #YAPADDON_VERSION_ELEMENT}.
     *
     * <p>To be deprecated/removed in future versions of YAP.
     */
    private static final String YAPADDON_SEMVER_ELEMENT = "semver";

    private static final String EXTENSION_ELEMENT = "extension";
    private static final String EXTENSIONS_ALL_ELEMENTS = "extensions." + EXTENSION_ELEMENT;
    private static final String EXTENSIONS_VERSION_ATT = "[@v]";
    private static final String EXTENSION_CLASS_NAME = "classname";
    private static final String EXTENSION_DEPENDENCIES =
            DEPENDENCIES_ELEMENT + "." + DEPENDENCIES_ADDONS_ALL_ELEMENTS;
    private static final String CLASSNAMES_ALLOWED_ELEMENT = "allowed";
    private static final String CLASSNAMES_ALLOWED_ALL_ELEMENTS =
            "classnames." + CLASSNAMES_ALLOWED_ELEMENT;
    private static final String CLASSNAMES_RESTRICTED_ELEMENT = "restricted";
    private static final String CLASSNAMES_RESTRICTED_ALL_ELEMENTS =
            "classnames." + CLASSNAMES_RESTRICTED_ELEMENT;

    private String name;
    private String status;
    private Version version;
    private Version semVer;
    private String description;
    private String author;
    private String url;
    private String changes;
    private String repo;

    private Dependencies dependencies;

    private AddOnClassnames addOnClassnames;

    private String notBeforeVersion;
    private String notFromVersion;

    private List<String> extensions;
    private List<ExtensionWithDeps> extensionsWithDeps;

    /**
     * Constructs a {@code BaseYapAddOnXmlData} with the given {@code inputStream} as the source of
     * the {@code YapAddOn} XML data.
     *
     * @param inputStream the source of the {@code YapAddOn} XML data.
     * @throws NullPointerException if the given input stream is {@code null}.
     * @throws IOException if an error occurs while reading the data
     */
    public BaseYapAddOnXmlData(InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream, "The InputStream must not be null.");

        YapXmlConfiguration yapAddOnXml = new YapXmlConfiguration();

        try {
            yapAddOnXml.load(inputStream);
        } catch (ConfigurationException e) {
            throw new IOException(e);
        }
        readDataImpl(yapAddOnXml);
    }

    /**
     * Constructs a {@code BaseYapAddOnXmlData} with the given {@code yapAddOnXml} {@code
     * HierarchicalConfiguration} as the source of the {@code YapAddOn} XML data.
     *
     * @param yapAddOnXml the source of the {@code YapAddOn} XML data.
     */
    public BaseYapAddOnXmlData(HierarchicalConfiguration yapAddOnXml) {
        readDataImpl(yapAddOnXml);
    }

    private void readDataImpl(HierarchicalConfiguration yapAddOnXml) {
        name = yapAddOnXml.getString(NAME_ELEMENT, "");

        String v = yapAddOnXml.getString(VERSION_ELEMENT, "1.0.0");
        try {
            version = new Version(v);
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Falling back to integer version [{}] for add-on {}", v, name);
            version = new Version(Integer.parseInt(v) + ".0.0");
        }

        status = yapAddOnXml.getString(STATUS);
        if (status == null) {
            LOGGER.log(
                    Constant.isDevMode() ? Level.ERROR : Level.WARN,
                    "No status specified for {}, defaulting to \"alpha\". Add-ons should declare its status in the manifest.",
                    name);
            status = "alpha";
        } else if (!ADDON_STATUSES.contains(status)) {
            throw new IllegalArgumentException(
                    "Unrecognised status \""
                            + status
                            + "\" in "
                            + name
                            + ", expected one of "
                            + ADDON_STATUSES);
        }
        semVer = createVersion(yapAddOnXml.getString(SEM_VER_ELEMENT, ""));
        description = yapAddOnXml.getString(DESCRIPTION_ELEMENT, "");
        author = yapAddOnXml.getString(AUTHOR_ELEMENT, "");
        url = yapAddOnXml.getString(URL_ELEMENT, "");
        changes = yapAddOnXml.getString(CHANGES_ELEMENT, "");
        repo = yapAddOnXml.getString(REPO_ELEMENT, "");

        dependencies = readDependencies(yapAddOnXml, "yapaddon");

        notBeforeVersion = yapAddOnXml.getString(NOT_BEFORE_VERSION_ELEMENT, "");
        notFromVersion = yapAddOnXml.getString(NOT_FROM_VERSION_ELEMENT, "");

        extensions = getStrings(yapAddOnXml, EXTENSIONS_ALL_ELEMENTS, EXTENSION_ELEMENT);
        extensionsWithDeps = readExtensionsWithDeps(yapAddOnXml);

        addOnClassnames = readAddOnClassnames(yapAddOnXml);

        readAdditionalData(yapAddOnXml);
    }

    /**
     * Reads additional data from the {@code YapAddOn} XML.
     *
     * <p>Called after reading the common data.
     *
     * @param yapAddOnData the source of the {@code YapAddOn} XML data.
     */
    protected void readAdditionalData(HierarchicalConfiguration yapAddOnData) {}

    private static Version createVersion(String version) {
        if (!version.isEmpty()) {
            return new Version(version);
        }
        return null;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the status of the add-on, "alpha", "beta" or "release".
     *
     * @return the status of the add-on
     * @since 2.6.0
     */
    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    /**
     * Gets the package/file version of the add-on.
     *
     * @return the package/file version
     * @deprecated (2.7.0) Use {@link #getVersion()} instead.
     */
    @Deprecated
    public int getPackageVersion() {
        return version.getMajorVersion();
    }

    public Version getVersion() {
        return version;
    }

    public Version getSemVer() {
        return semVer;
    }

    public String getChanges() {
        return changes;
    }

    /**
     * Gets the URL to the browsable repo.
     *
     * @return the URL to the repo, might be {@code null}.
     * @since 2.9.0
     */
    public String getRepo() {
        return repo;
    }

    public String getUrl() {
        return url;
    }

    public Dependencies getDependencies() {
        return dependencies;
    }

    public AddOnClassnames getAddOnClassnames() {
        return addOnClassnames;
    }

    public String getNotBeforeVersion() {
        return notBeforeVersion;
    }

    public String getNotFromVersion() {
        return notFromVersion;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public List<ExtensionWithDeps> getExtensionsWithDeps() {
        return extensionsWithDeps;
    }

    protected List<String> getStrings(
            HierarchicalConfiguration yapAddOnXml, String element, String elementName) {
        String[] fields = yapAddOnXml.getStringArray(element);
        if (fields.length == 0) {
            return Collections.emptyList();
        }

        ArrayList<String> strings = new ArrayList<>(fields.length);
        for (String field : fields) {
            if (!field.isEmpty()) {
                strings.add(field);
            } else {
                LOGGER.warn("Ignoring empty \"{}\" entry in add-on \"{}\".", elementName, name);
            }
        }

        if (strings.isEmpty()) {
            return Collections.emptyList();
        }
        strings.trimToSize();

        return strings;
    }

    private Dependencies readDependencies(HierarchicalConfiguration currentNode, String element) {
        List<HierarchicalConfiguration> dependencies =
                currentNode.configurationsAt(DEPENDENCIES_ELEMENT);
        if (dependencies.isEmpty()) {
            return null;
        }

        if (dependencies.size() > 1) {
            malformedFile(
                    "expected at most one \"dependencies\" element for \""
                            + element
                            + "\" element, found "
                            + dependencies.size()
                            + ".");
        }

        HierarchicalConfiguration node = dependencies.get(0);
        String javaVersion = node.getString(DEPENDENCIES_JAVA_VERSION_ELEMENT, "");
        List<HierarchicalConfiguration> fields =
                node.configurationsAt(DEPENDENCIES_ADDONS_ALL_ELEMENTS);
        if (fields.isEmpty()) {
            return new Dependencies(javaVersion, Collections.<AddOnDep>emptyList());
        }

        List<AddOnDep> addOns = readAddOnDependencies(fields);
        return new Dependencies(javaVersion, addOns);
    }

    private List<AddOnDep> readAddOnDependencies(List<HierarchicalConfiguration> fields) {
        List<AddOnDep> addOns = new ArrayList<>(fields.size());
        for (HierarchicalConfiguration sub : fields) {
            String id = sub.getString(YAPADDON_ID_ELEMENT, "");
            if (id.isEmpty()) {
                malformedFile("an add-on dependency has empty \"" + YAPADDON_ID_ELEMENT + "\".");
            }

            String version = sub.getString(YAPADDON_VERSION_ELEMENT, "");
            if (version.isEmpty()) {
                // Fallback to alias element.
                version = sub.getString(YAPADDON_SEMVER_ELEMENT, "");
            }

            AddOnDep addOnDep =
                    new AddOnDep(
                            id,
                            sub.getString(YAPADDON_NOT_BEFORE_VERSION_ELEMENT, ""),
                            sub.getString(YAPADDON_NOT_FROM_VERSION_ELEMENT, ""),
                            version);

            addOns.add(addOnDep);
        }

        return addOns;
    }

    private List<ExtensionWithDeps> readExtensionsWithDeps(HierarchicalConfiguration currentNode) {
        List<HierarchicalConfiguration> extensions =
                currentNode.configurationsAt(EXTENSIONS_ALL_ELEMENTS).stream()
                        .filter(e -> "1".equals(e.getString(EXTENSIONS_VERSION_ATT, null)))
                        .collect(Collectors.toList());
        if (extensions.isEmpty()) {
            return Collections.emptyList();
        }

        List<ExtensionWithDeps> extensionsWithDeps = new ArrayList<>(extensions.size());
        for (HierarchicalConfiguration extensionNode : extensions) {
            String classname = extensionNode.getString(EXTENSION_CLASS_NAME, "");
            if (classname.isEmpty()) {
                malformedFile("a v1 extension has empty \"" + EXTENSION_CLASS_NAME + "\".");
            }

            List<HierarchicalConfiguration> fields =
                    extensionNode.configurationsAt(EXTENSION_DEPENDENCIES);
            if (fields.isEmpty()) {
                // Extension v1 without dependencies, handle as normal extension.
                if (this.extensions.isEmpty()) {
                    // Empty thus Collections.emptyList(), create and use a mutable list.
                    this.extensions = new ArrayList<>(5);
                }
                this.extensions.add(classname);
                continue;
            }

            List<AddOnDep> addOnDeps = readAddOnDependencies(fields);
            AddOnClassnames classnames = readAddOnClassnames(extensionNode);
            extensionsWithDeps.add(new ExtensionWithDeps(classname, addOnDeps, classnames));
        }

        return extensionsWithDeps;
    }

    private AddOnClassnames readAddOnClassnames(HierarchicalConfiguration node) {
        List<String> allowed =
                getStrings(node, CLASSNAMES_ALLOWED_ALL_ELEMENTS, CLASSNAMES_ALLOWED_ELEMENT);
        List<String> restricted =
                getStrings(node, CLASSNAMES_RESTRICTED_ALL_ELEMENTS, CLASSNAMES_RESTRICTED_ELEMENT);
        if (allowed.isEmpty() && restricted.isEmpty()) {
            return AddOnClassnames.ALL_ALLOWED;
        }
        return new AddOnClassnames(allowed, restricted);
    }

    private void malformedFile(String reason) {
        throw new IllegalArgumentException(
                "Add-on \""
                        + name
                        + "\" contains malformed "
                        + AddOn.MANIFEST_FILE_NAME
                        + " file, "
                        + reason);
    }

    public static class Dependencies {

        private final String javaVersion;

        private final List<AddOnDep> addOnDependencies;

        public Dependencies(String javaVersion, List<AddOnDep> addOnDependencies) {
            this.javaVersion = javaVersion;
            this.addOnDependencies = addOnDependencies;
        }

        public String getJavaVersion() {
            return javaVersion;
        }

        public List<AddOnDep> getAddOns() {
            return addOnDependencies;
        }
    }

    public static class AddOnDep {

        private final String id;
        private final int notBeforeVersion;
        private final int notFromVersion;
        private final String version;

        public AddOnDep(String id, String notBeforeVersion, String notFromVersion, String version) {
            this.id = id;
            this.notBeforeVersion = convertToInt(notBeforeVersion, -1, "not-before-version");
            this.notFromVersion = convertToInt(notFromVersion, -1, "not-from-version");

            if (version.isEmpty()) {
                StringBuilder versionMatch = new StringBuilder();
                if (this.notBeforeVersion != -1) {
                    versionMatch.append(" >= ").append(this.notBeforeVersion).append(".0.0");
                }

                if (this.notFromVersion != -1) {
                    if (versionMatch.length() != 0) {
                        versionMatch.append(" &");
                    }
                    versionMatch.append(" < ").append(this.notFromVersion).append(".0.0");
                }
                this.version = versionMatch.toString();
            } else {
                if (!Version.isValidVersionRange(version)) {
                    throw new IllegalArgumentException("Invalid version range: " + version);
                }
                this.version = version;
            }
        }

        private static int convertToInt(String value, int defaultValue, String element) {
            if (value == null || value.isEmpty()) {
                return defaultValue;
            }

            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Expected integer for element \"" + element + "\" but was: " + value);
            }
        }

        public String getId() {
            return id;
        }

        /**
         * Gets the required version of the add-on, might not be an exact version (e.g. {@literal
         * 1.*} or {@literal >= 1.4.0 & < 2.0.0}).
         *
         * @return the required version of the add-on, never {@code null}.
         * @since 2.7.0
         */
        public String getVersion() {
            return version;
        }

        /**
         * Gets the "not before version" of the add-on (e.g. {@literal 2}).
         *
         * @return the "not before version" of the add-on
         * @deprecated (2.7.0) Use {@link #getVersion()} instead.
         */
        @Deprecated
        public int getNotBeforeVersion() {
            return notBeforeVersion;
        }

        /**
         * Gets the "not from version" of the add-on (e.g. {@literal 5}).
         *
         * @return the "not from version" of the add-on
         * @deprecated (2.7.0) Use {@link #getVersion()} instead.
         */
        @Deprecated
        public int getNotFromVersion() {
            return notFromVersion;
        }

        /**
         * Gets the required version of the add-on, might not be an exact version (e.g. {@literal
         * 1.*}).
         *
         * @return the required version of the add-on.
         * @deprecated (2.7.0) Use {@link #getVersion()} instead.
         */
        @Deprecated
        public String getSemVer() {
            return version;
        }
    }

    public static class ExtensionWithDeps {

        private final String classname;
        private final List<AddOnDep> addOnDependencies;
        private final AddOnClassnames addOnClassnames;

        public ExtensionWithDeps(
                String classname,
                List<AddOnDep> addOnDependencies,
                AddOnClassnames addOnClassnames) {
            this.classname = classname;
            this.addOnDependencies = addOnDependencies;
            this.addOnClassnames = addOnClassnames;
        }

        public String getClassname() {
            return classname;
        }

        public List<AddOnDep> getDependencies() {
            return addOnDependencies;
        }

        public AddOnClassnames getAddOnClassnames() {
            return addOnClassnames;
        }
    }
}
