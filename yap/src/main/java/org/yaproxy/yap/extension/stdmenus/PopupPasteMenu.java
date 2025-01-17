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
package org.yaproxy.yap.extension.stdmenus;

import java.awt.Component;
import javax.swing.text.JTextComponent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;

public class PopupPasteMenu extends ExtensionPopupMenuItem {
    private static final long serialVersionUID = 1L;
    private JTextComponent lastInvoker = null;

    public JTextComponent getLastInvoker() {
        return lastInvoker;
    }

    public PopupPasteMenu() {
        super();
        initialize();
    }

    private void initialize() {
        this.setText(Constant.messages.getString("paste.paste.popup"));
    }

    @Override
    public boolean isEnableForComponent(Component invoker) {
        if (invoker instanceof JTextComponent && !(invoker instanceof RSyntaxTextArea)) {
            this.lastInvoker = (JTextComponent) invoker;
            this.setEnabled(((JTextComponent) invoker).isEditable());

            return true;
        }

        this.lastInvoker = null;
        return false;
    }

    @Override
    public boolean isSafe() {
        return true;
    }
}
