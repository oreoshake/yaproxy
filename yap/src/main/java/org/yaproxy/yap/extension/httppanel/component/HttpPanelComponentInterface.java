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
package org.yaproxy.yap.extension.httppanel.component;

import javax.swing.JPanel;
import javax.swing.JToggleButton;
import org.apache.commons.configuration.FileConfiguration;
import org.yaproxy.yap.extension.httppanel.InvalidMessageDataException;
import org.yaproxy.yap.extension.httppanel.Message;
import org.yaproxy.yap.extension.httppanel.view.HttpPanelDefaultViewSelector;
import org.yaproxy.yap.extension.httppanel.view.HttpPanelView;

public interface HttpPanelComponentInterface {

    // Name of the component for internal reference.
    String getName();

    int getPosition();

    // Component has to provide the button which is displayed in the HttpPanel to select this view

    JToggleButton getButton();

    // Component needs to provide a panel with main content which is displayed in HttpPanel

    JPanel getMainPanel();

    // Component can provide an additional panel which is displayed in the HttpPanel header when
    // this view is selected

    JPanel getOptionsPanel();

    JPanel getMoreOptionsPanel();

    // Set a new Message for this Component
    // For example, the user selects a new message in the history tab.
    // The component should update it's models accordingly.

    void setMessage(Message aMessage);

    /**
     * Saves the data shown in the views into the current message.
     *
     * @throws InvalidMessageDataException if unable to save the data (e.g. malformed).
     */
    void save();

    void addView(HttpPanelView view, Object options, FileConfiguration fileConfiguration);

    void removeView(String viewName, Object options);

    void clearView();

    void clearView(boolean enableViewSelect);

    void setEnableViewSelect(boolean enableViewSelect);

    void addDefaultViewSelector(HttpPanelDefaultViewSelector defaultViewSelector, Object options);

    void removeDefaultViewSelector(String defaultViewSelectorName, Object options);

    void setParentConfigurationKey(String configurationKey);

    void loadConfig(FileConfiguration fileConfiguration);

    void saveConfig(FileConfiguration fileConfiguration);

    void setEditable(boolean editable);

    // Used to inform the view if it was selected/unselected

    void setSelected(boolean selected);

    boolean isEnabled(Message aMessage);

    HttpPanelView setSelectedView(String name);
}
