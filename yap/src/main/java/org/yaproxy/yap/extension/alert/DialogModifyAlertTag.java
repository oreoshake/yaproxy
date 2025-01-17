/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2021 The YAP Development Team
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
package org.yaproxy.yap.extension.alert;

import java.awt.Dialog;
import org.parosproxy.paros.Constant;

public class DialogModifyAlertTag extends DialogAddAlertTag {

    private static final long serialVersionUID = 1L;

    private static final String DIALOG_TITLE =
            Constant.messages.getString("alert.tags.dialog.modify.title");
    private static final String CONFIRM_BUTTON_LABEL =
            Constant.messages.getString("alert.tags.dialog.modify.button.confirm");

    private int tagRow;

    public DialogModifyAlertTag(Dialog owner, AlertTagsTableModel model) {
        super(owner, model, DIALOG_TITLE);
    }

    public void setTagRowInAlertTagsTable(int row) {
        this.tagRow = row;
    }

    @Override
    protected String getConfirmButtonLabel() {
        return CONFIRM_BUTTON_LABEL;
    }

    @Override
    protected void init() {
        getKeyTextField().setText(model.getValueAt(tagRow, 0).toString());
        getValueTextArea().setText(model.getValueAt(tagRow, 1).toString());
    }

    @Override
    protected boolean validateKey() {
        String originalKey = model.getValueAt(tagRow, 0).toString();
        if (getKeyTextField().getText().equals(originalKey)) {
            return true;
        }
        return super.validateKey();
    }

    @Override
    protected void performAction() {
        model.setValueAt(getKeyTextField().getText(), tagRow, 0);
        model.setValueAt(getValueTextArea().getText(), tagRow, 1);
    }
}
