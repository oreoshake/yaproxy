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
package org.yaproxy.yap.extension.httppanel.view.paramtable;

import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.JComboBox;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.network.HtmlParameter;
import org.yaproxy.yap.extension.httppanel.component.split.request.RequestSplitComponent;
import org.yaproxy.yap.extension.httppanel.view.DefaultHttpPanelViewModel;
import org.yaproxy.yap.extension.httppanel.view.HttpPanelView;
import org.yaproxy.yap.extension.httppanel.view.HttpPanelViewModel;
import org.yaproxy.yap.view.HttpPanelManager;
import org.yaproxy.yap.view.HttpPanelManager.HttpPanelViewFactory;

public class ExtensionHttpPanelRequestQueryCookieTableView extends ExtensionAdaptor {

    public static final String NAME = "ExtensionHttpPanelRequestQueryCookieTableView";

    public ExtensionHttpPanelRequestQueryCookieTableView() {
        super(NAME);
    }

    @Override
    public String getUIName() {
        return Constant.messages.getString("http.panel.view.requestquery.ext.name");
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);
        if (getView() != null) {
            HttpPanelManager.getInstance()
                    .addRequestViewFactory(
                            RequestSplitComponent.NAME,
                            new HttpPanelQueryCookieParamTableViewFactory());
        }
    }

    @Override
    public boolean canUnload() {
        // Do not allow the unload until moved to an add-on.
        return false;
    }

    @Override
    public void unload() {
        if (getView() != null) {
            HttpPanelManager panelManager = HttpPanelManager.getInstance();
            panelManager.removeRequestViewFactory(
                    RequestSplitComponent.NAME, HttpPanelQueryCookieParamTableViewFactory.NAME);
            panelManager.removeRequestViews(
                    RequestSplitComponent.NAME,
                    HttpPanelQueryCookieParamTableView.NAME,
                    RequestSplitComponent.ViewComponent.HEADER);
        }
    }

    private static final class HttpPanelQueryCookieParamTableViewFactory
            implements HttpPanelViewFactory {

        public static final String NAME = "HttpPanelQueryCookieParamTableViewFactory";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public HttpPanelView getNewView() {
            return new HttpPanelQueryCookieParamTableView(
                    new DefaultHttpPanelViewModel(), new HttpPanelQueryCookieParamTableModel());
        }

        @Override
        public Object getOptions() {
            return RequestSplitComponent.ViewComponent.HEADER;
        }
    }

    private static class HttpPanelQueryCookieParamTableView extends HttpPanelParamTableView {

        public HttpPanelQueryCookieParamTableView(
                HttpPanelViewModel model, HttpPanelParamTableModel tableModel) {
            super(model, tableModel);
        }

        @Override
        public String getTargetViewName() {
            return "";
        }

        @Override
        public JComboBox<HtmlParameter.Type> getComboBoxTypes() {
            JComboBox<HtmlParameter.Type> comboBoxTypes = new JComboBox<>();

            comboBoxTypes.addItem(HtmlParameter.Type.url);
            comboBoxTypes.addItem(HtmlParameter.Type.cookie);

            return comboBoxTypes;
        }
    }

    private static class HttpPanelQueryCookieParamTableModel extends HttpPanelParamTableModel {

        private static final long serialVersionUID = 869819957109403800L;

        @Override
        protected void loadAllParams() {
            allParams.addAll(httpMessage.getUrlParams());
            allParams.addAll(httpMessage.getRequestHeader().getCookieParams());
        }

        @Override
        public void saveAllParams() {
            TreeSet<HtmlParameter> get = new TreeSet<>();
            TreeSet<HtmlParameter> cookies = new TreeSet<>();

            Iterator<HtmlParameter> it = allParams.iterator();
            while (it.hasNext()) {
                HtmlParameter htmlParameter = it.next();
                if (!htmlParameter.getName().isEmpty()) {
                    switch (htmlParameter.getType()) {
                        case url:
                            get.add(htmlParameter);
                            break;
                        case cookie:
                            cookies.add(htmlParameter);
                            break;
                        case form:
                            break;
                        default:
                            break;
                    }
                }
            }

            httpMessage.setGetParams(get);
            httpMessage.setCookieParams(cookies);
        }

        @Override
        protected HtmlParameter getDefaultHtmlParameter() {
            return new HtmlParameter(HtmlParameter.Type.url, "", "");
        }
    }

    @Override
    public String getAuthor() {
        return Constant.YAP_TEAM;
    }

    /** No database tables used, so all supported */
    @Override
    public boolean supportsDb(String type) {
        return true;
    }
}
