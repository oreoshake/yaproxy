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
package org.yaproxy.yap.extension.httppanel.component.split.response;

import org.yaproxy.yap.extension.httppanel.Message;
import org.yaproxy.yap.extension.httppanel.component.split.request.RequestSplitComponent;
import org.yaproxy.yap.extension.httppanel.view.impl.models.http.response.ResponseBodyStringHttpPanelViewModel;
import org.yaproxy.yap.extension.httppanel.view.impl.models.http.response.ResponseHeaderStringHttpPanelViewModel;
import org.yaproxy.yap.extension.httppanel.view.text.HttpPanelTextView;

/*
 * ResponseSplitComponent is identical to RequestSplitComponent
 */

public class ResponseSplitComponent<T extends Message> extends RequestSplitComponent<T> {

    public static final String NAME = "ResponseSplit";

    public ResponseSplitComponent() {}

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected HttpPanelTextView createHttpPanelHeaderTextView() {
        return new HttpResponseHeaderPanelTextView(new ResponseHeaderStringHttpPanelViewModel());
    }

    @Override
    protected void initViews() {
        bodyViews.addView(
                new HttpResponseBodyPanelTextView(new ResponseBodyStringHttpPanelViewModel()));
    }
}
