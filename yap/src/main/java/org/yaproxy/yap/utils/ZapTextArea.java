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
package org.yaproxy.yap.utils;

import javax.swing.JTextArea;
import javax.swing.text.Document;
import org.yaproxy.yap.utils.YapTextComponentUndoManager.UndoManagerPolicy;

/**
 * {@code YapTextArea} is a {@code JTextArea} with {@code UndoableEdit}s.
 *
 * <p>The default is to maintain a window of 100 undoable edits. When the limit is reached older
 * undoable edits start to be discarded when new ones are saved. The limit can be changed with the
 * method {@code setEditsLimit(int)}.
 *
 * <p>It is responsibility of the owner of the {@code YapTextArea} to discard all undoable edits
 * when they are not needed.
 *
 * <p>If you do not need undoable edits consider using a {@code JTextArea} instead.
 *
 * @since 1.3.0
 * @see #discardAllEdits()
 * @see #setUndoManagerPolicy
 * @see #setEditsLimit(int)
 * @see YapTextComponentUndoManager
 */
public class YapTextArea extends JTextArea {

    private static final long serialVersionUID = -5473367713363097247L;

    private YapTextComponentUndoManager undoManager;

    /**
     * Constructs a {@code YapTextArea}, with a default {@code Document}, {@code null} text and zero
     * rows/columns.
     */
    public YapTextArea() {
        this(null, null, 0, 0);
    }

    /**
     * Constructs a {@code YapTextArea}, with the given {@code Document}, {@code null} text and zero
     * rows/columns.
     *
     * @param doc the document of the text area
     */
    public YapTextArea(Document doc) {
        this(doc, null, 0, 0);
    }

    /**
     * Constructs a {@code YapTextArea}, with a default {@code Document}, the given {@code text} and
     * zero rows/columns.
     *
     * @param text the initial text of the text area
     */
    public YapTextArea(String text) {
        this(null, text, 0, 0);
    }

    /**
     * Constructs a {@code YapTextArea}, with a default {@code Document}, {@code null} {@code text}
     * and the given number of rows and columns.
     *
     * @param rows the number of rows of the text area
     * @param columns the number of columns of the text area
     */
    public YapTextArea(int rows, int columns) {
        this(null, null, rows, columns);
    }

    /**
     * Constructs a {@code YapTextArea}, with a default {@code Document}, the given {@code text} and
     * the given number of rows and columns.
     *
     * @param text the initial text of the text area
     * @param rows the number of rows of the text area
     * @param columns the number of columns of the text area
     */
    public YapTextArea(String text, int rows, int columns) {
        this(null, text, rows, columns);
    }

    /**
     * Constructs a {@code YapTextArea}, with the given {@code Document}, {@code text} and number of
     * rows and columns.
     *
     * @param doc the document of the text area
     * @param text the initial text of the text area
     * @param rows the number of rows of the text area
     * @param columns the number of columns of the text area
     */
    public YapTextArea(Document doc, String text, int rows, int columns) {
        super(doc, text, rows, columns);

        undoManager = new YapTextComponentUndoManager(this);
    }

    /**
     * Discards all undoable edits.
     *
     * @see YapTextComponentUndoManager#discardAllEdits()
     */
    public void discardAllEdits() {
        undoManager.discardAllEdits();
    }

    /**
     * Sets the maximum number of undoable edits this {@code YapTextArea} can hold.
     *
     * @param limit the new limit
     * @see YapTextComponentUndoManager#setLimit(int)
     */
    public void setEditsLimit(int limit) {
        undoManager.setLimit(limit);
    }

    /**
     * Sets the policy of the undoable edits of this {@code YapTextArea}.
     *
     * @param policy the new policy
     * @throws NullPointerException if policy is {@code null}
     * @see YapTextComponentUndoManager#setUndoManagerPolicy(UndoManagerPolicy)
     */
    public void setUndoManagerPolicy(UndoManagerPolicy policy) throws NullPointerException {
        undoManager.setUndoManagerPolicy(policy);
    }
}
