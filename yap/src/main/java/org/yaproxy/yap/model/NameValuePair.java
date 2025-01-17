/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2016 The YAP Development Team
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

/**
 * A name/value pair.
 *
 * <p>How {@code null} name and/or value are handled is at the discretion of implementations, for
 * example, some implementations might choose to require a name, but not a value (thus being {@code
 * null}).
 *
 * @since 2.5.0
 */
public interface NameValuePair {

    /**
     * Gets the name of the name/value pair.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the value of the name/value pair.
     *
     * @return the value
     */
    String getValue();
}
