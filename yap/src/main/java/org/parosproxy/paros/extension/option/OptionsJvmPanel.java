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
package org.parosproxy.paros.extension.option;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.view.AbstractParamPanel;
import org.parosproxy.paros.view.View;
import org.yaproxy.yap.utils.YapLabel;
import org.yaproxy.yap.utils.YapTextField;
import org.yaproxy.yap.view.LayoutHelper;
import org.yaproxy.yap.view.renderer.SizeBytesStringValue;

/**
 * The JVM options panel.
 *
 * <p>These options are used by yap.sh and yap.bat when starting YAP
 */
public class OptionsJvmPanel extends AbstractParamPanel {

    private static final long serialVersionUID = -7541236934312940852L;
    private static final Path JVM_PROPERTIES_FILE =
            Paths.get(Constant.getDefaultHomeDirectory(false), ".YAP_JVM.properties");

    /** The name of the options panel. */
    private static final String NAME = Constant.messages.getString("jvm.options.title");

    private static final SizeBytesStringValue sbsv = new SizeBytesStringValue(false);

    /** The text field for the JVM options. */
    private YapTextField jvmOptionsField = null;

    private YapLabel sizeMemoryLabel = null;
    private YapLabel usedMemoryLabel = null;
    private YapLabel maxMemoryLabel = null;

    public OptionsJvmPanel() {
        super();
        setName(NAME);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        int row = 0;
        JLabel jvmOptionsLabel =
                new JLabel(Constant.messages.getString("jvm.options.label.jvmoptions"));
        jvmOptionsLabel.setLabelFor(getJvmOptionsField());

        panel.add(jvmOptionsLabel, LayoutHelper.getGBC(0, row, 1, 2.0));
        panel.add(getJvmOptionsField(), LayoutHelper.getGBC(1, row, 1, 8.0));

        panel.add(
                new JLabel(Constant.messages.getString("jvm.options.warning.restart")),
                LayoutHelper.getGBC(0, ++row, 2, 1.0));

        panel.add(
                new JSeparator(SwingConstants.HORIZONTAL),
                LayoutHelper.getGBC(0, ++row, 2, 0.0D, 0.0D));

        panel.add(getSizeMemoryLabel(), LayoutHelper.getGBC(0, ++row, 2, 1.0));
        panel.add(getUsedMemoryLabel(), LayoutHelper.getGBC(0, ++row, 2, 1.0));
        panel.add(getMaxMemoryLabel(), LayoutHelper.getGBC(0, ++row, 2, 1.0));

        panel.add(new JLabel(), LayoutHelper.getGBC(0, 10, 1, 0.5D, 1.0D)); // Spacer

        this.add(panel);
    }

    private YapTextField getJvmOptionsField() {
        if (jvmOptionsField == null) {
            jvmOptionsField = new YapTextField();
        }
        return jvmOptionsField;
    }

    private YapLabel getSizeMemoryLabel() {
        if (sizeMemoryLabel == null) {
            sizeMemoryLabel = new YapLabel();
        }
        return sizeMemoryLabel;
    }

    private YapLabel getUsedMemoryLabel() {
        if (usedMemoryLabel == null) {
            usedMemoryLabel = new YapLabel();
        }
        return usedMemoryLabel;
    }

    private YapLabel getMaxMemoryLabel() {
        if (maxMemoryLabel == null) {
            maxMemoryLabel = new YapLabel();
        }
        return maxMemoryLabel;
    }

    private void updateMemoryLabel(YapLabel labelToUpdate, String key, long value) {
        labelToUpdate.setText(Constant.messages.getString(key, sbsv.getString(value)));
    }

    @Override
    public void initParam(Object obj) {
        long size = Runtime.getRuntime().totalMemory();
        // initParam happens before display of the panel so the values are appropriately set when
        // viewed
        updateMemoryLabel(getSizeMemoryLabel(), "jvm.options.memory.size", size);
        updateMemoryLabel(
                getUsedMemoryLabel(),
                "jvm.options.memory.used",
                size - Runtime.getRuntime().freeMemory());
        updateMemoryLabel(
                getMaxMemoryLabel(), "jvm.options.memory.max", Runtime.getRuntime().maxMemory());
        try {
            /* JVM properties are unusual in that they are held
             * in a separate file from the other options.
             * This is for various reasons, including the fact they are used
             * by the scripts rather than the java code.
             */
            if (Files.exists(JVM_PROPERTIES_FILE)) {
                List<String> lines =
                        Files.readAllLines(JVM_PROPERTIES_FILE, StandardCharsets.UTF_8);
                if (lines.size() > 0) {
                    getJvmOptionsField().setText(lines.get(0));
                }
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    @Override
    public void reset() {
        getJvmOptionsField().setText("");
        saveJvmFile();
    }

    @Override
    public void saveParam(Object obj) throws Exception {
        saveJvmFile();
    }

    private void saveJvmFile() {
        try {
            String opts = getJvmOptionsField().getText();
            if (opts.length() == 0) {
                // Delete the file so that the 'normal' defaults apply
                Files.deleteIfExists(JVM_PROPERTIES_FILE);
            } else {
                if (!JVM_PROPERTIES_FILE.getParent().toFile().exists()) {
                    // Can happen if the user has only run the dev version not the release one
                    JVM_PROPERTIES_FILE.getParent().toFile().mkdirs();
                }
                // Replace the file contents, even if its just with whitespace
                Files.write(JVM_PROPERTIES_FILE, opts.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            View.getSingleton()
                    .showWarningDialog(
                            this,
                            Constant.messages.getString(
                                    "jvm.options.error.writing",
                                    JVM_PROPERTIES_FILE.toAbsolutePath(),
                                    e.getMessage()));
        }
    }

    @Override
    public String getHelpIndex() {
        return "ui.dialogs.options.jvm";
    }
}
