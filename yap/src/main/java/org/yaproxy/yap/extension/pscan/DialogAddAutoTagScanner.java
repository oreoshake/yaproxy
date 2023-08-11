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
package org.yaproxy.yap.extension.pscan;

import java.awt.Dialog;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.parosproxy.paros.Constant;
import org.yaproxy.yap.extension.pscan.scanner.RegexAutoTagScanner;
import org.yaproxy.yap.utils.YapTextField;
import org.yaproxy.yap.view.AbstractFormDialog;

@SuppressWarnings("serial")
class DialogAddAutoTagScanner extends AbstractFormDialog {

    private static final long serialVersionUID = -5209887319253495735L;

    private static final String DIALOG_TITLE =
            Constant.messages.getString("pscan.options.dialog.scanner.add.title");

    private static final String CONFIRM_BUTTON_LABEL =
            Constant.messages.getString("pscan.options.dialog.scanner.add.button.confirm");

    private static final String NAME_FIELD_LABEL =
            Constant.messages.getString("pscan.options.dialog.scanner.field.label.name");
    private static final String CONFIGURATION_FIELD_LABEL =
            Constant.messages.getString("pscan.options.dialog.scanner.field.label.config");
    private static final String REQUEST_URL_REGEX_FIELD_LABEL =
            Constant.messages.getString(
                    "pscan.options.dialog.scanner.field.label.editRequestUrlRegex");
    private static final String REQUEST_HEADER_REGEX_FIELD_LABEL =
            Constant.messages.getString(
                    "pscan.options.dialog.scanner.field.label.editRequestHeaderRegex");
    private static final String RESPONSE_HEADER_REGEX_FIELD_LABEL =
            Constant.messages.getString(
                    "pscan.options.dialog.scanner.field.label.editResponseHeaderRegex");
    private static final String RESPONSE_BODY_REGEX_FIELD_LABEL =
            Constant.messages.getString(
                    "pscan.options.dialog.scanner.field.label.editResponseBodyRegex");
    private static final String ENABLED_FIELD_LABEL =
            Constant.messages.getString("pscan.options.dialog.scanner.field.label.enabled");

    private static final String TITLE_DISPLAY_NAME_REPEATED_DIALOG =
            Constant.messages.getString("pscan.options.dialog.scanner.warning.name.repeated.title");
    private static final String TEXT_DISPLAY_NAME_REPEATED_DIALOG =
            Constant.messages.getString("pscan.options.dialog.scanner.warning.name.repeated.text");

    private static final String TITLE_WARNING_INVALID_REGEX =
            Constant.messages.getString("pscan.options.dialog.scanner.warning.invalid.regex.title");
    private static final String MESSAGE_INVALID_REQUEST_HEADER_REGEX =
            Constant.messages.getString(
                    "pscan.options.dialog.scanner.warning.invalid.requestHeaderRegex");
    private static final String MESSAGE_INVALID_REQUEST_URL_REGEX =
            Constant.messages.getString(
                    "pscan.options.dialog.scanner.warning.invalid.requestUrlRegex");
    private static final String MESSAGE_INVALID_RESPONSE_BODY_REGEX =
            Constant.messages.getString(
                    "pscan.options.dialog.scanner.warning.invalid.responseBodyRegex");
    private static final String MESSAGE_INVALID_RESPONSE_HEADER_REGEX =
            Constant.messages.getString(
                    "pscan.options.dialog.scanner.warning.invalid.responseHeaderRegex");

    private YapTextField nameTextField;
    private YapTextField configurationTextField;
    private YapTextField requestUrlRegexTextField;
    private YapTextField requestHeaderRegexTextField;
    private YapTextField responseHeaderRegexTextField;
    private YapTextField responseBodyRegexTextField;
    private JCheckBox enabledCheckBox;

    protected RegexAutoTagScanner scanner;
    private List<RegexAutoTagScanner> scanners;

    private ConfirmButtonValidatorDocListener confirmButtonValidatorDocListener;

    public DialogAddAutoTagScanner(Dialog owner) {
        super(owner, DIALOG_TITLE);
    }

    protected DialogAddAutoTagScanner(Dialog owner, String title) {
        super(owner, title);
    }

