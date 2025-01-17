/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2011 The YAP Development Team
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
package org.yaproxy.yap.extension.params;

import java.awt.Component;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;

@SuppressWarnings("serial")
public class PopupMenuParamSearch extends ExtensionPopupMenuItem {

    private static final long serialVersionUID = 1L;

    private ExtensionParams extension;

    public PopupMenuParamSearch() {
        super(Constant.messages.getString("params.search.popup"));
        this.addActionListener(
                new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        extension.searchForSelectedParam();
                    }
                });
    }

    public void setExtension(ExtensionParams extension) {
        this.extension = extension;
    }

    @Override
    public boolean isEnableForComponent(Component invoker) {
        if (invoker.getName() != null && invoker.getName().equals(ParamsPanel.PANEL_NAME)) {
            this.setEnabled(extension.getParamsPanel().isOnlyOneParamSelected());
            return true;
        }
        return false;
    }

    @Override
    public boolean isSafe() {
        return true;
    }
}
