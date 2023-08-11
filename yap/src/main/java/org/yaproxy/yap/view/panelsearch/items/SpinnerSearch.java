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

import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import org.yaproxy.yap.view.panelsearch.ComponentWithBackground;
import org.yaproxy.yap.view.panelsearch.HighlightedComponent;
import org.yaproxy.yap.view.panelsearch.HighlighterUtils;
import org.yaproxy.yap.view.panelsearch.JComponentWithBackground;
import org.yaproxy.yap.view.panelsearch.SearchQuery;

public class SpinnerSearch extends AbstractComponentSearch<JSpinner> {

    private static final String HIGHLIGHTED_EDITOR = "highlightedEditorComponent";

    @Override
    protected boolean isSearchMatchingInternal(JSpinner component, SearchQuery query) {
        return query.match(component.getValue().toString());
    }

    @Override
    protected HighlightedComponent highlightInternal(JSpinner component) {
        HighlightedComponent highlightedUpAndDownComponent =
                HighlighterUtils.highlightBackground(
                        new JComponentWithBackground(component),
                        HighlighterUtils.getHighlightColor());
        HighlightedComponent highlightedEditorComponent =
                HighlighterUtils.highlightBackground(
                        new SpinnerSearchComponentWithBackground(component),
                        HighlighterUtils.getHighlightColor());

        highlightedUpAndDownComponent.put(HIGHLIGHTED_EDITOR, highlightedEditorComponent);
        return highlightedUpAndDownComponent;
    }

    @Override
    protected void undoHighlightInternal(
            HighlightedComponent highlightedComponent, JSpinner component) {
        HighlightedComponent highlightedUpAndDownComponent = highlightedComponent;
        HighlightedComponent highlightedEditorComponent =
                highlightedUpAndDownComponent.get(HIGHLIGHTED_EDITOR);

        HighlighterUtils.undoHighlightBackground(
                new JComponentWithBackground(component), highlightedUpAndDownComponent);
        HighlighterUtils.undoHighlightBackground(
                new SpinnerSearchComponentWithBackground(component), highlightedEditorComponent);
    }

    private static class SpinnerSearchComponentWithBackground extends ComponentWithBackground {
        private JSpinner component;

        public SpinnerSearchComponentWithBackground(JSpinner component) {
            this.component = component;
        }

        @Override
        public Object getComponent() {
            return component;
        }

        @Override
        public void setBackground(Color color) {
            getEditor().setBackground(color);
        }

        private JComponent getEditor() {
            return component.getEditor();
        }

        @Override
        public Color getBackground() {
            return getEditor().getBackground();
        }

        @Override
        public void setOpaque(boolean isOpaque) {
            getEditor().setOpaque(isOpaque);
        }

        @Override
        public boolean isOpaque() {
            return getEditor().isOpaque();
        }
    }
}