    @Override
    protected JPanel getFieldsPanel() {
        JPanel fieldsPanel = new JPanel();

        GroupLayout layout = new GroupLayout(fieldsPanel);
        fieldsPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel nameLabel = new JLabel(NAME_FIELD_LABEL);
        JLabel configurationLabel = new JLabel(CONFIGURATION_FIELD_LABEL);
        JLabel requestUrlRegexLabel = new JLabel(REQUEST_URL_REGEX_FIELD_LABEL);
        JLabel requestHeaderRegexLabel = new JLabel(REQUEST_HEADER_REGEX_FIELD_LABEL);
        JLabel responseHeaderRegexLabel = new JLabel(RESPONSE_HEADER_REGEX_FIELD_LABEL);
        JLabel responseBodyRegexLabel = new JLabel(RESPONSE_BODY_REGEX_FIELD_LABEL);
        JLabel enabledLabel = new JLabel(ENABLED_FIELD_LABEL);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(nameLabel)
                                        .addComponent(configurationLabel)
                                        .addComponent(requestUrlRegexLabel)
                                        .addComponent(requestHeaderRegexLabel)
                                        .addComponent(responseHeaderRegexLabel)
                                        .addComponent(responseBodyRegexLabel)
                                        .addComponent(enabledLabel))
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(getNameTextField())
                                        .addComponent(getConfigurationTextField())
                                        .addComponent(getRequestUrlRegexTextField())
                                        .addComponent(getRequestHeaderRegexTextField())
                                        .addComponent(getResponseHeaderRegexTextField())
                                        .addComponent(getResponseBodyRegexTextField())
                                        .addComponent(getEnabledCheckBox())));

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(nameLabel)
                                        .addComponent(getNameTextField()))
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(configurationLabel)
                                        .addComponent(getConfigurationTextField()))
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(requestUrlRegexLabel)
                                        .addComponent(getRequestUrlRegexTextField()))
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(requestHeaderRegexLabel)
                                        .addComponent(getRequestHeaderRegexTextField()))
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(responseHeaderRegexLabel)
                                        .addComponent(getResponseHeaderRegexTextField()))
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(responseBodyRegexLabel)
                                        .addComponent(getResponseBodyRegexTextField()))
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(enabledLabel)
                                        .addComponent(getEnabledCheckBox())));

        return fieldsPanel;
    }

    @Override
    protected String getConfirmButtonLabel() {
        return CONFIRM_BUTTON_LABEL;
    }

    @Override
    protected void init() {
        getNameTextField().setText("");
        getConfigurationTextField().setText("");
        getRequestUrlRegexTextField().setText("");
        getRequestHeaderRegexTextField().setText("");
        getResponseHeaderRegexTextField().setText("");
        getResponseBodyRegexTextField().setText("");
        getEnabledCheckBox().setSelected(true);
        scanner = null;
    }

    @Override
    protected boolean validateFields() {
        if (!validateName(getNameTextField().getText())) {
            return false;
        }

        if (!validateRegex(
                getRequestHeaderRegexTextField(), MESSAGE_INVALID_REQUEST_HEADER_REGEX)) {
            return false;
        }
        if (!validateRegex(getRequestUrlRegexTextField(), MESSAGE_INVALID_REQUEST_URL_REGEX)) {
            return false;
        }
        if (!validateRegex(
                getResponseHeaderRegexTextField(), MESSAGE_INVALID_RESPONSE_HEADER_REGEX)) {
            return false;
        }
        if (!validateRegex(getResponseBodyRegexTextField(), MESSAGE_INVALID_RESPONSE_BODY_REGEX)) {
            return false;
        }

        return true;
    }

    protected boolean validateName(String name) {
        for (RegexAutoTagScanner s : scanners) {
            if (name.equals(s.getName())) {
                JOptionPane.showMessageDialog(
                        this,
                        TEXT_DISPLAY_NAME_REPEATED_DIALOG,
                        TITLE_DISPLAY_NAME_REPEATED_DIALOG,
                        JOptionPane.INFORMATION_MESSAGE);
                getNameTextField().requestFocusInWindow();
                return false;
            }
        }
        return true;
    }

    private boolean validateRegex(YapTextField regexTextField, String warningMessage) {
        try {
            Pattern.compile(regexTextField.getText(), Pattern.CASE_INSENSITIVE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this, warningMessage, TITLE_WARNING_INVALID_REGEX, JOptionPane.WARNING_MESSAGE);
            regexTextField.requestFocusInWindow();
            return false;
        }
        return true;
    }

    @Override
    protected void performAction() {
        scanner =
                new RegexAutoTagScanner(
                        getNameTextField().getText(),
                        RegexAutoTagScanner.TYPE.TAG,
                        getConfigurationTextField().getText());
        scanner.setRequestHeaderRegex(getRequestHeaderRegexTextField().getText());
        scanner.setRequestUrlRegex(getRequestUrlRegexTextField().getText());
        scanner.setResponseHeaderRegex(getResponseHeaderRegexTextField().getText());
        scanner.setResponseBodyRegex(getResponseBodyRegexTextField().getText());
        scanner.setEnabled(getEnabledCheckBox().isSelected());
    }

    @Override
    protected void clearFields() {
        getNameTextField().setText("");
        getNameTextField().discardAllEdits();

        getConfigurationTextField().setText("");
        getConfigurationTextField().discardAllEdits();

        getRequestUrlRegexTextField().setText("");
        getRequestUrlRegexTextField().discardAllEdits();

        getRequestHeaderRegexTextField().setText("");
        getRequestHeaderRegexTextField().discardAllEdits();

        getResponseHeaderRegexTextField().setText("");
        getResponseHeaderRegexTextField().discardAllEdits();

        getResponseBodyRegexTextField().setText("");
        getResponseBodyRegexTextField().discardAllEdits();
    }

    public RegexAutoTagScanner getScanner() {
        return scanner;
    }

    protected YapTextField getNameTextField() {
        if (nameTextField == null) {
            nameTextField = new YapTextField(25);
            nameTextField.getDocument().addDocumentListener(getConfirmButtonValidatorDocListener());
        }

        return nameTextField;
    }

    protected YapTextField getConfigurationTextField() {
        if (configurationTextField == null) {
            configurationTextField = new YapTextField();
            configurationTextField
                    .getDocument()
                    .addDocumentListener(getConfirmButtonValidatorDocListener());
        }

        return configurationTextField;
    }

    protected YapTextField getRequestUrlRegexTextField() {
        if (requestUrlRegexTextField == null) {
            requestUrlRegexTextField = new YapTextField();
            requestUrlRegexTextField
                    .getDocument()
                    .addDocumentListener(getConfirmButtonValidatorDocListener());
        }

        return requestUrlRegexTextField;
    }

    protected YapTextField getRequestHeaderRegexTextField() {
        if (requestHeaderRegexTextField == null) {
            requestHeaderRegexTextField = new YapTextField();
            requestHeaderRegexTextField
                    .getDocument()
                    .addDocumentListener(getConfirmButtonValidatorDocListener());
        }

        return requestHeaderRegexTextField;
    }

    protected YapTextField getResponseHeaderRegexTextField() {
        if (responseHeaderRegexTextField == null) {
            responseHeaderRegexTextField = new YapTextField();
            responseHeaderRegexTextField
                    .getDocument()
                    .addDocumentListener(getConfirmButtonValidatorDocListener());
        }

        return responseHeaderRegexTextField;
    }

    protected YapTextField getResponseBodyRegexTextField() {
        if (responseBodyRegexTextField == null) {
            responseBodyRegexTextField = new YapTextField();
            responseBodyRegexTextField
                    .getDocument()
                    .addDocumentListener(getConfirmButtonValidatorDocListener());
        }

        return responseBodyRegexTextField;
    }

    protected JCheckBox getEnabledCheckBox() {
        if (enabledCheckBox == null) {
            enabledCheckBox = new JCheckBox();
        }

        return enabledCheckBox;
    }

    public void setScanners(List<RegexAutoTagScanner> scanners) {
        this.scanners = scanners;
    }

    public void clear() {
        this.scanners = null;
        this.scanner = null;
    }

    private ConfirmButtonValidatorDocListener getConfirmButtonValidatorDocListener() {
        if (confirmButtonValidatorDocListener == null) {
            confirmButtonValidatorDocListener = new ConfirmButtonValidatorDocListener();
        }

        return confirmButtonValidatorDocListener;
    }

    private class ConfirmButtonValidatorDocListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            checkAndEnableConfirmButton();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkAndEnableConfirmButton();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            checkAndEnableConfirmButton();
        }

        private void checkAndEnableConfirmButton() {
            boolean enabled =
                    (getNameTextField().getDocument().getLength() > 0)
                            && (getConfigurationTextField().getDocument().getLength() > 0)
                            && (getRequestUrlRegexTextField().getDocument().getLength() > 0
                                    || getRequestHeaderRegexTextField().getDocument().getLength()
                                            > 0
                                    || getResponseHeaderRegexTextField().getDocument().getLength()
                                            > 0
                                    || getResponseBodyRegexTextField().getDocument().getLength()
                                            > 0);
            setConfirmButtonEnabled(enabled);
        }
    }
}
