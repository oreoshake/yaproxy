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
package org.yaproxy.yap.view.popup;

import org.parosproxy.paros.Constant;
import org.yaproxy.yap.model.Context;
import org.yaproxy.yap.view.messagecontainer.http.HttpMessageContainer;

/**
 * @since 2.3.0
 */
@SuppressWarnings("serial")
public abstract class PopupMenuItemContext extends PopupMenuItemSiteNodeContainer {

    private static final long serialVersionUID = 2282358266003940700L;

    private Context context;
    private String parentMenu;

    public PopupMenuItemContext() {
        super(Constant.messages.getString("context.new.title"), true);
        this.context = null;
        this.setPrecedeWithSeparator(true);
    }

    public PopupMenuItemContext(Context context, String parentMenu, String name) {
        super(name, true);
        this.context = context;
        this.parentMenu = parentMenu;
    }

    @Override
    public String getParentMenuName() {
        return parentMenu;
    }

    @Override
    public boolean isSubMenu() {
        return true;
    }

    @Override
    protected boolean isEnableForInvoker(
            Invoker invoker, HttpMessageContainer httpMessageContainer) {
        if (invoker == Invoker.SITES_PANEL || invoker == Invoker.HISTORY_PANEL) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSafe() {
        return true;
    }

    protected Context getContext() {
        return this.context;
    }
}
