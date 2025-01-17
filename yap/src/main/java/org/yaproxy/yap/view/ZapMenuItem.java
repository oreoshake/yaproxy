/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2014 The YAP Development Team
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

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.parosproxy.paros.Constant;

/**
 * A {@code JMenuItem} that has an identifier, allows to define the default accelerator and uses a
 * internationalised text and, optionally, a mnemonic, both read from resource files.
 *
 * <p>The use of this class is preferred to {@code JMenuItem} as it allows the user to configure its
 * accelerator through the options dialogue. The identifier is used to save/load its configurations.
 *
 * <p>Example usage:
 *
 * <blockquote>
 *
 * <pre>{@code
 * // Obtain the system dependent accelerator modifier key
 * int acceleratorModifierKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
 * KeyStroke defaultAccelerator = KeyStroke.getKeyStroke(KeyEvent.VK_R, acceleratorModifierKey);
 * YapMenuItem menuItem = new YapMenuItem("menu.report", defaultAccelerator);
 * }</pre>
 *
 * </blockquote>
 *
 * and in the resource file (e.g. Messages.properties) define the keys:
 *
 * <blockquote>
 *
 * <pre>{@code
 * menu.report = Report
 * # Optionally define its mnemonic
 * menu.report.mnemonic = R
 * }</pre>
 *
 * </blockquote>
 *
 * @since 2.3.0
 */
public class YapMenuItem extends JMenuItem {

    private static final long serialVersionUID = 1L;

    private String identifier;
    private KeyStroke defaultAccelerator;

    /**
     * Constructs a {@code YapMenuItem} with the text for the menu obtained from the resource files
     * (e.g. Messages.properties) using as key the parameter {@code i18nKey} and using the given
     * {@code KeyStroke} as default accelerator (the user can override the accelerator through the
     * configurations).
     *
     * <p>The parameter {@code i18nKey} is used as identifier (to save/load configurations, like its
     * accelerator) and it will also be used to attempt to create its mnemonic, by using as key
     * {@code <i18nKey>.mnemonic}. No mnemonic is set if the key does not exist in the resource
     * files.
     *
     * <p><strong>Note:</strong> This constructor is preferred to {@link #YapMenuItem(String,
     * String, KeyStroke)}, as it supports the (automatic) load of internationalised text and
     * definition of the mnemonic.
     *
     * @param i18nKey the key used to read the internationalised text for the menu item and,
     *     optionally, its mnemonic
     * @param defaultAccelerator the default accelerator for the menu item, might be {@code null}.
     * @see #setMnemonic(int)
     * @see #setAccelerator(KeyStroke)
     * @throws NullPointerException if {@code i18nKey} is {@code null}.
     */
    public YapMenuItem(String i18nKey, KeyStroke defaultAccelerator) {
        this(i18nKey, Constant.messages.getString(i18nKey), defaultAccelerator);
        // This will handle missing i18n keys ok
        this.setMnemonic(Constant.messages.getChar(i18nKey + ".mnemonic"));
    }

    /**
     * Constructs a {@code YapMenuItem} with the given {@code identifier}, {@code text} and default
     * accelerator (user can override the accelerator through the configurations).
     *
     * <p><strong>Note:</strong> The constructor {@link #YapMenuItem(String, KeyStroke)} is
     * preferred to this one, as it supports the (automatic) load of internationalised text and
     * definition of the mnemonic.
     *
     * @param identifier the identifier for the menu item (used to save/load configurations), should
     *     not be {@code null}
     * @param text the text shown in the menu item
     * @param defaultAccelerator the default accelerator for the menu item, might be {@code null}
     * @see #setMnemonic(int)
     */
    public YapMenuItem(String identifier, String text, KeyStroke defaultAccelerator) {
        super(text);
        this.identifier = identifier;
        this.defaultAccelerator = defaultAccelerator;

        if (defaultAccelerator != null) {
            // Note that this can be overridden by the Keyboard extension
            this.setAccelerator(defaultAccelerator);
        }
    }

    /**
     * Constructs a {@code YapMenuItem} with the text for the menu obtained from the resources file
     * (e.g. Messages.properties) using as key the given parameter and with no default accelerator.
     *
     * <p>The given parameter is used as identifier (to save/load configurations, like its
     * accelerator) and it will also be used to attempt to create its mnemonic, by using as key
     * {@code <i18nKey>.mnemonic}. No mnemonic is set if the key does not exist in the resource
     * files.
     *
     * @param i18nKey the key used to read the internationalised text for the menu item and,
     *     optionally, its mnemonic
     * @throws NullPointerException if {@code i18nKey} is {@code null}.
     * @see YapMenuItem#YapMenuItem(String, KeyStroke)
     * @see #setMnemonic(int)
     */
    public YapMenuItem(String i18nKey) {
        this(i18nKey, null);
    }

    /**
     * Gets the identifier of the menu item.
     *
     * @return the identifier, might be {@code null}.
     * @since 2.8.0
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Gets the identifier of the menu item.
     *
     * @return the identifier, might be {@code null}
     * @deprecated (2.8.0) Use {@link #getIdentifier()} instead.
     */
    @Deprecated
    public String getIdenfifier() {
        return this.identifier;
    }

    /**
     * Resets the accelerator to default value (which might be {@code null} thus just removing any
     * accelerator previously set).
     *
     * @see #getDefaultAccelerator()
     */
    public void resetAccelerator() {
        this.setAccelerator(this.defaultAccelerator);
    }

    /**
     * Gets the default accelerator, defined when the menu item was constructed.
     *
     * @return a {@code KeyStroke} with the default accelerator, might be {@code null}.
     * @see #resetAccelerator()
     */
    public KeyStroke getDefaultAccelerator() {
        return defaultAccelerator;
    }
}
