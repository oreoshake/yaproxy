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
package org.yaproxy.yap.network;

import java.io.IOException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PutMethod;

/**
 * An HTTP PUT method implementation that ignores malformed HTTP response header lines.
 *
 * @see PutMethod
 * @deprecated (2.12.0) Implementation details, do not use.
 */
@Deprecated
public class YapPutMethod extends PutMethod {

    public YapPutMethod() {
        super();
    }

    public YapPutMethod(String uri) {
        super(uri);
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Note:</strong> Malformed HTTP header lines are ignored (instead of throwing an
     * exception).
     */
    /*
     * Implementation copied from HttpMethodBase#readResponseHeaders(HttpState, HttpConnection) but changed to use a custom
     * header parser (YapHttpParser#parseHeaders(InputStream, String)).
     */
    @Override
    protected void readResponseHeaders(
            HttpState state, org.apache.commons.httpclient.HttpConnection conn) throws IOException {
        getResponseHeaderGroup().clear();

        Header[] headers =
                YapHttpParser.parseHeaders(
                        conn.getResponseInputStream(), getParams().getHttpElementCharset());
        // Wire logging moved to HttpParser
        getResponseHeaderGroup().setHeaders(headers);
    }
}
