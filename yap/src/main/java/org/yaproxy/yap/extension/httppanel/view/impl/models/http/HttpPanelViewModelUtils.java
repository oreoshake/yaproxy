/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2013 The YAP Development Team
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
package org.yaproxy.yap.extension.httppanel.view.impl.models.http;

import org.parosproxy.paros.network.HttpMessage;

public final class HttpPanelViewModelUtils {

    private HttpPanelViewModelUtils() {}

    public static void updateRequestContentLength(HttpMessage message) {
        message.getRequestHeader().setContentLength(message.getRequestBody().length());
    }

    public static void updateResponseContentLength(HttpMessage message) {
        message.getResponseHeader().setContentLength(message.getResponseBody().length());
    }

    /**
     * Finds the HTTP header limit, that is the separator ({@code CRLFCRLF}) between the header and
     * the body.
     *
     * @param data the data that contains the header and body.
     * @return the position after the limit, or {@code -1} if not found.
     * @since 2.10.0
     */
    public static int findHeaderLimit(byte[] data) {
        boolean lastIsCrLf = false;
        boolean lastIsCr = false;
        boolean lastIsLf = false;

        for (int i = 0; i < data.length; ++i) {
            if (!lastIsCr && data[i] == '\r') {
                lastIsCr = true;
                lastIsLf = false;
            } else if (!lastIsLf && data[i] == '\n') {
                if (lastIsCrLf) {
                    return i + 1;
                }

                lastIsCrLf = true;
                lastIsCr = false;
                lastIsLf = true;
            } else {
                lastIsCr = false;
                lastIsLf = false;
                lastIsCrLf = false;
            }
        }

        return -1;
    }
}
