/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2018 The YAP Development Team
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
package org.yaproxy.yap.extension.ascan;

import javax.swing.ImageIcon;
import org.parosproxy.paros.model.Model;
import org.yaproxy.yap.extension.stdmenus.PopupContextTreeMenu;
import org.yaproxy.yap.model.Context;
import org.yaproxy.yap.model.Target;

/**
 * A {@code PopupContextTreeMenu} that allows to show the Active Scan dialogue for a selected {@link
 * Context}.
 *
 * @see ExtensionActiveScan#showCustomScanDialog(Target)
 */
public class PopupMenuActiveScanCustomWithContext extends PopupContextTreeMenu {

    private static final long serialVersionUID = 1L;

    public PopupMenuActiveScanCustomWithContext(ExtensionActiveScan extension) {
        super(false);

        this.setText(extension.getMessages().getString("ascan.custom.popup"));
        this.setIcon(
                new ImageIcon(
                        PopupMenuActiveScanCustomWithContext.class.getResource(
                                "/resource/icon/16/093.png")));

        this.addActionListener(
                e -> {
                    Context context = Model.getSingleton().getSession().getContext(getContextId());
                    extension.showCustomScanDialog(new Target(context));
                });
    }
}
