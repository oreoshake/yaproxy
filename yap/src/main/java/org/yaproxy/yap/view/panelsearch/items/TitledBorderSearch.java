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

import javax.swing.border.TitledBorder;
import org.yaproxy.yap.view.panelsearch.ComponentWithTitle;
import org.yaproxy.yap.view.panelsearch.HighlightedComponent;
import org.yaproxy.yap.view.panelsearch.HighlighterUtils;
import org.yaproxy.yap.view.panelsearch.SearchQuery;

public class TitledBorderSearch extends AbstractComponentSearch<TitledBorder> {

    @Override
    protected boolean isSearchMatchingInternal(TitledBorder component, SearchQuery query) {
        return query.match(component.getTitle());
    }

    @Override
    protected HighlightedComponent highlightInternal(TitledBorder component) {
        return HighlighterUtils.highlightTitleBackgroundWithHtml(
                new TitledBorderComponentWithTitle(component));
    }

    @Override
    protected void undoHighlightInternal(
            HighlightedComponent highlightedComponent, TitledBorder component) {
        HighlighterUtils.undoHighlightTitleBackgroundWithHtml(
                new TitledBorderComponentWithTitle(component), highlightedComponent);
    }

    private static class TitledBorderComponentWithTitle extends ComponentWithTitle {

        private TitledBorder component;

        public TitledBorderComponentWithTitle(TitledBorder component) {
            this.component = component;
        }

        @Override
        public Object getComponent() {
            return component;
        }

        @Override
        public void setTitle(String title) {
            component.setTitle(title);
        }

        @Override
        public String getTitle() {
            return component.getTitle();
        }
    }
}
