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
package org.yaproxy.yap.extension.httppanel.view.syntaxhighlight.components.split.request;

import java.awt.Component;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.view.View;
import org.yaproxy.yap.extension.httppanel.Message;
import org.yaproxy.yap.extension.httppanel.view.impl.models.http.request.RequestHeaderStringHttpPanelViewModel;
import org.yaproxy.yap.extension.httppanel.view.syntaxhighlight.HttpPanelSyntaxHighlightTextArea;
import org.yaproxy.yap.extension.httppanel.view.syntaxhighlight.HttpPanelSyntaxHighlightTextView;
import org.yaproxy.yap.extension.httppanel.view.util.CaretVisibilityEnforcerOnFocusGain;
import org.yaproxy.yap.extension.httppanel.view.util.HttpTextViewUtils;
import org.yaproxy.yap.extension.search.SearchMatch;
import org.yaproxy.yap.model.DefaultTextHttpMessageLocation;
import org.yaproxy.yap.model.HttpMessageLocation;
import org.yaproxy.yap.model.MessageLocation;
import org.yaproxy.yap.model.TextHttpMessageLocation;
import org.yaproxy.yap.utils.DisplayUtils;
import org.yaproxy.yap.view.messagecontainer.http.SelectableContentHttpMessageContainer;
import org.yaproxy.yap.view.messagelocation.MessageLocationHighlight;
import org.yaproxy.yap.view.messagelocation.MessageLocationHighlightsManager;
import org.yaproxy.yap.view.messagelocation.MessageLocationProducerFocusListener;
import org.yaproxy.yap.view.messagelocation.MessageLocationProducerFocusListenerAdapter;
import org.yaproxy.yap.view.messagelocation.TextMessageLocationHighlight;
import org.yaproxy.yap.view.messagelocation.TextMessageLocationHighlightsManager;

