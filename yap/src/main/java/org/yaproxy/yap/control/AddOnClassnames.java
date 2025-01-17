/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2015 The YAP Development Team
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A class that allows to query if a classname can be loaded.
 *
 * <p>The classnames can be allowed or restricted.
 *
 * @since 2.4.3
 */
final class AddOnClassnames {

    public static final AddOnClassnames ALL_ALLOWED =
            new AddOnClassnames(Collections.<String>emptyList(), Collections.<String>emptyList());

    private final List<String> allowedClassnames;
    private final List<String> restrictedClassnames;

    public AddOnClassnames(List<String> allowedClassnames, List<String> restrictedClassnames) {
        if (allowedClassnames == null) {
            throw new IllegalArgumentException("Parameter allowedClassnames must not be null.");
        }
        if (restrictedClassnames == null) {
            throw new IllegalArgumentException("Parameter restrictedClassnames must not be null.");
        }

        this.allowedClassnames = allowedClassnames;
        this.restrictedClassnames = restrictedClassnames;
    }

    List<String> getAllowedClassnames() {
        return allowedClassnames;
    }

    List<String> getRestrictedClassnames() {
        return restrictedClassnames;
    }

    /**
     * Tells whether or not the given classname is allowed, thus can be loaded.
     *
     * @param classname the classname to check.
     * @return {@code true} if the given classname is allowed, {@code false} otherwise.
     * @throws NullPointerException if the {@code classname} is {@code null}.
     */
    public boolean isAllowed(String classname) {
        Objects.requireNonNull(classname);

        if (!restrictedClassnames.isEmpty()) {
            for (String restrictedClassname : restrictedClassnames) {
                if (classname.startsWith(restrictedClassname)) {
                    return false;
                }
            }
        }
        if (!allowedClassnames.isEmpty()) {
            for (String allowedClassname : allowedClassnames) {
                if (classname.startsWith(allowedClassname)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
