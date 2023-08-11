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
package org.yaproxy.yap.view;

import javax.swing.event.TableModelEvent;
import org.yaproxy.yap.utils.EnableableInterface;

public abstract class AbstractMultipleOptionsTableModel<E extends EnableableInterface>
        extends AbstractMultipleOptionsBaseTableModel<E> {

    private static final long serialVersionUID = 1L;

    public AbstractMultipleOptionsTableModel() {
        super();
    }

    public void setAllEnabled(boolean enabled) {
        final int size = getElements().size();
        if (size > 0) {
            getElements().forEach(e -> e.setEnabled(enabled));
            fireTableColumnUpdated(0, size - 1, 0);
        }
    }

    public void fireTableColumnUpdated(int firstRow, int lastRow, int column) {
        fireTableChanged(
                new TableModelEvent(this, firstRow, lastRow, column, TableModelEvent.UPDATE));
    }
}
