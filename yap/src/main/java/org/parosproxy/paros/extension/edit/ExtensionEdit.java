/*
 *
 * Paros and its related class files.
 *
 * Paros is an HTTP/HTTPS proxy for assessing web application security.
 * Copyright (C) 2003-2004 Chinotec Technologies Company
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Clarified Artistic License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Clarified Artistic License for more details.
 *
 * You should have received a copy of the Clarified Artistic License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
// YAP: 2011/11/20 Set order
// YAP: 2012/03/17 Issue 282 Added getAuthor()
// YAP: 2012/04/25 Added @Override annotation to all appropriate methods.
// YAP: 2013/03/03 Issue 546: Remove all template Javadoc comments
// YAP: 2014/01/28 Issue 207: Support keyboard shortcuts
// YAP: 2015/03/16 Issue 1525: Further database independence changes
// YAP: 2016/06/20 Removed unnecessary/unused constructor
// YAP: 2017/04/07 Added name constants and getUIName()
// YAP: 2017/07/22 Added KeyStroke constant for consistency with other FindDialog usage
// YAP: 2017/08/10 Issue 3798: java.awt.Toolkit initialised in daemon mode
// YAP: 2017/10/18 Use Window for parent of invoked component.
// YAP: 2018/07/17 Use ViewDelegate.getMenuShortcutKeyStroke.
// YAP: 2019/06/01 Normalise line endings.
// YAP: 2019/06/05 Normalise format/style.
// YAP: 2020/06/05 JavaDoc corrections.
package org.parosproxy.paros.extension.edit;

import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.view.FindDialog;
import org.parosproxy.paros.view.View;
import org.yaproxy.yap.view.YapMenuItem;

public class ExtensionEdit extends ExtensionAdaptor {

    private static final String NAME = "ExtensionEdit";

    /**
     * The find default keyboard shortcut.
     *
     * <p>Lazily initialised.
     *
     * @see #getFindDefaultKeyStroke()
     */
    private static KeyStroke findDefaultKeyStroke;

    private YapMenuItem menuFind = null;
    private PopupFindMenu popupFindMenu = null;

    public ExtensionEdit() {
        super(NAME);
        this.setOrder(4);
    }

    @Override
    public String getUIName() {
        return Constant.messages.getString("edit.name");
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);

        if (getView() != null) {
            extensionHook.getHookMenu().addEditMenuItem(getMenuFind());
            extensionHook.getHookMenu().addPopupMenuItem(getPopupMenuFind());
        }
    }

    private void showFindDialog(Window window, JTextComponent lastInvoker) {
        FindDialog findDialog = FindDialog.getDialog(window, false);
        findDialog.setLastInvoker(lastInvoker);
        findDialog.setVisible(true);
    }

    /**
     * This method initializes menuFind
     *
     * @return the 'Find' menu item.
     */
    private YapMenuItem getMenuFind() {
        if (menuFind == null) {
            menuFind = new YapMenuItem("menu.edit.find", getFindDefaultKeyStroke());

            menuFind.addActionListener(
                    new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            showFindDialog(getView().getMainFrame(), null);
                        }
                    });
        }
        return menuFind;
    }

    /**
     * Gets the default keyboard shortcut for find functionality.
     *
     * <p>Should be called/used only when in view mode.
     *
     * @return the keyboard shortcut, never {@code null}.
     * @since 2.7.0
     */
    public static KeyStroke getFindDefaultKeyStroke() {
        if (findDefaultKeyStroke == null) {
            findDefaultKeyStroke =
                    View.getSingleton().getMenuShortcutKeyStroke(KeyEvent.VK_F, 0, false);
        }
        return findDefaultKeyStroke;
    }

    /**
     * This method initializes popupMenuFind
     *
     * @return the 'Find' context menu.
     */
    private PopupFindMenu getPopupMenuFind() {
        if (popupFindMenu == null) {
            popupFindMenu = new PopupFindMenu();
            popupFindMenu.setText(Constant.messages.getString("edit.find.popup")); // YAP: i18n
            popupFindMenu.addActionListener(
                    new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            JTextComponent component = popupFindMenu.getLastInvoker();
                            Window window = getWindowAncestor(component);
                            if (window != null) {
                                showFindDialog(window, component);
                            }
                        }
                    });
        }
        return popupFindMenu;
    }

    /**
     * Gets the ancestor {@code Window} where the given {@code component} is contained.
     *
     * @param component the component for which the window ancestor should be found.
     * @return the {@code Window}, or {@code null} if the component is {@code null} or if not
     *     contained inside a {@code Window}.
     */
    private static Window getWindowAncestor(JTextComponent component) {
        if (component == null) {
            return null;
        }
        return SwingUtilities.getWindowAncestor(component);
    }

    @Override
    public String getAuthor() {
        return Constant.PAROS_TEAM;
    }

    /** No database tables used, so all supported */
    @Override
    public boolean supportsDb(String type) {
        return true;
    }
}
