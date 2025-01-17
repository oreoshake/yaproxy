/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2014 The YAP Development Team
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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.view.View;
import org.yaproxy.yap.utils.DisplayUtils;
import org.yaproxy.yap.view.SingleColumnTableModel;
import org.yaproxy.yap.view.StandardFieldsDialog;
import org.yaproxy.yap.view.widgets.WritableFileChooser;

@SuppressWarnings("serial")
public class PolicyManagerDialog extends StandardFieldsDialog {

    private static final long serialVersionUID = 1L;

    private JButton addButton = null;
    private JButton modifyButton = null;
    private JButton removeButton = null;
    private JButton importButton = null;
    private JButton exportButton = null;

    private JTable paramsTable = null;
    private SingleColumnTableModel paramsModel = null;

    private ExtensionActiveScan extension;

    private static final Logger LOGGER = LogManager.getLogger(PolicyManagerDialog.class);

    public PolicyManagerDialog(Frame owner) {
        super(owner, "ascan.policymgr.title", DisplayUtils.getScaledDimension(512, 400));
    }

    public void init(ExtensionActiveScan extension) {
        this.extension = extension;

        this.removeAllFields();

        this.getParamsModel().setLines(extension.getPolicyManager().getAllPolicyNames());

        List<JButton> buttons = new ArrayList<>();
        buttons.add(getAddButton());
        buttons.add(getModifyButton());
        buttons.add(getRemoveButton());
        buttons.add(getImportButton());
        buttons.add(getExportButton());

        this.addTableField(this.getParamsTable(), buttons);
    }

    /** Only need one close button */
    @Override
    public boolean hasCancelSaveButtons() {
        return false;
    }

    @Override
    public String getHelpIndex() {
        return "ui.dialogs.scanpolicymgr";
    }

