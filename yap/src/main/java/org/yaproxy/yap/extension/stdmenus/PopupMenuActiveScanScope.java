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
package org.yaproxy.yap.extension.stdmenus;

import javax.swing.ImageIcon;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.model.SiteNode;
import org.yaproxy.yap.extension.ascan.ExtensionActiveScan;
import org.yaproxy.yap.view.messagecontainer.http.HttpMessageContainer;
import org.yaproxy.yap.view.popup.PopupMenuItemSiteNodeContainer;

@SuppressWarnings("serial")
public class PopupMenuActiveScanScope extends PopupMenuItemSiteNodeContainer {

    private static final long serialVersionUID = 1L;
    private ExtensionActiveScan extension = null;

    /**
     * @param label
     */
    public PopupMenuActiveScanScope(String label) {
        super(label);
        this.setIcon(
                new ImageIcon(
                        PopupMenuActiveScanScope.class.getResource("/resource/icon/16/093.png")));
    }

    private ExtensionActiveScan getExtensionActiveScan() {
        if (extension == null) {
            extension =
                    Control.getSingleton()
                            .getExtensionLoader()
                            .getExtension(ExtensionActiveScan.class);
        }
        return extension;
    }

    @Override
    public boolean isSubMenu() {
        return true;
    }

    @Override
    public String getParentMenuName() {
        return Constant.messages.getString("attack.site.popup");
    }

    @Override
    public int getParentMenuIndex() {
        return ATTACK_MENU_INDEX;
    }

    @Override
    public void performAction(SiteNode node) {
        extension.startScanAllInScope();
    }

    @Override
    public boolean isEnableForInvoker(Invoker invoker, HttpMessageContainer httpMessageContainer) {
        if (getExtensionActiveScan() == null) {
            return false;
        }
        switch (invoker) {
            case ALERTS_PANEL:
            case ACTIVE_SCANNER_PANEL:
            case FORCED_BROWSE_PANEL:
            case FUZZER_PANEL:
                return false;
            case HISTORY_PANEL:
            case SITES_PANEL:
            case SEARCH_PANEL:
            default:
                return true;
        }
    }
}
