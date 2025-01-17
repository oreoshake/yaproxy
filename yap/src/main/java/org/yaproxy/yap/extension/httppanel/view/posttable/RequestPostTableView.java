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
package org.yaproxy.yap.extension.httppanel.view.posttable;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.apache.commons.configuration.FileConfiguration;
import org.parosproxy.paros.Constant;
import org.yaproxy.yap.extension.httppanel.Message;
import org.yaproxy.yap.extension.httppanel.view.AbstractStringHttpPanelViewModel;
import org.yaproxy.yap.extension.httppanel.view.HttpPanelView;
import org.yaproxy.yap.extension.httppanel.view.HttpPanelViewModel;
import org.yaproxy.yap.extension.httppanel.view.HttpPanelViewModelEvent;
import org.yaproxy.yap.extension.httppanel.view.HttpPanelViewModelListener;
import org.yaproxy.yap.utils.DisplayUtils;

public class RequestPostTableView implements HttpPanelView, HttpPanelViewModelListener {

    public static final String NAME = "RequestPostTableView";

    private static final String CAPTION_NAME =
            Constant.messages.getString("http.panel.view.table.name");

    private JTable tableBody;
    private JPanel mainPanel;
    private RequestPostTableModel httpPanelTabularModel;
    private AbstractStringHttpPanelViewModel model;

    public RequestPostTableView(AbstractStringHttpPanelViewModel modelTextBody) {
        httpPanelTabularModel = new RequestPostTableModel();
        httpPanelTabularModel.setEditable(false);

        init();

        this.model = modelTextBody;
        this.model.addHttpPanelViewModelListener(this);
    }

    private void init() {
        // Table
        tableBody = new JTable();
        tableBody.setName("");
        tableBody.setModel(httpPanelTabularModel);

        tableBody.setGridColor(java.awt.Color.gray);
        tableBody.setIntercellSpacing(new java.awt.Dimension(1, 1));
        tableBody.setRowHeight(DisplayUtils.getScaledSize(18));

        // Issue 954: Force the JTable cell to auto-save when the focus changes.
        // Example, edit cell, click OK for a panel dialog box, the data will get saved.
        tableBody.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(tableBody), BorderLayout.CENTER);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCaptionName() {
        return CAPTION_NAME;
    }

    @Override
    public String getTargetViewName() {
        return "";
    }

    @Override
    public int getPosition() {
        return 10;
    }

    @Override
    public boolean hasChanged() {
        return httpPanelTabularModel.hasChanged();
    }

    @Override
    public JComponent getPane() {
        return mainPanel;
    }

    @Override
    public boolean isEditable() {
        return httpPanelTabularModel.isEditable();
    }

    @Override
    public void setEditable(boolean editable) {
        httpPanelTabularModel.setEditable(editable);
    }

    @Override
    public boolean isEnabled(Message msg) {
        return true;
    }

    @Override
    public void dataChanged(HttpPanelViewModelEvent e) {
        httpPanelTabularModel.setText(model.getData());
    }

    @Override
    public void save() {
        String data = httpPanelTabularModel.getText();
        if (data != null) {
            model.setData(data);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        if (selected) {
            tableBody.requestFocusInWindow();
        }
    }

    @Override
    public HttpPanelViewModel getModel() {
        return model;
    }

    @Override
    public void setParentConfigurationKey(String configurationKey) {}

    @Override
    public void loadConfiguration(FileConfiguration configuration) {}

    @Override
    public void saveConfiguration(FileConfiguration configuration) {}
}
