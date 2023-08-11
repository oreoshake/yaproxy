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
package org.yaproxy.yap.network;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * YAP's {@link ProxySelector}.
 *
 * @since 2.10.0
 * @deprecated (2.12.0) No longer in use, it will be removed in a following version.
 */
@Deprecated
public class YapProxySelector extends ProxySelector {

    private static final ProxySelector DEFAULT_PROXY_SELECTOR = ProxySelector.getDefault();

    private static final YapProxySelector SINGLETON = new YapProxySelector();

    private static final Logger logger = LogManager.getLogger(YapProxySelector.class);

    private YapProxySelector() {}

    /**
     * Gets the singleton.
     *
     * @return the YAP's {@code ProxySelector}.
     */
    public static YapProxySelector getSingleton() {
        return SINGLETON;
    }

    /**
     * The default {@link ProxySelector}, provided by the JRE.
     *
     * @return the default {@code ProxySelector}.
     */
    public static ProxySelector getDefaultProxySelector() {
        return DEFAULT_PROXY_SELECTOR;
    }

    @Override
    public List<Proxy> select(URI uri) {
        List<Proxy> proxies = getDefaultProxySelector().select(uri);
        logger.debug("Selected proxies for {} {}", uri, proxies);
        return proxies;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        logger.debug("Connect failed for {} {}", uri, sa, ioe);

        getDefaultProxySelector().connectFailed(uri, sa, ioe);
    }
}
