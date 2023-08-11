/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2020 The YAP Development Team
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
package org.yaproxy.yap.extension.authentication;

import java.util.function.Function;
import org.parosproxy.paros.network.HttpHeader;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpSender;
import org.yaproxy.yap.extension.script.ScriptVars;
import org.yaproxy.yap.network.HttpSenderListener;

class HttpSenderAuthHeaderListener implements HttpSenderListener {

    public static final String YAP_AUTH_HEADER_VALUE = "YAP_AUTH_HEADER_VALUE";
    public static final String YAP_AUTH_HEADER = "YAP_AUTH_HEADER";
    public static final String YAP_AUTH_HEADER_SITE = "YAP_AUTH_HEADER_SITE";

    public HttpSenderAuthHeaderListener(Function<String, String> propertyProvider) {
        String authHeaderValueVar = propertyProvider.apply(YAP_AUTH_HEADER_VALUE);
        if (authHeaderValueVar != null && !authHeaderValueVar.isEmpty()) {
            ScriptVars.setGlobalVar(YAP_AUTH_HEADER_VALUE, authHeaderValueVar);
        }

        String authHeaderVar = propertyProvider.apply(YAP_AUTH_HEADER);
        if (authHeaderVar != null && !authHeaderVar.isEmpty()) {
            ScriptVars.setGlobalVar(YAP_AUTH_HEADER, authHeaderVar);
        } else {
            ScriptVars.setGlobalVar(YAP_AUTH_HEADER, HttpHeader.AUTHORIZATION);
        }

        String authHeaderSiteVar = propertyProvider.apply(YAP_AUTH_HEADER_SITE);
        if (authHeaderSiteVar != null && !authHeaderSiteVar.isEmpty()) {
            ScriptVars.setGlobalVar(YAP_AUTH_HEADER_SITE, authHeaderSiteVar);
        }
    }

    @Override
    public int getListenerOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onHttpRequestSend(HttpMessage msg, int initiator, HttpSender sender) {
        String authHeaderValue = ScriptVars.getGlobalVar(YAP_AUTH_HEADER_VALUE);
        if (authHeaderValue != null) {
            String authHeader = ScriptVars.getGlobalVar(YAP_AUTH_HEADER);
            if (authHeader == null) {
                authHeader = HttpHeader.AUTHORIZATION;
                ScriptVars.setGlobalVar(YAP_AUTH_HEADER, authHeader);
            }
            String authHeaderSite = ScriptVars.getGlobalVar(YAP_AUTH_HEADER_SITE);
            if (authHeaderSite == null
                    || msg.getRequestHeader().getHostName().contains(authHeaderSite)) {
                msg.getRequestHeader().setHeader(authHeader, authHeaderValue);
            }
        }
    }

    @Override
    public void onHttpResponseReceive(HttpMessage msg, int initiator, HttpSender sender) {}
}
