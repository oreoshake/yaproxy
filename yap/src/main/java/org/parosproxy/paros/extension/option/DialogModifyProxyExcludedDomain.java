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
package org.parosproxy.paros.extension.option;

import java.awt.Dialog;
import org.parosproxy.paros.Constant;
import org.yaproxy.yap.network.DomainMatcher;

/**
 * @deprecated (2.12.0) No longer in use.
 */
@Deprecated
class DialogModifyProxyExcludedDomain extends DialogAddProxyExcludedDomain {

    private static final long serialVersionUID = -4031122965844883255L;

    private static final String DIALOG_TITLE =
            Constant.messages.getString("conn.options.proxy.excluded.domain.modify.title");

    private static final String CONFIRM_BUTTON_LABEL =
            Constant.messages.getString("conn.options.proxy.excluded.domain.modify.button.confirm");

    protected DialogModifyProxyExcludedDomain(Dialog owner) {
        super(owner, DIALOG_TITLE);
    }

    @Override
    protected String getConfirmButtonLabel() {
        return CONFIRM_BUTTON_LABEL;
    }

    public void setProxyExcludedDomain(DomainMatcher excludedDomain) {
        this.proxyExcludedDomain = excludedDomain;
    }

    @Override
    protected void init() {
        getDomainTextField().setText(proxyExcludedDomain.getValue());
        getDomainTextField().discardAllEdits();

        getRegexCheckBox().setSelected(proxyExcludedDomain.isRegex());

        getEnabledCheckBox().setSelected(proxyExcludedDomain.isEnabled());
    }
}
