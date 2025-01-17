/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2013 The YAP Development Team
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

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

/**
 * A {@code ButtonGroup} that allows to deselect the selected button.
 *
 * @see ButtonGroup
 */
public class DeselectableButtonGroup extends ButtonGroup {

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     *
     * <p>Overridden to deselect the button when {@code selected} is {@code false}.
     */
    @Override
    public void setSelected(ButtonModel m, boolean selected) {
        if (!selected && m == getSelection()) {
            clearSelection();
        } else {
            super.setSelected(m, selected);
        }
    }
}
