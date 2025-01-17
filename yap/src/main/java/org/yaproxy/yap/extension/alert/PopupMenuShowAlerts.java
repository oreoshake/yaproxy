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
package org.yaproxy.yap.extension.alert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.httpclient.URI;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.model.HistoryReference;
import org.yaproxy.yap.view.messagecontainer.http.HttpMessageContainer;
import org.yaproxy.yap.view.popup.ExtensionPopupMenuComponent;
import org.yaproxy.yap.view.popup.PopupMenuHistoryReferenceContainer;

@SuppressWarnings("serial")
public class PopupMenuShowAlerts extends PopupMenuHistoryReferenceContainer {

    private static final long serialVersionUID = 1L;

    private final ExtensionAlert extension;

    /**
     * Constructs a {@code PopupMenuShowAlerts} with the given label.
     *
     * @param label the text shown in the pop up menu
     * @param extension the {@code ExtensionAlert} to show the Edit Alert dialogue.
     */
    public PopupMenuShowAlerts(String label, ExtensionAlert extension) {
        super(label);
        this.extension = extension;
        setProcessExtensionPopupChildren(false);
    }

    @Override
    public boolean isEnableForInvoker(Invoker invoker, HttpMessageContainer httpMessageContainer) {
        switch (invoker) {
            case SITES_PANEL:
            case SPIDER_PANEL:
            case HISTORY_PANEL:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isButtonEnabledForHistoryReference(HistoryReference href) {
        List<Alert> alerts;
        if (href.getSiteNode() != null) {
            alerts = href.getSiteNode().getAlerts();
        } else {
            alerts = href.getAlerts();
        }
        URI hrefURI = href.getURI();
        List<PopupMenuShowAlert> alertList = new ArrayList<>(alerts.size());
        for (Alert alert : alerts) {
            // Just show ones for this node
            if (hrefURI != null && !alert.getUri().equals(hrefURI.toString())) {
                continue;
            }
            final PopupMenuShowAlert menuItem =
                    new PopupMenuShowAlert(alert.getName(), extension, alert);
            menuItem.setIcon(alert.getIcon());

            alertList.add(menuItem);
        }
        Collections.sort(alertList);

        for (PopupMenuShowAlert pmsa : alertList) {
            this.add(pmsa);
        }

        return (alertList.size() > 0);
    }

    @Override
    public void dismissed(ExtensionPopupMenuComponent selectedMenuComponent) {
        if (getMenuComponentCount() > 0) {
            removeAll();
        }
    }

    @Override
    public boolean isSafe() {
        return true;
    }
}
