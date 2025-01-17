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

import java.util.List;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.view.View;
import org.yaproxy.yap.utils.DesktopUtils;
import org.yaproxy.yap.view.messagecontainer.http.HttpMessageContainer;
import org.yaproxy.yap.view.popup.PopupMenuItemHttpMessageContainer;

public class PopupMenuOpenUrlInBrowser extends PopupMenuItemHttpMessageContainer {

    private static final long serialVersionUID = 1L;
    private boolean disabledToolTipSet = false;

    /**
     * @param label
     */
    public PopupMenuOpenUrlInBrowser(String label) {
        super(label);
    }

    @Override
    public void performAction(HttpMessage msg) {
        if (!DesktopUtils.openUrlInBrowser(msg.getRequestHeader().getURI())) {
            View.getSingleton()
                    .showWarningDialog(Constant.messages.getString("history.browser.warning"));
        }
    }

    @Override
    protected boolean isButtonEnabledForSelectedMessages(List<HttpMessage> httpMessages) {
        if (Constant.isInContainer()
                && !Model.getSingleton()
                        .getOptionsParam()
                        .getViewParam()
                        .isAllowAppIntegrationInContainers()) {
            if (!disabledToolTipSet) {
                this.setToolTipText(Constant.messages.getString("history.browser.disabled"));
                disabledToolTipSet = true;
            }
            return false;
        }
        if (disabledToolTipSet) {
            this.setToolTipText("");
            disabledToolTipSet = false;
        }
        return super.isButtonEnabledForSelectedMessages(httpMessages);
    }

    @Override
    public boolean isEnableForInvoker(Invoker invoker, HttpMessageContainer httpMessageContainer) {
        return DesktopUtils.canOpenUrlInBrowser();
    }

    @Override
    public boolean isSafe() {
        return true;
    }
}
