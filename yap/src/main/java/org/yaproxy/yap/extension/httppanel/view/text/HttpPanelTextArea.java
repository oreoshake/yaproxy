/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2011 The YAP Development Team
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
package org.yaproxy.yap.extension.httppanel.view.text;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaproxy.yap.extension.httppanel.Message;
import org.yaproxy.yap.extension.search.SearchMatch;
import org.yaproxy.yap.utils.DisplayUtils;
import org.yaproxy.yap.utils.YapTextArea;
import org.yaproxy.yap.view.HighlightSearchEntry;
import org.yaproxy.yap.view.HighlighterManager;

/* YAP Text Area
 * Which enhanced functionality. Used to display HTTP Message request / response, or parts of it.
 */
@SuppressWarnings("serial")
public abstract class HttpPanelTextArea extends YapTextArea {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(HttpPanelTextArea.class);

    private Message message;

    public HttpPanelTextArea() {
        this.message = null;

        initHighlighter();
    }

    private void initHighlighter() {
        HighlighterManager highlighter = HighlighterManager.getInstance();

        highlighter.addHighlighterManagerListener(
                e -> {
                    switch (e.getType()) {
                        case HIGHLIGHTS_SET:
                        case HIGHLIGHT_REMOVED:
                            removeAllHighlights();
                            highlightAll();
                            break;
                        case HIGHLIGHT_ADDED:
                            highlightEntryParser(e.getHighlight());
                            break;
                    }
                    this.invalidate();
                });

        if (message != null) {
            highlightAll();
        }
    }

    // Highlight all search strings from HighlightManager
    private void highlightAll() {
        HighlighterManager highlighter = HighlighterManager.getInstance();

        LinkedList<HighlightSearchEntry> highlights = highlighter.getHighlights();
        for (HighlightSearchEntry entry : highlights) {
            highlightEntryParser(entry);
        }
    }

    // Parse the TextArea data and search the HighlightEntry strings
    // Highlight all found strings
    private void highlightEntryParser(HighlightSearchEntry entry) {
        String text;
        int lastPos = 0;

        text = this.getText();

        Highlighter hilite = this.getHighlighter();
        HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(entry.getColor());

        while ((lastPos = text.indexOf(entry.getToken(), lastPos)) > -1) {
            try {
                hilite.addHighlight(lastPos, lastPos + entry.getToken().length(), painter);
                lastPos += entry.getToken().length();
            } catch (BadLocationException e) {
                LOGGER.warn("Could not highlight entry", e);
            }
        }
    }

    // Apply highlights after a setText()
    @Override
    public void setText(String s) {
        super.setText(s);
        highlightAll();
    }

    protected void highlight(int start, int end) {
        Highlighter hilite = this.getHighlighter();
        HighlightPainter painter =
                new DefaultHighlighter.DefaultHighlightPainter(DisplayUtils.getHighlightColor());

        try {
            removeAllHighlights();
            hilite.addHighlight(start, end, painter);
            this.setCaretPosition(start);
        } catch (BadLocationException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void removeAllHighlights() {
        Highlighter hilite = this.getHighlighter();
        hilite.removeAllHighlights();
    }

    public abstract void search(Pattern p, List<SearchMatch> matches);

    // highlight a specific SearchMatch in the editor
    public abstract void highlight(SearchMatch sm);

    public void setMessage(Message aMessage) {
        this.message = aMessage;
    }

    public Message getMessage() {
        return message;
    }
}
