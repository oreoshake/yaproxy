/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2022 The YAP Development Team
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

import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicHTML;

/**
 * A JLabel which enables HTML support.
 *
 * @since 2.12.0
 */
public class YapHtmlLabel extends JLabel {

    private static final long serialVersionUID = 1L;

    public YapHtmlLabel() {
        setDefaults();
    }

    public YapHtmlLabel(String text) {
        super(text);
        setDefaults();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setDefaults();
        BasicHTML.updateRenderer(this, getText());
    }

    private void setDefaults() {
        putClientProperty("html.disable", null);
    }
}
