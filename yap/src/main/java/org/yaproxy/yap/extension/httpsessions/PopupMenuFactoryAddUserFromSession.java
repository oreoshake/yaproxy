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
package org.yaproxy.yap.extension.httpsessions;

import java.awt.Component;
import java.awt.Dialog;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.view.SessionDialog;
import org.parosproxy.paros.view.View;
import org.yaproxy.yap.authentication.ManualAuthenticationMethodType;
import org.yaproxy.yap.authentication.ManualAuthenticationMethodType.ManualAuthenticationMethod;
import org.yaproxy.yap.extension.authentication.ExtensionAuthentication;
import org.yaproxy.yap.extension.stdmenus.PopupContextMenuItemFactory;
import org.yaproxy.yap.extension.users.ContextUsersPanel;
import org.yaproxy.yap.extension.users.DialogAddUser;
import org.yaproxy.yap.extension.users.ExtensionUserManagement;
import org.yaproxy.yap.model.Context;
import org.yaproxy.yap.users.User;

@SuppressWarnings("serial")
public class PopupMenuFactoryAddUserFromSession extends PopupContextMenuItemFactory {

    private static final Logger LOGGER =
            LogManager.getLogger(PopupMenuFactoryAddUserFromSession.class);

    private static final long serialVersionUID = 2453839120088204122L;

    /** The extension. */
    private ExtensionHttpSessions extension;

    /** The extension auth. */
    private ExtensionAuthentication extensionAuth;

    /** The extension users management. */
    private ExtensionUserManagement extensionUsers;

    public PopupMenuFactoryAddUserFromSession(ExtensionHttpSessions extension) {
        super(Constant.messages.getString("httpsessions.popup.session.addUser"));
        this.extension = extension;
    }

    @Override
    public ExtensionPopupMenuItem getContextMenu(Context context, String parentMenu) {
        return new PopupMenuAddUserFromSession(context);
    }

    /**
     * Gets the authentication extension
     *
     * @return the extension authentication
     */
    private ExtensionAuthentication getExtensionAuthentication() {
        if (extensionAuth == null) {
            extensionAuth =
                    Control.getSingleton()
                            .getExtensionLoader()
                            .getExtension(ExtensionAuthentication.class);
        }
        return extensionAuth;
    }

    /**
     * Gets the Users extension
     *
     * @return the extension Users
     */
    private ExtensionUserManagement getExtensionUserManagement() {
        if (extensionUsers == null) {
            extensionUsers =
                    Control.getSingleton()
                            .getExtensionLoader()
                            .getExtension(ExtensionUserManagement.class);
        }
        return extensionUsers;
    }

    @SuppressWarnings("serial")
    protected class PopupMenuAddUserFromSession extends ExtensionPopupMenuItem {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 301409585294663964L;

        @Override
        public boolean isSubMenu() {
            return true;
        }

        @Override
        public String getParentMenuName() {
            return Constant.messages.getString("httpsessions.popup.session.addUser");
        }

        private Context context;
        private boolean pendingUsersClearing;
        private User newUser;

        public PopupMenuAddUserFromSession(Context context) {
            super(context.getName());
            this.context = context;
            this.addActionListener(
                    new java.awt.event.ActionListener() {

                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            performAction();
                        }
                    });
        }

        @Override
        public boolean isEnableForComponent(Component invoker) {
            // Only enable if the authentication and the UsersManagement extensions are enabled
            if (getExtensionAuthentication() == null) {
                return false;
            }
            if (getExtensionUserManagement() == null) {
                return false;
            }
            // Only enable it for the HttpSessionsPanel
            if (invoker.getName() != null
                    && invoker.getName().equals(HttpSessionsPanel.PANEL_NAME)) {
                return true;
            }

            // Enable always for now.
            // // Only enable if the currently selected site is in the context
            // TODO: WOuld require cleaning up the port number from site name
            // if (context.isInContext(extension.getHttpSessionsPanel().getCurrentSite()))
            // return true;

            return false;
        }

        @Override
        public int getParentMenuIndex() {
            return 1;
        }

