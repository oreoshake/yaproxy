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
package org.yaproxy.yap.extension.httppanel.component.all.request;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.parosproxy.paros.network.HttpMessage;
import org.yaproxy.yap.extension.httppanel.view.impl.models.http.request.RequestStringHttpPanelViewModel;
import org.yaproxy.yap.extension.httppanel.view.text.HttpPanelTextArea;
import org.yaproxy.yap.extension.httppanel.view.text.HttpPanelTextView;
import org.yaproxy.yap.extension.httppanel.view.util.HttpTextViewUtils;
import org.yaproxy.yap.extension.search.SearchMatch;

public class HttpRequestAllPanelTextView extends HttpPanelTextView {

    public HttpRequestAllPanelTextView(RequestStringHttpPanelViewModel model) {
        super(model);
    }

    @Override
    protected HttpPanelTextArea createHttpPanelTextArea() {
        return new HttpRequestAllPanelTextArea();
    }

    protected static class HttpRequestAllPanelTextArea extends HttpPanelTextArea {

        private static final long serialVersionUID = 6236551060576387786L;

        @Override
        public void search(Pattern p, List<SearchMatch> matches) {
            String header = ((HttpMessage) getMessage()).getRequestHeader().toString();

            Matcher m = p.matcher(getText());
            while (m.find()) {
                int[] position =
                        HttpTextViewUtils.getViewToHeaderBodyPosition(
                                this, header, m.start(), m.end());
                if (position.length == 0) {
                    return;
                }

                SearchMatch.Location location =
                        position.length == 2
                                ? SearchMatch.Location.REQUEST_HEAD
                                : SearchMatch.Location.REQUEST_BODY;
                matches.add(new SearchMatch(location, position[0], position[1]));
            }
        }

        @Override
        public void highlight(SearchMatch sm) {
            if (!(SearchMatch.Location.REQUEST_HEAD.equals(sm.getLocation())
                    || SearchMatch.Location.REQUEST_BODY.equals(sm.getLocation()))) {
                return;
            }

            int[] pos;
            if (SearchMatch.Location.REQUEST_HEAD.equals(sm.getLocation())) {
                pos =
                        HttpTextViewUtils.getHeaderToViewPosition(
                                this,
                                sm.getMessage().getRequestHeader().toString(),
                                sm.getStart(),
                                sm.getEnd());
            } else {
                pos =
                        HttpTextViewUtils.getBodyToViewPosition(
                                this,
                                sm.getMessage().getRequestHeader().toString(),
                                sm.getStart(),
                                sm.getEnd());
            }

            if (pos.length == 0) {
                return;
            }

            highlight(pos[0], pos[1]);
        }
    }
}
