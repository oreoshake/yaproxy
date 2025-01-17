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
package org.yaproxy.yap.view;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.Session;
import org.parosproxy.paros.view.View;
import org.yaproxy.yap.model.Context;

public class ContextIncludePanel extends AbstractContextPropertiesPanel {

    private static final String PANEL_NAME =
            Constant.messages.getString("context.scope.include.title");
    private static final long serialVersionUID = -8337361808959321380L;

    private JPanel panelSession = null;
    private MultipleRegexesOptionsPanel regexesPanel;

    /**
     * Returns the name of the panel "Include in context" for the given {@code contextId}.
     *
     * @param contextId the context index that will be used to create the name of the panel
     * @return the name of the panel "Include in context" for the given {@code contextId}
     * @since 2.2.0
     * @see Context#getId()
     */
    public static String getPanelName(int contextId) {
        // Panel names have to be unique, so precede with the context index
        return contextId + ": " + PANEL_NAME;
    }

    /**
     * Constructs a {@code ContextIncludePanel} for the given context.
     *
     * @param context the target context, must not be {@code null}.
     */
    public ContextIncludePanel(Context context) {
        super(context.getId());

        regexesPanel = new MultipleRegexesOptionsPanel(View.getSingleton().getSessionDialog());

        this.setLayout(new CardLayout());
        this.setName(getPanelName(getContextId()));
        this.add(getPanelSession(), getPanelSession().getName());
    }

    /**
     * This method initializes panelSession
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPanelSession() {
        if (panelSession == null) {

            panelSession = new JPanel();
            panelSession.setLayout(new GridBagLayout());
            panelSession.setName("IncludeInScope");

            java.awt.GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            java.awt.GridBagConstraints gridBagConstraints1 = new GridBagConstraints();

            javax.swing.JLabel jLabel = new JLabel();

            jLabel.setText(Constant.messages.getString("context.label.include"));
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.gridheight = 1;
            gridBagConstraints1.insets = new java.awt.Insets(10, 0, 5, 0);
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.weightx = 0.0D;

            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.weighty = 1.0;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints2.ipadx = 0;
            gridBagConstraints2.insets = new java.awt.Insets(0, 0, 0, 0);
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
            panelSession.add(jLabel, gridBagConstraints1);
            panelSession.add(regexesPanel, gridBagConstraints2);
        }
        return panelSession;
    }

    @Override
    public String getHelpIndex() {
        return "ui.dialogs.contexts";
    }

    @Override
    public void initContextData(Session session, Context uiContext) {
        regexesPanel.setRegexes(uiContext.getIncludeInContextRegexs());
    }

    @Override
    public void validateContextData(Session session) {
        // Nothing to do, the regular expressions are already validated when manually added and
        // regular expressions added programmatically are expected to be valid.
    }

    @Override
    public void saveContextData(Session session) throws Exception {
        Context context = session.getContext(getContextId());
        context.setIncludeInContextRegexs(regexesPanel.getRegexes());
    }

    @Override
    public void saveTemporaryContextData(Context uiSharedContext) {
        uiSharedContext.setIncludeInContextRegexs(regexesPanel.getRegexes());
    }
}