    private JButton getAddButton() {
        if (this.addButton == null) {
            this.addButton = new JButton(Constant.messages.getString("ascan.policymgr.button.add"));
            this.addButton.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                extension.showPolicyDialog(PolicyManagerDialog.this);
                            } catch (ConfigurationException e1) {
                                LOGGER.error(e1.getMessage(), e1);
                            }
                        }
                    });
        }
        return this.addButton;
    }

    private JButton getModifyButton() {
        if (this.modifyButton == null) {
            this.modifyButton =
                    new JButton(Constant.messages.getString("ascan.policymgr.button.modify"));
            this.modifyButton.setEnabled(false);
            this.modifyButton.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String name =
                                    (String)
                                            getParamsModel()
                                                    .getValueAt(
                                                            getParamsTable().getSelectedRow(), 0);
                            if (name != null) {
                                try {
                                    extension.showPolicyDialog(PolicyManagerDialog.this, name);
                                } catch (ConfigurationException e1) {
                                    LOGGER.error(e1.getMessage(), e1);
                                }
                            }
                        }
                    });
        }
        return this.modifyButton;
    }

    private JButton getRemoveButton() {
        if (this.removeButton == null) {
            this.removeButton =
                    new JButton(Constant.messages.getString("ascan.policymgr.button.remove"));
            this.removeButton.setEnabled(false);
            this.removeButton.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String name =
                                    (String)
                                            getParamsModel()
                                                    .getValueAt(
                                                            getParamsTable().getSelectedRow(), 0);
                            if (name != null) {
                                if (View.getSingleton()
                                                .showConfirmDialog(
                                                        PolicyManagerDialog.this,
                                                        Constant.messages.getString(
                                                                "ascan.policymgr.warn.delete"))
                                        == JOptionPane.OK_OPTION) {
                                    extension.getPolicyManager().deletePolicy(name);
                                    policyNamesChanged();
                                }
                            }
                        }
                    });
        }
        return this.removeButton;
    }

    private JButton getImportButton() {
        if (this.importButton == null) {
            this.importButton =
                    new JButton(Constant.messages.getString("ascan.policymgr.button.import"));
            this.importButton.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Default to YAP home dir - we dont want to import/export to the policy
                            // dir
                            JFileChooser chooser = new JFileChooser(Constant.getYapHome());
                            chooser.setFileFilter(
                                    new FileNameExtensionFilter(
                                            Constant.messages.getString("file.format.yap.policy"),
                                            "policy"));
                            File file = null;
                            int rc = chooser.showOpenDialog(View.getSingleton().getMainFrame());
                            if (rc == JFileChooser.APPROVE_OPTION) {
                                file = chooser.getSelectedFile();
                                if (file == null) {
                                    return;
                                }
                                try {
                                    extension.getPolicyManager().importPolicy(file);
                                    policyNamesChanged();
                                } catch (ConfigurationException | IOException e1) {
                                    LOGGER.error(e1.getMessage(), e1);
                                    View.getSingleton()
                                            .showWarningDialog(
                                                    Constant.messages.getString(
                                                            "ascan.policy.load.error"));
                                }
                            }
                        }
                    });
        }
        return this.importButton;
    }

    private JButton getExportButton() {
        if (this.exportButton == null) {
            this.exportButton =
                    new JButton(Constant.messages.getString("ascan.policymgr.button.export"));
            this.exportButton.setEnabled(false);
            this.exportButton.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String name =
                                    (String)
                                            getParamsModel()
                                                    .getValueAt(
                                                            getParamsTable().getSelectedRow(), 0);
                            if (name != null) {
                                JFileChooser chooser =
                                        new WritableFileChooser(Constant.getPoliciesDir());
                                File file =
                                        new File(
                                                Constant.getYapHome(),
                                                name + PolicyManager.POLICY_EXTENSION);
                                chooser.setSelectedFile(file);

                                chooser.setFileFilter(
                                        new FileNameExtensionFilter(
                                                Constant.messages.getString(
                                                        "file.format.yap.policy"),
                                                "policy"));
                                int rc = chooser.showSaveDialog(View.getSingleton().getMainFrame());
                                if (rc == JFileChooser.APPROVE_OPTION) {
                                    file = chooser.getSelectedFile();
                                    if (file == null) {
                                        return;
                                    }
                                    try {
                                        ScanPolicy policy =
                                                extension.getPolicyManager().getPolicy(name);
                                        if (policy != null) {
                                            extension.getPolicyManager().exportPolicy(policy, file);
                                        }
                                    } catch (ConfigurationException e1) {
                                        LOGGER.error(e1.getMessage(), e1);
                                        View.getSingleton()
                                                .showWarningDialog(
                                                        Constant.messages.getString(
                                                                "ascan.policy.load.error"));
                                    }
                                }
                            }
                        }
                    });
        }
        return this.exportButton;
    }

    @Override
    public void save() {}

    @Override
    public String validateFields() {
        return null;
    }

    private SingleColumnTableModel getParamsModel() {
        if (paramsModel == null) {
            paramsModel =
                    new SingleColumnTableModel(
                            Constant.messages.getString("ascan.policymgr.table.policy"));
            paramsModel.setEditable(false);
        }
        return paramsModel;
    }

    private JTable getParamsTable() {
        if (paramsTable == null) {
            paramsTable = new JTable();
            paramsTable.setModel(getParamsModel());
            paramsTable.addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mousePressed(MouseEvent e) {
                            if (e.getClickCount() >= 2) {
                                int row = paramsTable.rowAtPoint(e.getPoint());
                                if (row >= 0) {
                                    String name = (String) getParamsModel().getValueAt(row, 0);
                                    if (name != null) {
                                        try {
                                            extension.showPolicyDialog(
                                                    PolicyManagerDialog.this, name);
                                        } catch (ConfigurationException e1) {
                                            LOGGER.error(e1.getMessage(), e1);
                                        }
                                    }
                                }
                            }
                        }
                    });
            paramsTable
                    .getSelectionModel()
                    .addListSelectionListener(
                            new ListSelectionListener() {
                                @Override
                                public void valueChanged(ListSelectionEvent e) {
                                    if (getParamsTable().getSelectedRowCount() == 0) {
                                        getModifyButton().setEnabled(false);
                                        getRemoveButton().setEnabled(false);
                                        getExportButton().setEnabled(false);
                                    } else if (getParamsTable().getSelectedRowCount() == 1) {
                                        getModifyButton().setEnabled(true);
                                        // Dont let the last policy be removed
                                        getRemoveButton()
                                                .setEnabled(getParamsModel().getRowCount() > 1);
                                        getExportButton().setEnabled(true);
                                    } else {
                                        getModifyButton().setEnabled(false);
                                        getRemoveButton().setEnabled(false);
                                        getExportButton().setEnabled(false);
                                    }
                                }
                            });
        }
        return paramsTable;
    }

    protected void policyNamesChanged() {
        this.getParamsModel().setLines(extension.getPolicyManager().getAllPolicyNames());
    }
}
