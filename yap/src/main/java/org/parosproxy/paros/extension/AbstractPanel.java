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
// YAP: 2012/11/30 Issue 425: Added tab index to support quick start tab
// YAP: 2013/07/23 Issue 738: Options to hide tabs
// YAP: 2013/12/13 Added support for remembering the old tab name, which is used for nameless tabs.
// YAP: 2014/01/28 Issue 207: Support keyboard shortcuts
// YAP: 2014/02/07 Issue 207: Added tabSelected method
// YAP: 2014/08/14 Issue 1301: AbstractPanel leak through TabbedPanel2
// YAP: 2014/10/07 Issue 1357: Hide unused tabs
// YAP: 2018/02/22 Focus the tab of ancestor AbstractPanel.
// YAP: 2019/06/01 Normalise line endings.
// YAP: 2019/06/05 Normalise format/style.
// YAP: 2022/08/05 Address warns with Java 18 (Issue 7389).
package org.parosproxy.paros.extension;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.parosproxy.paros.model.Model;
import org.yaproxy.yap.view.TabbedPanel2;

@SuppressWarnings("serial")
public class AbstractPanel extends JPanel {

    private static final long serialVersionUID = 4076608955743534659L;

    // YAP: Added icon
    private Icon icon = null;
    private int tabIndex = -1;
    private TabbedPanel2 parent = null;
    private boolean hideable = true;
    private boolean pinned = false;
    private boolean locked = false;
    private boolean showByDefault = false;

    private KeyStroke defaultAccelerator;
    private char mnemonic;

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    /** This is the default constructor */
    public AbstractPanel() {
        super();
        initialize();
    }

    /** This method initializes this */
    private void initialize() {
        if (Model.getSingleton().getOptionsParam().getViewParam().getWmUiHandlingOption() == 0) {
            this.setSize(300, 200);
        }
    }

    public void setTabFocus() {
        if (parent != null) {
            // Just in case the tab has been hidden
            parent.setVisible(this, true);
        }

        Component c = this.getParent();
        if (c instanceof JTabbedPane) {
            JTabbedPane tab = (JTabbedPane) c;
            tab.setSelectedComponent(this);
        } else {
            // If this panel is added in an AbstractPanel request focus to that one too.
            AbstractPanel ancestorPanel =
                    (AbstractPanel) SwingUtilities.getAncestorOfClass(AbstractPanel.class, this);
            if (ancestorPanel != null) {
                ancestorPanel.setTabFocus();
            }
        }
    }

    public boolean isTabVisible() {
        if (parent != null) {
            return parent.isTabVisible(this);
        }
        // Fall back, probably not that useful, but better than nothing?
        return this.isVisible();
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public void setParent(final TabbedPanel2 parent) {
        this.parent = parent;
    }

    /**
     * Invoked when the associated tab is selected. The method does nothing so override to act on
     * the event
     */
    public void tabSelected() {
        // Do nothing
    }

    public boolean isHideable() {
        return hideable;
    }

    public void setHideable(boolean hideable) {
        this.hideable = hideable;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isShowByDefault() {
        return showByDefault;
    }

    public void setShowByDefault(boolean showByDefault) {
        this.showByDefault = showByDefault;
    }

    public KeyStroke getDefaultAccelerator() {
        return defaultAccelerator;
    }

    public void setDefaultAccelerator(KeyStroke defaultAccelerator) {
        this.defaultAccelerator = defaultAccelerator;
    }

    public char getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(char mnemonic) {
        this.mnemonic = mnemonic;
    }
}
