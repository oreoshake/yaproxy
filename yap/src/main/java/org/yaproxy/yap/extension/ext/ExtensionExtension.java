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
package org.yaproxy.yap.extension.ext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.extension.CommandLineArgument;
import org.parosproxy.paros.extension.CommandLineListener;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;

public class ExtensionExtension extends ExtensionAdaptor implements CommandLineListener {

    public static final String NAME = "ExtensionExtension";

    private OptionsExtensionPanel optionsExceptionsPanel = null;

    public ExtensionExtension() {
        super();
        initialize();
    }

    private void initialize() {
        this.setName(NAME);
        // this.setOrder(0);
    }

    @Override
    public String getUIName() {
        return Constant.messages.getString("ext.name");
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);

        if (getView() != null) {
            extensionHook.getHookView().addOptionPanel(getOptionsExtensionPanel());
        }
    }

    private OptionsExtensionPanel getOptionsExtensionPanel() {
        if (optionsExceptionsPanel == null) {
            optionsExceptionsPanel = new OptionsExtensionPanel(this);
        }
        return optionsExceptionsPanel;
    }

    public void enableExtension(String name, boolean enable) {
        ExtensionParam extParam = getModel().getOptionsParam().getExtensionParam();
        Map<String, Boolean> extensionsState = extParam.getExtensionsState();
        extensionsState.put(name, enable);
        extParam.setExtensionsState(extensionsState);
    }

    @Override
    public boolean isCore() {
        return true;
    }

    @Override
    public String getAuthor() {
        return Constant.YAP_TEAM;
    }

    @Override
    public String getDescription() {
        return Constant.messages.getString("ext.desc");
    }

    @Override
    public void execute(CommandLineArgument[] args) {
        // Don nothing

    }

    @Override
    public boolean handleFile(File file) {
        // Support loading sessions
        Control.getSingleton().openSession(file, null);
        return true;
    }

    @Override
    public List<String> getHandledExtensions() {
        // Support loading sessions
        List<String> exts = new ArrayList<>();
        exts.add("session");
        return exts;
    }

    /** No database tables used, so all supported */
    @Override
    public boolean supportsDb(String type) {
        return true;
    }
}
