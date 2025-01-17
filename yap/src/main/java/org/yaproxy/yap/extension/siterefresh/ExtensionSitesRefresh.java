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
package org.yaproxy.yap.extension.siterefresh;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;

public class ExtensionSitesRefresh extends ExtensionAdaptor {

    private static final String NAME = "ExtensionSitesRefresh";

    private PopupMenuSitesRefresh popupMenuSitesRefresh = null;

    public ExtensionSitesRefresh() {
        super(NAME);
        this.setOrder(1000); // Want this to be as low as possible :)
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);
        if (getView() != null) {
            extensionHook.getHookMenu().addPopupMenuItem(getPopupMenuSitesRefresh());
        }
    }

    private PopupMenuSitesRefresh getPopupMenuSitesRefresh() {
        if (popupMenuSitesRefresh == null) {
            popupMenuSitesRefresh = new PopupMenuSitesRefresh();
        }
        return popupMenuSitesRefresh;
    }

    @Override
    public String getUIName() {
        return Constant.messages.getString("siterefresh.name");
    }

    @Override
    public String getAuthor() {
        return Constant.YAP_TEAM;
    }

    @Override
    public String getDescription() {
        return Constant.messages.getString("siterefresh.desc");
    }

    /** No database tables used, so all supported */
    @Override
    public boolean supportsDb(String type) {
        return true;
    }
}
