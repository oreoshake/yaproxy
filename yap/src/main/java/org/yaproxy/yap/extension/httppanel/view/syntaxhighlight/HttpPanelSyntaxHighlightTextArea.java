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
package org.yaproxy.yap.extension.httppanel.view.syntaxhighlight;

import java.awt.Component;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;
import org.parosproxy.paros.view.View;
import org.yaproxy.yap.extension.httppanel.Message;
import org.yaproxy.yap.extension.httppanel.view.syntaxhighlight.menus.SyntaxMenu;
import org.yaproxy.yap.extension.httppanel.view.syntaxhighlight.menus.ViewMenu;
import org.yaproxy.yap.extension.search.SearchMatch;
import org.yaproxy.yap.utils.DisplayUtils;
import org.yaproxy.yap.utils.FontUtils;
import org.yaproxy.yap.utils.FontUtils.FontType;
import org.yaproxy.yap.view.HighlightSearchEntry;
import org.yaproxy.yap.view.HighlighterManager;

@SuppressWarnings("serial")
public abstract class HttpPanelSyntaxHighlightTextArea extends RSyntaxTextArea {

    private static final long serialVersionUID = -9082089105656842054L;

    private static final Logger LOGGER =
            LogManager.getLogger(HttpPanelSyntaxHighlightTextArea.class);

    public static final String PLAIN_SYNTAX_LABEL =
            Constant.messages.getString("http.panel.view.syntaxtext.syntax.plain");

    private static final String ANTI_ALIASING = "aa";
    private static final String SHOW_LINE_NUMBERS = "linenumbers";
    private static final String CODE_FOLDING = "codefolding";
    private static final String WORD_WRAP = "wordwrap";
    private static final String HIGHLIGHT_CURRENT_LINE = "highlightline";
    private static final String FADE_CURRENT_HIGHLIGHT_LINE = "fadehighlightline";
    private static final String SHOW_WHITESPACE_CHARACTERS = "whitespaces";
    private static final String SHOW_NEWLINE_CHARACTERS = "newlines";
    private static final String MARK_OCCURRENCES = "markoccurrences";
    private static final String ROUNDED_SELECTION_EDGES = "roundedselection";
    private static final String BRACKET_MATCHING = "bracketmatch";
    private static final String ANIMATED_BRACKET_MATCHING = "animatedbracketmatch";

    private static final String RESOURCE_DARK = "/org/fife/ui/rsyntaxtextarea/themes/dark.xml";
    private static final String RESOURCE_LIGHT = "/org/fife/ui/rsyntaxtextarea/themes/default.xml";

    private Message message;
    private Vector<SyntaxStyle> syntaxStyles;
    private boolean codeFoldingAllowed;

    private static SyntaxMenu syntaxMenu = null;
    private static ViewMenu viewMenu = null;
    private static TextAreaMenuItem cutAction = null;
    private static TextAreaMenuItem copyAction = null;
    private static TextAreaMenuItem pasteAction = null;
    private static TextAreaMenuItem deleteAction = null;
    private static TextAreaMenuItem undoAction = null;
    private static TextAreaMenuItem redoAction = null;
    private static TextAreaMenuItem selectAllAction = null;

    private Boolean darkLaF;

    public HttpPanelSyntaxHighlightTextArea() {
        ((RSyntaxDocument) getDocument()).setTokenMakerFactory(getTokenMakerFactory());
        setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);

        syntaxStyles = new Vector<>();
        addSyntaxStyle(PLAIN_SYNTAX_LABEL, SyntaxConstants.SYNTAX_STYLE_NONE);

        if (syntaxMenu == null) {
            initActions();
        }

        setPopupMenu(null);

        this.message = null;

        setHyperlinksEnabled(false);

        setAntiAliasingEnabled(true);

        setLineWrap(true);

        setHighlightCurrentLine(false);
        setFadeCurrentLineHighlight(false);

        setWhitespaceVisible(false);
        setEOLMarkersVisible(false);

