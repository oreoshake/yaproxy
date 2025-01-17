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
package org.yaproxy.yap.model;

public interface MessageLocationConsumer {

    /**
     * If this replacer handles the given type of location, for example, when using a AMF replacer
     * it would not handle other locations than AMFLocation and a "text" replacer would not handle
     * AMF locations
     *
     * @param location the location being checked
     * @return {@code true} if the location is supported, {@code false} otherwise
     */
    boolean supports(MessageLocation location);

    /**
     * If this replacer handles the given type of location, for example, when using a AMF replacer
     * it would not handle other locations than AMFLocation and a "text" replacer would not handle
     * AMF locations
     *
     * @param classLocation the class of the location being checked
     * @return {@code true} if the location is supported, {@code false} otherwise
     */
    boolean supports(Class<? extends MessageLocation> classLocation);
}