public class HttpRequestHeaderPanelSyntaxHighlightTextView extends HttpPanelSyntaxHighlightTextView
        implements SelectableContentHttpMessageContainer {

    public static final String NAME = "HttpRequestHeaderSyntaxTextView";

    private MessageLocationProducerFocusListenerAdapter focusListenerAdapter;

    public HttpRequestHeaderPanelSyntaxHighlightTextView(
            RequestHeaderStringHttpPanelViewModel model) {
        super(model);

        getHttpPanelTextArea()
                .setComponentPopupMenu(
                        new CustomPopupMenu() {

                            private static final long serialVersionUID = -426000345249750052L;

                            @Override
                            public void show(Component invoker, int x, int y) {
                                if (!getHttpPanelTextArea().isFocusOwner()) {
                                    getHttpPanelTextArea().requestFocusInWindow();
                                }

                                View.getSingleton()
                                        .getPopupMenu()
                                        .show(
                                                HttpRequestHeaderPanelSyntaxHighlightTextView.this,
                                                x,
                                                y);
                            }
                        });
    }

    @Override
    protected HttpPanelSyntaxHighlightTextArea createHttpPanelTextArea() {
        return new HttpRequestHeaderPanelSyntaxHighlightTextArea();
    }

    @Override
    protected HttpRequestHeaderPanelSyntaxHighlightTextArea getHttpPanelTextArea() {
        return (HttpRequestHeaderPanelSyntaxHighlightTextArea) super.getHttpPanelTextArea();
    }

    @SuppressWarnings("serial")
    private static class HttpRequestHeaderPanelSyntaxHighlightTextArea
            extends HttpPanelSyntaxHighlightTextArea {

        private static final long serialVersionUID = -4532294585338584747L;

        // private static final String HTTP_REQUEST_HEADER = "HTTP Request Header";

        // private static final String SYNTAX_STYLE_HTTP_REQUEST_HEADER =
        // "text/http-request-header";

        private static RequestHeaderTokenMakerFactory tokenMakerFactory = null;

        private CaretVisibilityEnforcerOnFocusGain caretVisibilityEnforcer;

        public HttpRequestHeaderPanelSyntaxHighlightTextArea() {
            // addSyntaxStyle(HTTP_REQUEST_HEADER, SYNTAX_STYLE_HTTP_REQUEST_HEADER);

            // setSyntaxEditingStyle(SYNTAX_STYLE_HTTP_REQUEST_HEADER);

            caretVisibilityEnforcer = new CaretVisibilityEnforcerOnFocusGain(this);
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public HttpMessage getMessage() {
            return (HttpMessage) super.getMessage();
        }

        @Override
        public void setMessage(Message aMessage) {
            super.setMessage(aMessage);

            caretVisibilityEnforcer.setEnforceVisibilityOnFocusGain(aMessage != null);
        }

        protected MessageLocation getSelection() {
            int[] position =
                    HttpTextViewUtils.getViewToHeaderPosition(
                            this, getSelectionStart(), getSelectionEnd());
            if (position.length == 0) {
                return new DefaultTextHttpMessageLocation(
                        HttpMessageLocation.Location.REQUEST_HEADER, 0);
            }

            int start = position[0];
            int end = position[1];
            if (start == end) {
                return new DefaultTextHttpMessageLocation(
                        HttpMessageLocation.Location.REQUEST_HEADER, start);
            }

            return new DefaultTextHttpMessageLocation(
                    HttpMessageLocation.Location.REQUEST_HEADER,
                    start,
                    end,
                    getMessage().getRequestHeader().toString().substring(start, end));
        }

        protected MessageLocationHighlightsManager create() {
            return new TextMessageLocationHighlightsManager();
        }

        protected MessageLocationHighlight highlightImpl(
                TextHttpMessageLocation textLocation, TextMessageLocationHighlight textHighlight) {
            if (getMessage() == null) {
                return null;
            }

            int[] pos =
                    HttpTextViewUtils.getHeaderToViewPosition(
                            this,
                            getMessage().getRequestHeader().toString(),
                            textLocation.getStart(),
                            textLocation.getEnd());
            if (pos.length == 0) {
                return null;
            }
            textHighlight.setHighlightReference(highlight(pos[0], pos[1], textHighlight));

            return textHighlight;
        }

        @Override
        public void search(Pattern p, List<SearchMatch> matches) {
            Matcher m = p.matcher(getText());
            while (m.find()) {

                int[] position =
                        HttpTextViewUtils.getViewToHeaderPosition(this, m.start(), m.end());
                if (position.length == 0) {
                    return;
                }

                matches.add(
                        new SearchMatch(
                                SearchMatch.Location.REQUEST_HEAD, position[0], position[1]));
            }
        }

        @Override
        public void highlight(SearchMatch sm) {
            if (!SearchMatch.Location.REQUEST_HEAD.equals(sm.getLocation())) {
                return;
            }

            int[] pos =
                    HttpTextViewUtils.getHeaderToViewPosition(
                            this,
                            sm.getMessage().getRequestHeader().toString(),
                            sm.getStart(),
                            sm.getEnd());
            if (pos.length == 0) {
                return;
            }
            highlight(pos[0], pos[1]);
        }

        @Override
        protected synchronized CustomTokenMakerFactory getTokenMakerFactory() {
            if (tokenMakerFactory == null) {
                tokenMakerFactory = new RequestHeaderTokenMakerFactory();
            }
            return tokenMakerFactory;
        }

        private static class RequestHeaderTokenMakerFactory extends CustomTokenMakerFactory {

            public RequestHeaderTokenMakerFactory() {
                // String pkg = "org.yaproxy.yap.extension.httppanel.view.text.lexers.";

                // putMapping(SYNTAX_STYLE_HTTP_REQUEST_HEADER, pkg +
                // "HttpRequestHeaderTokenMaker");
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Class<HttpMessage> getMessageClass() {
        return HttpMessage.class;
    }

    @Override
    public Class<? extends MessageLocation> getMessageLocationClass() {
        return TextHttpMessageLocation.class;
    }

    @Override
    public MessageLocation getSelection() {
        return getHttpPanelTextArea().getSelection();
    }

    @Override
    public MessageLocationHighlightsManager create() {
        return getHttpPanelTextArea().create();
    }

    @Override
    public MessageLocationHighlight highlight(MessageLocation location) {
        if (!supports(location)) {
            return null;
        }
        TextHttpMessageLocation textLocation = (TextHttpMessageLocation) location;

        return getHttpPanelTextArea()
                .highlightImpl(
                        textLocation,
                        new TextMessageLocationHighlight(DisplayUtils.getHighlightColor()));
    }

    @Override
    public MessageLocationHighlight highlight(
            MessageLocation location, MessageLocationHighlight highlight) {
        if (!supports(location) || !(highlight instanceof TextMessageLocationHighlight)) {
            return null;
        }
        TextHttpMessageLocation textLocation = (TextHttpMessageLocation) location;
        TextMessageLocationHighlight textHighlight = (TextMessageLocationHighlight) highlight;

        return getHttpPanelTextArea().highlightImpl(textLocation, textHighlight);
    }

    @Override
    public void removeHighlight(
            MessageLocation location, MessageLocationHighlight highlightReference) {
        if (!(highlightReference instanceof TextMessageLocationHighlight)) {
            return;
        }
        getHttpPanelTextArea()
                .removeHighlight(
                        ((TextMessageLocationHighlight) highlightReference)
                                .getHighlightReference());
    }

    @Override
    public boolean supports(MessageLocation location) {
        if (!(location instanceof TextHttpMessageLocation)) {
            return false;
        }
        return ((TextHttpMessageLocation) location).getLocation()
                == TextHttpMessageLocation.Location.REQUEST_HEADER;
    }

    @Override
    public boolean supports(Class<? extends MessageLocation> classLocation) {
        return (TextHttpMessageLocation.class.isAssignableFrom(classLocation));
    }

    @Override
    public void addFocusListener(MessageLocationProducerFocusListener focusListener) {
        getFocusListenerAdapter().addFocusListener(focusListener);
    }

    @Override
    public void removeFocusListener(MessageLocationProducerFocusListener focusListener) {
        getFocusListenerAdapter().removeFocusListener(focusListener);

        if (!getFocusListenerAdapter().hasFocusListeners()) {
            getHttpPanelTextArea().removeFocusListener(focusListenerAdapter);
            focusListenerAdapter = null;
        }
    }

    @Override
    public HttpMessage getMessage() {
        return getHttpPanelTextArea().getMessage();
    }

    @Override
    public Component getComponent() {
        return getHttpPanelTextArea();
    }

    @Override
    public boolean isEmpty() {
        return getHttpPanelTextArea().getMessage() == null;
    }

    private MessageLocationProducerFocusListenerAdapter getFocusListenerAdapter() {
        if (focusListenerAdapter == null) {
            focusListenerAdapter = new MessageLocationProducerFocusListenerAdapter(this);
            getHttpPanelTextArea().addFocusListener(focusListenerAdapter);
        }
        return focusListenerAdapter;
    }
}