        setMarkOccurrences(false);

        setBracketMatchingEnabled(false);
        setAnimateBracketMatching(false);

        setAutoIndentEnabled(false);
        setCloseCurlyBraces(false);
        setCloseMarkupTags(false);
        setClearWhitespaceLinesEnabled(false);

        if (DisplayUtils.isDarkLookAndFeel()) {
            darkLaF = true;
            setLookAndFeel(darkLaF);
        } else {
            darkLaF = false;
            setFont();
        }
        initHighlighter();
    }

    private void setLookAndFeel(boolean dark) {
        try {
            Theme theme =
                    Theme.load(
                            this.getClass()
                                    .getResourceAsStream(dark ? RESOURCE_DARK : RESOURCE_LIGHT));

            theme.apply(this);
            setFont();
        } catch (IOException e) {
            // Ignore
        }
    }

    private void setFont() {
        this.setFont(
                FontUtils.getFontWithFallback(FontType.workPanels, this.getFont().getFontName()));
    }

    @Override
    public void updateUI() {

        if (darkLaF == null || darkLaF == DisplayUtils.isDarkLookAndFeel()) {
            return;
        }

        darkLaF = DisplayUtils.isDarkLookAndFeel();
        setLookAndFeel(darkLaF);
    }

    /**
     * Sets whether or not code folding is allowed, to show or not a context menu item to
     * enable/disable code folding.
     *
     * <p>Default is {@code false}.
     *
     * @param codeFoldingAllowed {@code true} if code folding is allowed, {@code false} otherwise.
     * @since 2.7.0
     * @see RSyntaxTextArea#setCodeFoldingEnabled(boolean)
     */
    protected void setCodeFoldingAllowed(boolean codeFoldingAllowed) {
        this.codeFoldingAllowed = codeFoldingAllowed;
    }

    /**
     * Tells whether or not code folding is allowed.
     *
     * @return {@code true} if code folding is allowed, {@code false} otherwise.
     * @since 2.7.0
     */
    public boolean isCodeFoldingAllowed() {
        return codeFoldingAllowed;
    }

    @Override
    protected JPopupMenu createPopupMenu() {
        return null;
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

    @Override
    // Apply highlights after a setText()
    public void setText(String s) {
        super.setText(s);
        highlightAll();
    }

    public abstract void search(Pattern p, List<SearchMatch> matches);

    // highlight a specific SearchMatch in the editor
    public abstract void highlight(SearchMatch sm);

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

    public Object highlight(int start, int end, HighlightPainter painter) {
        try {
            Object highlightReference = getHighlighter().addHighlight(start, end, painter);
            this.setCaretPosition(start);
            return highlightReference;
        } catch (BadLocationException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public void removeHighlight(Object highlightReference) {
        getHighlighter().removeHighlight(highlightReference);
    }

    private void removeAllHighlights() {
        Highlighter hilite = this.getHighlighter();
        hilite.removeAllHighlights();
    }

    public void setMessage(Message aMessage) {
        this.message = aMessage;
    }

    public Message getMessage() {
        return message;
    }

    public void loadConfiguration(String key, FileConfiguration fileConfiguration) {
        setAntiAliasingEnabled(
                fileConfiguration.getBoolean(key + ANTI_ALIASING, this.getAntiAliasingEnabled()));

        Component c = getParent();
        if (c instanceof JViewport) {
            c = c.getParent();
            if (c instanceof RTextScrollPane) {
                final RTextScrollPane scrollPane = (RTextScrollPane) c;
                scrollPane.setLineNumbersEnabled(
                        fileConfiguration.getBoolean(
                                key + SHOW_LINE_NUMBERS, scrollPane.getLineNumbersEnabled()));

                if (isCodeFoldingAllowed()) {
                    setCodeFoldingEnabled(
                            fileConfiguration.getBoolean(
                                    key + CODE_FOLDING, this.isCodeFoldingEnabled()));
                    scrollPane.setFoldIndicatorEnabled(this.isCodeFoldingEnabled());
                }
            }
        }

        setLineWrap(fileConfiguration.getBoolean(key + WORD_WRAP, this.getLineWrap()));

        setHighlightCurrentLine(
                fileConfiguration.getBoolean(
                        key + HIGHLIGHT_CURRENT_LINE, this.getHighlightCurrentLine()));
        setFadeCurrentLineHighlight(
                fileConfiguration.getBoolean(
                        key + FADE_CURRENT_HIGHLIGHT_LINE, this.getFadeCurrentLineHighlight()));

        setWhitespaceVisible(
                fileConfiguration.getBoolean(
                        key + SHOW_WHITESPACE_CHARACTERS, this.isWhitespaceVisible()));
        setEOLMarkersVisible(
                fileConfiguration.getBoolean(
                        key + SHOW_NEWLINE_CHARACTERS, this.getEOLMarkersVisible()));

        setMarkOccurrences(
                fileConfiguration.getBoolean(key + MARK_OCCURRENCES, this.getMarkOccurrences()));

        setRoundedSelectionEdges(
                fileConfiguration.getBoolean(
                        key + ROUNDED_SELECTION_EDGES, this.getRoundedSelectionEdges()));

        setBracketMatchingEnabled(
                fileConfiguration.getBoolean(
                        key + BRACKET_MATCHING, this.isBracketMatchingEnabled()));
        setAnimateBracketMatching(
                fileConfiguration.getBoolean(
                        key + ANIMATED_BRACKET_MATCHING, this.getAnimateBracketMatching()));
    }

    public void setLineWrapDisabled(boolean disabled) {
        viewMenu.setWordWrapEnabled(!disabled);
    }

    public void saveConfiguration(String key, FileConfiguration fileConfiguration) {
        fileConfiguration.setProperty(key + ANTI_ALIASING, this.getAntiAliasingEnabled());

        Component c = getParent();
        if (c instanceof JViewport) {
            c = c.getParent();
            if (c instanceof RTextScrollPane) {
                final RTextScrollPane scrollPane = (RTextScrollPane) c;
                fileConfiguration.setProperty(
                        key + SHOW_LINE_NUMBERS, scrollPane.getLineNumbersEnabled());

                if (isCodeFoldingAllowed()) {
                    fileConfiguration.setProperty(key + CODE_FOLDING, this.isCodeFoldingEnabled());
                }
            }
        }

        fileConfiguration.setProperty(key + WORD_WRAP, this.getLineWrap());

        fileConfiguration.setProperty(key + HIGHLIGHT_CURRENT_LINE, this.getHighlightCurrentLine());
        fileConfiguration.setProperty(
                key + FADE_CURRENT_HIGHLIGHT_LINE, this.getFadeCurrentLineHighlight());

        fileConfiguration.setProperty(key + SHOW_WHITESPACE_CHARACTERS, this.isWhitespaceVisible());
        fileConfiguration.setProperty(key + SHOW_NEWLINE_CHARACTERS, this.getEOLMarkersVisible());

        fileConfiguration.setProperty(key + MARK_OCCURRENCES, this.getMarkOccurrences());

        fileConfiguration.setProperty(
                key + ROUNDED_SELECTION_EDGES, this.getRoundedSelectionEdges());

        fileConfiguration.setProperty(key + BRACKET_MATCHING, this.isBracketMatchingEnabled());
        fileConfiguration.setProperty(
                key + ANIMATED_BRACKET_MATCHING, this.getAnimateBracketMatching());
    }

    public Vector<SyntaxStyle> getSyntaxStyles() {
        return syntaxStyles;
    }

    protected void addSyntaxStyle(String label, String styleKey) {
        syntaxStyles.add(new SyntaxStyle(label, styleKey));
    }

    protected abstract CustomTokenMakerFactory getTokenMakerFactory();

    private static synchronized void initActions() {
        if (syntaxMenu == null) {
            syntaxMenu = new SyntaxMenu();
            viewMenu = new ViewMenu();

            undoAction = new TextAreaMenuItem(RTextArea.UNDO_ACTION, true, false);
            redoAction = new TextAreaMenuItem(RTextArea.REDO_ACTION, false, true);

            cutAction = new TextAreaMenuItem(RTextArea.CUT_ACTION, false, false);
            copyAction = new TextAreaMenuItem(RTextArea.COPY_ACTION, false, false);
            pasteAction = new TextAreaMenuItem(RTextArea.PASTE_ACTION, false, false);
            deleteAction = new TextAreaMenuItem(RTextArea.DELETE_ACTION, false, true);

            selectAllAction = new TextAreaMenuItem(RTextArea.SELECT_ALL_ACTION, false, false);

            final List<JMenuItem> mainPopupMenuItems = View.getSingleton().getPopupList();
            mainPopupMenuItems.add(syntaxMenu);
            mainPopupMenuItems.add(viewMenu);

            mainPopupMenuItems.add(undoAction);
            mainPopupMenuItems.add(redoAction);

            mainPopupMenuItems.add(cutAction);
            mainPopupMenuItems.add(copyAction);
            mainPopupMenuItems.add(pasteAction);
            mainPopupMenuItems.add(deleteAction);

            mainPopupMenuItems.add(selectAllAction);
        }
    }

    public static class SyntaxStyle {
        private String label;
        private String styleKey;

        public SyntaxStyle(String label, String styleKey) {
            this.label = label;
            this.styleKey = styleKey;
        }

        public String getLabel() {
            return label;
        }

        public String getStyleKey() {
            return styleKey;
        }
    }

    protected static class CustomTokenMakerFactory extends AbstractTokenMakerFactory {

        @Override
        protected void initTokenMakerMap() {
            String pkg = "org.fife.ui.rsyntaxtextarea.modes.";
            putMapping(SYNTAX_STYLE_NONE, pkg + "PlainTextTokenMaker");
        }
    }

    private static class TextAreaMenuItem extends ExtensionPopupMenuItem {

        private static final long serialVersionUID = -8369459846515841057L;

        private int actionId;
        private boolean precedeWithSeparator;
        private boolean succeedWithSeparator;

        public TextAreaMenuItem(
                int actionId, boolean precedeWithSeparator, boolean succeedWithSeparator)
                throws IllegalArgumentException {
            this.actionId = actionId;
            this.precedeWithSeparator = precedeWithSeparator;
            this.succeedWithSeparator = succeedWithSeparator;
            Action action = RTextArea.getAction(actionId);
            if (action == null) {
                throw new IllegalArgumentException("Action not found with id: " + actionId);
            }
            setAction(action);
        }

        @Override
        public boolean isEnableForComponent(Component invoker) {
            if (invoker instanceof HttpPanelSyntaxHighlightTextArea) {
                HttpPanelSyntaxHighlightTextArea httpPanelTextArea =
                        (HttpPanelSyntaxHighlightTextArea) invoker;

                switch (actionId) {
                    case RTextArea.CUT_ACTION:
                        if (!httpPanelTextArea.isEditable()) {
                            this.setEnabled(false);
                        }
                        break;
                    case RTextArea.DELETE_ACTION:
                    case RTextArea.PASTE_ACTION:
                        this.setEnabled(httpPanelTextArea.isEditable());
                        break;
                    case RTextArea.SELECT_ALL_ACTION:
                        this.setEnabled(httpPanelTextArea.getDocument().getLength() != 0);
                        break;
                }

                return true;
            }
            return false;
        }

        @Override
        public boolean precedeWithSeparator() {
            return precedeWithSeparator;
        }

        @Override
        public boolean succeedWithSeparator() {
            return succeedWithSeparator;
        }

        @Override
        public boolean isSafe() {
            return true;
        }
    }
}
