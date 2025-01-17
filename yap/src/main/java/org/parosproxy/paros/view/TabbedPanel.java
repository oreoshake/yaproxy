/*
 * Paros and its related class files.
 *
 * Paros is an HTTP/HTTPS proxy for assessing web application security.
 * Copyright (C) 2003-2004 www.proofsecure.com
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
// YAP: 2011/06/02 Warn the first time the user double clicks on a tab
// YAP: 2012/04/23 Added @Override annotation to the appropriate method.
// YAP: 2013/02/17 Issue 496: Allow to see the request and response at the same
// time in the main window
// YAP: 2013/02/26 Issue 540: Maximised work tabs hidden when response tab
// position changed
// YAP: 2016/04/06 Fix layouts' issues
// YAP: 2019/06/01 Normalise line endings.
// YAP: 2019/06/05 Normalise format/style.
package org.parosproxy.paros.view;

import javax.swing.JTabbedPane;
import org.parosproxy.paros.model.Model;
import org.yaproxy.yap.view.ComponentMaximiser;
import org.yaproxy.yap.view.ComponentMaximiserMouseListener;

@SuppressWarnings("serial")
public class TabbedPanel extends JTabbedPane {

    private static final long serialVersionUID = 8927990541854169615L;

    private ComponentMaximiserMouseListener componentMaximiserML;

    /** This is the default constructor */
    public TabbedPanel() {
        super();
        componentMaximiserML =
                new ComponentMaximiserMouseListener(
                        Model.getSingleton().getOptionsParam().getViewParam());
        initialize();
    }

    /** This method initializes this */
    private void initialize() {
        if (Model.getSingleton().getOptionsParam().getViewParam().getWmUiHandlingOption() == 0) {
            this.setSize(225, 145);
        }
        this.addMouseListener(componentMaximiserML);
    }

    public void setAlternativeParent(java.awt.Container alternativeParent) {
        ComponentMaximiser maximiser =
                alternativeParent != null ? new ComponentMaximiser(alternativeParent) : null;
        componentMaximiserML.setComponentMaximiser(maximiser);
    }

    public boolean isInAlternativeParent() {
        ComponentMaximiser maximiser = componentMaximiserML.getComponentMaximiser();
        if (maximiser == null) {
            return false;
        }
        return maximiser.isComponentMaximised();
    }

    public void alternateParent() {
        componentMaximiserML.triggerMaximisation(this);
    }
} //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
