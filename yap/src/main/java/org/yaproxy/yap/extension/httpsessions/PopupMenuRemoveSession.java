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
package org.yaproxy.yap.extension.httpsessions;

import java.awt.Component;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;

/**
 * The PopupMenuRemoveSession is used to delete a http session from the {@link
 * ExtensionHttpSessions} .
 */
@SuppressWarnings("serial")
public class PopupMenuRemoveSession extends ExtensionPopupMenuItem {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The extension. */
    private ExtensionHttpSessions extension;

    /** Instantiates a new popup menu used to delete a session. */
    public PopupMenuRemoveSession() {
        super(Constant.messages.getString("httpsessions.popup.session.remove"));
        initialize();
    }

    /**
     * Sets the extension.
     *
     * @param extension the new extension
     */
    public void setExtension(ExtensionHttpSessions extension) {
        this.extension = extension;
    }

    /** Initialize the popup menu. */
    private void initialize() {
        this.addActionListener(
                new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        // Get the HttpSessionsSite
                        HttpSessionsPanel panel = extension.getHttpSessionsPanel();
                        HttpSessionsSite site = panel.getCurrentHttpSessionSite();
                        if (site == null) return;

                        // Get the selected session, delete it and trigger a table repaint
                        HttpSession item = panel.getSelectedSession();
                        site.removeHttpSession(item);
                    }
                });
    }

    @Override
    public boolean isEnableForComponent(Component invoker) {
        if (HttpSessionsPanel.PANEL_NAME.equals(invoker.getName())) {
            setEnabled(extension.getHttpSessionsPanel().getSelectedSession() != null);
            return true;
        }
        return false;
    }

    @Override
    public boolean isSafe() {
        return true;
    }
}
