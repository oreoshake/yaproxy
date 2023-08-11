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
package org.yaproxy.yap.view.panelsearch.items;

import javax.swing.JLabel;
import org.yaproxy.yap.view.panelsearch.HighlightedComponent;
import org.yaproxy.yap.view.panelsearch.HighlighterUtils;
import org.yaproxy.yap.view.panelsearch.SearchQuery;

public class LabelSearch extends AbstractComponentSearch<JLabel> {

    @Override
    protected boolean isSearchMatchingInternal(JLabel component, SearchQuery query) {
        return query.match(component.getText());
    }

    @Override
    protected Object[] getComponentsInternal(JLabel component) {
        return new Object[] {};
    }

    @Override
    protected HighlightedComponent highlightInternal(JLabel component) {
        return HighlighterUtils.highlightBackground(
                component, HighlighterUtils.getHighlightColor());
    }

    @Override
    protected void undoHighlightInternal(
            HighlightedComponent highlightedComponent, JLabel component) {
        HighlighterUtils.undoHighlightBackground(highlightedComponent, component);
    }
}
