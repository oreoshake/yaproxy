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
package org.yaproxy.yap.extension.spider;

import org.parosproxy.paros.model.Model;
import org.yaproxy.yap.extension.stdmenus.PopupContextTreeMenu;
import org.yaproxy.yap.model.Context;
import org.yaproxy.yap.model.Target;

/**
 * A {@code PopupContextTreeMenu} that allows to show the Spider dialogue for a selected {@link
 * Context}.
 *
 * @see ExtensionSpider#showSpiderDialog(Target)
 * @deprecated (2.12.0) See the spider add-on in yap-extensions instead.
 */
@Deprecated
public class PopupMenuItemSpiderDialogWithContext extends PopupContextTreeMenu {

    private static final long serialVersionUID = 1L;

    public PopupMenuItemSpiderDialogWithContext(ExtensionSpider extension) {
        super(false);

        this.setText(extension.getMessages().getString("spider.custom.popup"));
        this.setIcon(extension.getIcon());

        this.addActionListener(
                e -> {
                    Context context = Model.getSingleton().getSession().getContext(getContextId());
                    extension.showSpiderDialog(new Target(context));
                });
    }
}
