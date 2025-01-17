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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;
import org.parosproxy.paros.model.HistoryReference;
import org.yaproxy.yap.view.popup.PopupMenuItemHistoryReferenceContainer;

/**
 * @deprecated (2.12.0) see the exim add-on
 */
@Deprecated
public class PopupMenuCopyUrls extends PopupMenuItemHistoryReferenceContainer
        implements ClipboardOwner {

    private static final long serialVersionUID = 1L;

    /**
     * @param label
     */
    public PopupMenuCopyUrls(String label) {
        super(label, true);
    }

    @Override
    public void performAction(HistoryReference href) {}

    @Override
    protected void performHistoryReferenceActions(List<HistoryReference> hrefs) {
        StringBuilder sb = new StringBuilder();
        for (HistoryReference href : hrefs) {
            sb.append(href.getURI().toString());
            sb.append("\n");
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(sb.toString()), this);
    }

    @Override
    protected boolean isButtonEnabledForHistoryReference(HistoryReference historyReference) {
        return true;
    }

    @Override
    public boolean isSafe() {
        return true;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // Ignore
    }
}