        /**
         * Make sure the user acknowledges the Users corresponding to this context will be deleted.
         *
         * @return true, if successful
         */
        private boolean confirmUsersDeletion(Context uiSharedContext) {
            if (getExtensionUserManagement() != null) {
                if (getExtensionUserManagement().getSharedContextUsers(uiSharedContext).size()
                        > 0) {
                    int choice =
                            JOptionPane.showConfirmDialog(
                                    this,
                                    Constant.messages.getString(
                                            "authentication.dialog.confirmChange.label"),
                                    Constant.messages.getString(
                                            "authentication.dialog.confirmChange.title"),
                                    JOptionPane.OK_CANCEL_OPTION);
                    if (choice == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * A dialog, based on {@link DialogAddUser} that uses credentials based on existing session.
         */
        private class DialogAddUserBasedOnSession extends DialogAddUser {

            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 2269873123657767822L;

            /** The session. */
            private HttpSession session;

            public DialogAddUserBasedOnSession(
                    Dialog owner, ExtensionUserManagement extension, HttpSession session) {
                super(owner, extension);
                this.session = session;
            }

            @Override
            protected void init() {
                if (this.workingContext == null)
                    throw new IllegalStateException(
                            "A working Context should be set before setting the 'Add Dialog' visible.");

                // Initialize the credentials that will be configured
                configuredCredentials =
                        ManualAuthenticationMethodType.createAuthenticationCredentials(session);

                getNameTextField().setText(session.getName());
                getEnabledCheckBox().setSelected(true);
                initializeCredentialsConfigPanel();
            }
        }

        /**
         * Shows the add User dialogue for creating an user based on a session. Method imported from
         * {@link ContextUsersPanel}.
         *
         * @return the user, or null if cancel was pressed
         */
        private User showAddUserDialogue(Context uiSharedContext, HttpSession session) {
            DialogAddUserBasedOnSession addDialog = null;

            if (addDialog == null) {
                addDialog =
                        new DialogAddUserBasedOnSession(
                                View.getSingleton().getOptionsDialog(null),
                                getExtensionUserManagement(),
                                session);
                addDialog.pack();
            }
            addDialog.setWorkingContext(uiSharedContext);
            addDialog.setVisible(true);

            User user = addDialog.getUser();
            addDialog.clear();
            addDialog.dispose();

            return user;
        }

        public void performAction() {
            // Manually create the UI shared contexts so any modifications are done
            // on an UI shared Context, so changes can be undone by pressing Cancel
            SessionDialog sessionDialog = View.getSingleton().getSessionDialog();
            sessionDialog.recreateUISharedContexts(Model.getSingleton().getSession());
            final Context uiSharedContext = sessionDialog.getUISharedContext(this.context.getId());

            HttpSessionsPanel panel = extension.getHttpSessionsPanel();
            HttpSession session = panel.getSelectedSession();
            LOGGER.info(
                    "Creating user from HttpSession {} for Context {}",
                    session.getName(),
                    uiSharedContext.getName());

            pendingUsersClearing = false;

            // Do the work/changes on the UI shared context
            // First make sure the authentication method is Manual Authentication
            if (!(uiSharedContext.getAuthenticationMethod()
                    instanceof ManualAuthenticationMethod)) {
                LOGGER.info(
                        "Creating new Manual Authentication instance for Context {}",
                        uiSharedContext.getName());
                ManualAuthenticationMethod method =
                        new ManualAuthenticationMethodType()
                                .createAuthenticationMethod(context.getId());

                if (!confirmUsersDeletion(uiSharedContext)) {
                    LOGGER.debug("Cancelled change of authentication type.");
                    return;
                }

                uiSharedContext.setAuthenticationMethod(method);

                pendingUsersClearing = true;
            }

            newUser = showAddUserDialogue(uiSharedContext, session);
            if (newUser == null) {
                LOGGER.debug("Cancelled creation of user from HttpSession.");
                return;
            }
            LOGGER.info("Created user: {}", newUser);

            // Show the session dialog without recreating UI Shared contexts
            // NOTE: First init the panels of the dialog so old users data gets loaded and just then
            // update the users
            // from the UI data model, otherwise the 'real' users from the non-shared context would
            // be loaded
            // and would override any deletions made.
            View.getSingleton()
                    .showSessionDialog(
                            Model.getSingleton().getSession(),
                            ContextUsersPanel.getPanelName(context.getId()),
                            false,
                            new Runnable() {

                                @Override
                                public void run() {
                                    // Removing the users from the 'shared context' (the UI) will
                                    // cause their removal at
                                    // save as well
                                    if (getExtensionUserManagement() != null) {
                                        if (pendingUsersClearing)
                                            getExtensionUserManagement()
                                                    .removeSharedContextUsers(uiSharedContext);
                                        getExtensionUserManagement()
                                                .addSharedContextUser(uiSharedContext, newUser);
                                    }
                                }
                            });
        }
    }
}
