/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2010 The YAP Development Team
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
package org.yaproxy.yap.extension.history;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.extension.history.ExtensionHistory;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.yaproxy.yap.view.messagecontainer.http.HttpMessageContainer;
import org.yaproxy.yap.view.popup.PopupMenuItemHistoryReferenceContainer;

@SuppressWarnings("serial")
public class PopupMenuNote extends PopupMenuItemHistoryReferenceContainer {

    private static final long serialVersionUID = -5692544221103745600L;

    private static final Logger LOGGER = LogManager.getLogger(PopupMenuNote.class);

    private final ExtensionHistory extension;

    public PopupMenuNote(ExtensionHistory extension) {
        super(Constant.messages.getString("history.note.popup"));

        this.extension = extension;
    }

    @Override
    public boolean isEnableForInvoker(Invoker invoker, HttpMessageContainer httpMessageContainer) {
        return (invoker == Invoker.HISTORY_PANEL);
    }

    @Override
    public void performAction(HistoryReference href) {
        try {
            extension.showNotesAddDialog(href, href.getHttpMessage().getNote());

        } catch (HttpMalformedHeaderException | DatabaseException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean isSafe() {
        return true;
    }
}
