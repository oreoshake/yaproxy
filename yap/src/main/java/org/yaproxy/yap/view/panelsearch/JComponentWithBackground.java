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
package org.yaproxy.yap.view.panelsearch;

import java.awt.Color;
import javax.swing.JComponent;

public class JComponentWithBackground extends ComponentWithBackground {

    private JComponent component;

    public JComponentWithBackground(JComponent component) {
        this.component = component;
    }

    @Override
    public Object getComponent() {
        return component;
    }

    @Override
    public void setBackground(Color color) {
        component.setBackground(color);
    }

    @Override
    public Color getBackground() {
        return component.getBackground();
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        component.setOpaque(isOpaque);
    }

    @Override
    public boolean isOpaque() {
        return component.isOpaque();
    }
}
