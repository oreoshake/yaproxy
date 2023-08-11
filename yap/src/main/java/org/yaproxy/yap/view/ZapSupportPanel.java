/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2017 The YAP Development Team
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.yaproxy.yap.utils.YapSupportUtils;

public class YapSupportPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextArea supportDetailsTextArea = new JTextArea(20, 0);

    /** Constructs an {@code YapSupportPanel}. */
    public YapSupportPanel() {
        super(new GridBagLayout(), true);

        GridBagConstraints gbcSupportDetails = new GridBagConstraints();

        supportDetailsTextArea.setEditable(false);

        supportDetailsTextArea.setText(YapSupportUtils.getAll(true));
        supportDetailsTextArea.setCaretPosition(0);

        gbcSupportDetails.gridx = 3;
        gbcSupportDetails.gridy = 0;
        gbcSupportDetails.ipadx = 0;
        gbcSupportDetails.ipady = 0;
        gbcSupportDetails.weightx = 1.0D;
        gbcSupportDetails.weighty = 1.0D;
        gbcSupportDetails.fill = GridBagConstraints.BOTH;
        gbcSupportDetails.anchor = GridBagConstraints.NORTHWEST;
        gbcSupportDetails.insets = new Insets(2, 2, 2, 2);

        this.add(new JScrollPane(supportDetailsTextArea), gbcSupportDetails);
    }

    public String getSupportInfo() {
        return supportDetailsTextArea.getText();
    }
}
