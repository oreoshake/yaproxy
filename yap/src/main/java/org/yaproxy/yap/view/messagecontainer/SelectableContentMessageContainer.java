/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2015 The YAP Development Team
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
package org.yaproxy.yap.view.messagecontainer;

import org.yaproxy.yap.extension.httppanel.Message;
import org.yaproxy.yap.view.messagelocation.MessageLocationHighlighter;
import org.yaproxy.yap.view.messagelocation.MessageLocationProducer;

/**
 * A {@code SingleMessageContainer} that allows to select content, that is, produces message
 * locations and it's also capable of highlight.
 *
 * @param <T> the type of message in this container
 * @since 2.4.0
 * @see MessageLocationProducer
 * @see MessageLocationHighlighter
 */
public interface SelectableContentMessageContainer<T extends Message>
        extends SingleMessageContainer<T>, MessageLocationProducer, MessageLocationHighlighter {}
