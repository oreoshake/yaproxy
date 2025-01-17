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
// YAP: 2012/04/14 Changed the method initParam to discard all edits.
// YAP: 2012/04/25 Added @Override annotation to all appropriate methods.
// YAP: 2012/12/18 Issue 441: Dont access view in daemon mode
// YAP: 2013/01/04 Added field txtSslTunnelingPorts below txtTimeoutInSecs.
// YAP: 2013/01/30 Issue 478: Allow to choose to send YAP's managed cookies on
// a single Cookie request header and set it as the default
// YAP: 2013/12/13 Issue 939: YAP should accept SSL connections on non-standard ports automatically
// YAP: 2014/03/23 Issue 416: Normalise how multiple related options are managed throughout YAP
// and enhance the usability of some options
// YAP: 2014/03/23 Issue 968: Allow to choose the enabled SSL/TLS protocols
// YAP: 2015/02/10 Issue 1528: Support user defined font size
// YAP: 2015/08/07 Issue 1768: Update to use a more recent default user agent
// YAP: 2016/03/08 Issue 646: Outgoing proxy password as JPasswordField (pips) instead of
// YapTextField
// YAP: 2016/03/18 Add checkbox to allow showing of the password
// YAP: 2016/08/08 Issue 2742: Allow for override/customization of Java's "networkaddress.cache.ttl"
// value
// YAP: 2017/05/02 Checkbox to Enable / Disable HTTP State
// YAP: 2017/06/19 Use YapNumberSpinner for connection timeout.
// YAP: 2019/06/01 Normalise line endings.
// YAP: 2019/06/05 Normalise format/style.
// YAP: 2020/03/24 Remove hardcoded white background on some fields (part of Issue 5542).
// YAP: 2020/03/25 Remove hardcoded colour in titled borders (Issue 5542).
// YAP: 2020/04/20 Add SocksProxyPanel (Issue 29).
// YAP: 2021/05/14 Remove redundant type arguments.
// YAP: 2022/05/04 Remove single cookie request header option.
// YAP: 2022/05/20 Deprecate the class.
package org.parosproxy.paros.extension.option;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.SortOrder;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.model.OptionsParam;
import org.parosproxy.paros.view.AbstractParamPanel;
import org.parosproxy.paros.view.View;
import org.yaproxy.yap.network.DomainMatcher;
import org.yaproxy.yap.utils.FontUtils;
import org.yaproxy.yap.utils.YapNumberSpinner;
import org.yaproxy.yap.utils.YapPortNumberSpinner;
import org.yaproxy.yap.utils.YapTextField;
import org.yaproxy.yap.view.AbstractMultipleOptionsTablePanel;
import org.yaproxy.yap.view.LayoutHelper;

/**
 * @deprecated (2.12.0) No longer in use.
 */
@Deprecated
public class OptionsConnectionPanel extends AbstractParamPanel {
    // YAP: i18n
    private static final long serialVersionUID = 1L;
    private JCheckBox chkUseProxyChain = null;
    private JPanel jPanel = null;
    private JPanel panelProxyAuth = null;
    // private OptionsParam optionsParam = null;
    private JPanel panelProxyChain = null;
    private YapTextField txtProxyChainName = null;
    // YAP: Do not allow invalid port numbers
    private YapPortNumberSpinner spinnerProxyChainPort = null;
    private YapTextField txtProxyChainRealm = null;
    private YapTextField txtProxyChainUserName = null;
    private JPasswordField txtProxyChainPassword = null;
    private JCheckBox chkShowPassword = null;
    private JCheckBox chkProxyChainAuth = null;
    // YAP: Added prompt option and timeout in secs
    private JCheckBox chkProxyChainPrompt = null;
    private YapNumberSpinner spinnerTimeoutInSecs;
    private JPanel panelGeneral = null;
    private JCheckBox checkBoxHttpStateEnabled;
    private JComboBox<String> commonUserAgents = null;
    private YapTextField defaultUserAgent = null;

    private JPanel dnsPanel;
    private YapNumberSpinner dnsTtlSuccessfulQueriesNumberSpinner;

    private SecurityProtocolsPanel securityProtocolsPanel;

    private ProxyExcludedDomainsMultipleOptionsPanel proxyExcludedDomainsPanel;
    private ProxyExcludedDomainsTableModel proxyExcludedDomainsTableModel;

    private final org.yaproxy.yap.extension.option.SocksProxyPanel socksProxyPanel;

    public OptionsConnectionPanel() {
        super();
        socksProxyPanel = new org.yaproxy.yap.extension.option.SocksProxyPanel();
        initialize();
    }

    /**
     * This method initializes chkShowPassword
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getChkShowPassword() {
        if (chkShowPassword == null) {
            chkShowPassword = new JCheckBox();
            chkShowPassword.setText(
                    Constant.messages.getString("conn.options.proxy.auth.showpass"));
            chkShowPassword.addActionListener(
                    new java.awt.event.ActionListener() {

                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            if (chkShowPassword.isSelected()) {
                                txtProxyChainPassword.setEchoChar((char) 0);
                            } else {
                                txtProxyChainPassword.setEchoChar('*');
                            }
                        }
                    });
        }
        return chkShowPassword;
    }

    /**
     * This method initializes chkUseProxyChain
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getChkUseProxyChain() {
        if (chkUseProxyChain == null) {
            chkUseProxyChain = new JCheckBox();
            chkUseProxyChain.setText(Constant.messages.getString("conn.options.useProxy"));
            chkUseProxyChain.addActionListener(
                    new java.awt.event.ActionListener() {

                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            setProxyChainEnabled(chkUseProxyChain.isSelected());
                        }
                    });
        }
        return chkUseProxyChain;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            java.awt.GridBagConstraints gridBagConstraints71 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints61 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints51 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints41 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints3 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints2 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints15 = new GridBagConstraints();

            javax.swing.JLabel jLabel7 = new JLabel();

            javax.swing.JLabel jLabel6 = new JLabel();

            javax.swing.JLabel jLabel5 = new JLabel();

            jPanel = new JPanel();
            jPanel.setLayout(new GridBagLayout());
            jLabel5.setText(Constant.messages.getString("conn.options.proxy.address"));
            jLabel6.setText(Constant.messages.getString("conn.options.proxy.port"));
            jLabel7.setText(Constant.messages.getString("conn.options.proxy.skipAddresses"));
            gridBagConstraints15.gridx = 0;
            gridBagConstraints15.gridy = 0;
            gridBagConstraints15.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints15.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.gridwidth = 2;
            gridBagConstraints15.weightx = 1.0D;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.weightx = 0.5D;
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 0.5D;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints3.ipadx = 50;
            gridBagConstraints41.gridx = 0;
            gridBagConstraints41.gridy = 2;
            gridBagConstraints41.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints41.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints41.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints41.weightx = 0.5D;
            gridBagConstraints51.gridx = 1;
            gridBagConstraints51.gridy = 2;
            gridBagConstraints51.weightx = 0.5D;
            gridBagConstraints51.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints51.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints51.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints51.ipadx = 50;
            gridBagConstraints61.gridx = 0;
            gridBagConstraints61.gridy = 3;
            gridBagConstraints61.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints61.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints61.weightx = 1.0D;
            gridBagConstraints61.gridwidth = 2;
            gridBagConstraints61.anchor = java.awt.GridBagConstraints.NORTHEAST;
            gridBagConstraints71.gridx = 0;
            gridBagConstraints71.gridy = 4;
            gridBagConstraints71.weightx = 1.0D;
            gridBagConstraints71.weighty = 0.2D;
            gridBagConstraints71.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints71.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints71.anchor = java.awt.GridBagConstraints.NORTHEAST;
            gridBagConstraints71.gridwidth = 2;
            gridBagConstraints71.ipady = 20;
            jPanel.setBorder(
                    javax.swing.BorderFactory.createTitledBorder(
                            null,
                            Constant.messages.getString("conn.options.proxy.useProxyChain"),
                            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                            javax.swing.border.TitledBorder.DEFAULT_POSITION,
                            FontUtils.getFont(FontUtils.Size.standard)));
            jPanel.add(getChkUseProxyChain(), gridBagConstraints15);
            jPanel.add(jLabel5, gridBagConstraints2);
            jPanel.add(getTxtProxyChainName(), gridBagConstraints3);
            jPanel.add(jLabel6, gridBagConstraints41);
            jPanel.add(getTxtProxyChainPort(), gridBagConstraints51);
            jPanel.add(jLabel7, gridBagConstraints61);
            jPanel.add(getProxyExcludedDomainsPanel(), gridBagConstraints71);
        }
        return jPanel;
    }

    /**
     * This method initializes panelProxyAuth
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPanelProxyAuth() {
        if (panelProxyAuth == null) {
            java.awt.GridBagConstraints gridBagConstraints82 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints72 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints62 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints52 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints42 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints31 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints21 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints16 = new GridBagConstraints();

            java.awt.GridBagConstraints gridBagConstraints17 = new GridBagConstraints();

            javax.swing.JLabel jLabel11 = new JLabel();

            javax.swing.JLabel jLabel10 = new JLabel();

            javax.swing.JLabel jLabel9 = new JLabel();

            panelProxyAuth = new JPanel();
            panelProxyAuth.setLayout(new GridBagLayout());
            jLabel9.setText(Constant.messages.getString("conn.options.proxy.auth.realm"));
            jLabel10.setText(Constant.messages.getString("conn.options.proxy.auth.username"));
            jLabel11.setText(Constant.messages.getString("conn.options.proxy.auth.password"));
            panelProxyAuth.setBorder(
                    javax.swing.BorderFactory.createTitledBorder(
                            null,
                            Constant.messages.getString("conn.options.proxy.auth.auth"),
                            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                            javax.swing.border.TitledBorder.DEFAULT_POSITION,
                            FontUtils.getFont(FontUtils.Size.standard)));
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.gridy = 0;
            gridBagConstraints16.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints16.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.gridwidth = 2;
            gridBagConstraints16.weightx = 1.0D;
            gridBagConstraints17.gridx = 0;
            gridBagConstraints17.gridy = 1;
            gridBagConstraints17.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints17.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.gridwidth = 2;
            gridBagConstraints17.weightx = 1.0D;
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.gridy = 2;
            gridBagConstraints21.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.weightx = 0.5D;
            gridBagConstraints31.gridx = 1;
            gridBagConstraints31.gridy = 2;
            gridBagConstraints31.weightx = 0.5D;
            gridBagConstraints31.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints31.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints31.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints31.ipadx = 50;
            gridBagConstraints42.gridx = 0;
            gridBagConstraints42.gridy = 3;
            gridBagConstraints42.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints42.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints42.weightx = 0.5D;
            gridBagConstraints42.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints52.gridx = 1;
            gridBagConstraints52.gridy = 3;
            gridBagConstraints52.weightx = 0.5D;
            gridBagConstraints52.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints52.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints52.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints52.ipadx = 50;
            gridBagConstraints62.gridx = 0;
            gridBagConstraints62.gridy = 4;
            gridBagConstraints62.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints62.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints62.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints62.weightx = 0.5D;
            gridBagConstraints72.gridx = 1;
            gridBagConstraints72.gridy = 4;
            gridBagConstraints72.weightx = 0.5D;
            gridBagConstraints72.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints72.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints72.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints72.ipadx = 50;
            gridBagConstraints82.gridx = 1;
            gridBagConstraints82.gridy = 5;
            gridBagConstraints82.weightx = 0.5D;
            gridBagConstraints82.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints82.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints82.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints82.ipadx = 50;
            panelProxyAuth.add(getChkProxyChainAuth(), gridBagConstraints16);
            panelProxyAuth.add(getChkProxyChainPrompt(), gridBagConstraints17);
            panelProxyAuth.add(jLabel9, gridBagConstraints21);
            panelProxyAuth.add(getTxtProxyChainRealm(), gridBagConstraints31);
            panelProxyAuth.add(jLabel10, gridBagConstraints42);
            panelProxyAuth.add(getTxtProxyChainUserName(), gridBagConstraints52);
            panelProxyAuth.add(jLabel11, gridBagConstraints62);
            panelProxyAuth.add(getTxtProxyChainPassword(), gridBagConstraints72);
            panelProxyAuth.add(getChkShowPassword(), gridBagConstraints82);
        }
        return panelProxyAuth;
    }

    /**
     * This method initializes panelProxyChain
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPanelProxyChain() {
        if (panelProxyChain == null) {
            panelProxyChain = new JPanel(new BorderLayout());
            panelProxyChain.setName("ProxyChain");
            JPanel innerPanel = new JPanel(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = 0;
            gbc.insets = new java.awt.Insets(2, 2, 2, 2);
            gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0D;

            innerPanel.add(getPanelGeneral(), gbc);
            innerPanel.add(getDnsPanel(), gbc);
            innerPanel.add(getSecurityProtocolsPanel(), gbc);
            innerPanel.add(getJPanel(), gbc);
            innerPanel.add(getPanelProxyAuth(), gbc);
            innerPanel.add(socksProxyPanel, gbc);

            JScrollPane scrollPane = new JScrollPane(innerPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            panelProxyChain.add(scrollPane, BorderLayout.CENTER);
        }
        return panelProxyChain;
    }

    private JPanel getDnsPanel() {
        if (dnsPanel == null) {
            dnsPanel = new JPanel();
            dnsPanel.setBorder(
                    javax.swing.BorderFactory.createTitledBorder(
                            null,
                            Constant.messages.getString("conn.options.dns.title"),
                            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                            javax.swing.border.TitledBorder.DEFAULT_POSITION,
                            FontUtils.getFont(FontUtils.Size.standard)));

            GroupLayout layout = new GroupLayout(dnsPanel);
            dnsPanel.setLayout(layout);
            layout.setAutoCreateGaps(true);

            JLabel valueLabel =
                    new JLabel(
                            Constant.messages.getString(
                                    "conn.options.dns.ttlSuccessfulQueries.label"));
            valueLabel.setToolTipText(
                    Constant.messages.getString("conn.options.dns.ttlSuccessfulQueries.toolTip"));
            valueLabel.setLabelFor(getDnsTtlSuccessfulQueriesNumberSpinner());

            layout.setHorizontalGroup(
                    layout.createSequentialGroup()
                            .addComponent(valueLabel)
                            .addComponent(getDnsTtlSuccessfulQueriesNumberSpinner()));

            layout.setVerticalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(valueLabel)
                            .addComponent(getDnsTtlSuccessfulQueriesNumberSpinner()));
        }
        return dnsPanel;
    }

    private YapNumberSpinner getDnsTtlSuccessfulQueriesNumberSpinner() {
        if (dnsTtlSuccessfulQueriesNumberSpinner == null) {
            dnsTtlSuccessfulQueriesNumberSpinner =
                    new YapNumberSpinner(
                            -1,
                            org.parosproxy.paros.network.ConnectionParam
                                    .DNS_DEFAULT_TTL_SUCCESSFUL_QUERIES,
                            Integer.MAX_VALUE);
        }
        return dnsTtlSuccessfulQueriesNumberSpinner;
    }

    /**
     * This method initializes txtProxyChainName
     *
     * @return org.yaproxy.yap.utils.YapTextField
     */
    private YapTextField getTxtProxyChainName() {
        if (txtProxyChainName == null) {
            txtProxyChainName = new YapTextField();
        }
        return txtProxyChainName;
    }

    /**
     * This method initializes spinnerProxyChainPort
     *
     * @return YapPortNumberSpinner
     */
    private YapPortNumberSpinner getTxtProxyChainPort() {
        if (spinnerProxyChainPort == null) {
            // YAP: Do not allow invalid port numbers
            spinnerProxyChainPort = new YapPortNumberSpinner(8080);
        }
        return spinnerProxyChainPort;
    }

    private JComboBox<String> getCommonUserAgents() {
        if (commonUserAgents == null) {
            commonUserAgents = new JComboBox<>(org.yaproxy.yap.model.CommonUserAgents.getNames());
            if (commonUserAgents.getItemCount() == 0) {
                commonUserAgents.setEnabled(false);
            } else {
                commonUserAgents.addItem("");
                commonUserAgents.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String item = (String) commonUserAgents.getSelectedItem();
                                String ua =
                                        org.yaproxy.yap.model.CommonUserAgents.getStringFromName(
                                                item);
                                if (ua != null) {
                                    getDefaultUserAgent().setText(ua);
                                }
                            }
                        });
            }
        }
        return commonUserAgents;
    }

    private YapTextField getDefaultUserAgent() {
        if (defaultUserAgent == null) {
            defaultUserAgent = new YapTextField();
            defaultUserAgent.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            setUaFromString();
                        }
                    });
            defaultUserAgent.addKeyListener(
                    new KeyListener() {
                        @Override
                        public void keyTyped(KeyEvent e) {}

                        @Override
                        public void keyPressed(KeyEvent e) {}

                        @Override
                        public void keyReleased(KeyEvent e) {
                            setUaFromString();
                        }
                    });
        }
        return defaultUserAgent;
    }

    private void setUaFromString() {
        String name =
                org.yaproxy.yap.model.CommonUserAgents.getNameFromString(
                        getDefaultUserAgent().getText());
        if (name != null) {
            getCommonUserAgents().setSelectedItem(name);
        } else {
            getCommonUserAgents().setSelectedItem("");
        }
    }

    /** This method initializes this */
    private void initialize() {
        this.setLayout(new CardLayout());
        this.setName(Constant.messages.getString("conn.options.title"));

        this.add(getPanelProxyChain(), getPanelProxyChain().getName());
    }

    @Override
    public void initParam(Object obj) {

        OptionsParam optionsParam = (OptionsParam) obj;
        org.parosproxy.paros.network.ConnectionParam connectionParam =
                optionsParam.getConnectionParam();

        this.spinnerTimeoutInSecs.setValue(connectionParam.getTimeoutInSecs());

        checkBoxHttpStateEnabled.setSelected(connectionParam.isHttpStateEnabled());

        getProxyExcludedDomainsTableModel()
                .setExcludedDomains(connectionParam.getProxyExcludedDomains());
        getProxyExcludedDomainsPanel()
                .setRemoveWithoutConfirmation(
                        !connectionParam.isConfirmRemoveProxyExcludedDomain());

        chkUseProxyChain.setSelected(connectionParam.isUseProxyChain());

        // set Proxy Chain parameters
        txtProxyChainName.setText(connectionParam.getProxyChainName());
        txtProxyChainName.discardAllEdits();
        // YAP: Do not allow invalid port numbers
        spinnerProxyChainPort.setValue(connectionParam.getProxyChainPort());

        chkProxyChainAuth.setSelected(connectionParam.isUseProxyChainAuth());

        txtProxyChainRealm.setText(connectionParam.getProxyChainRealm());
        txtProxyChainRealm.discardAllEdits();
        txtProxyChainUserName.setText(connectionParam.getProxyChainUserName());
        txtProxyChainUserName.discardAllEdits();

        chkProxyChainPrompt.setSelected(connectionParam.isProxyChainPrompt());
        chkShowPassword.setSelected(false); // Default don't show (everytime)
        txtProxyChainPassword.setEchoChar('*'); // Default mask (everytime)

        setProxyChainEnabled(connectionParam.isUseProxyChain());

        if (!connectionParam.isProxyChainPrompt()) {
            txtProxyChainPassword.setText(connectionParam.getProxyChainPassword());
        }

        dnsTtlSuccessfulQueriesNumberSpinner.setValue(connectionParam.getDnsTtlSuccessfulQueries());

        securityProtocolsPanel.setSecurityProtocolsEnabled(
                connectionParam.getSecurityProtocolsEnabled());

        defaultUserAgent.setText(connectionParam.getDefaultUserAgent());
        setUaFromString();

        socksProxyPanel.initParam(connectionParam);
    }

    private void setProxyChainEnabled(boolean isEnabled) {
        txtProxyChainName.setEnabled(isEnabled);
        spinnerProxyChainPort.setEnabled(isEnabled);
        getProxyExcludedDomainsPanel().setComponentEnabled(isEnabled);
        chkProxyChainAuth.setEnabled(isEnabled);
        setProxyChainAuthEnabled(isEnabled && chkProxyChainAuth.isSelected());
    }

    private void setProxyChainAuthEnabled(boolean isEnabled) {

        txtProxyChainRealm.setEnabled(isEnabled);
        txtProxyChainUserName.setEnabled(isEnabled);
        txtProxyChainPassword.setEnabled(isEnabled);
        // YAP: Added prompt option
        chkProxyChainPrompt.setEnabled(isEnabled);
        chkShowPassword.setEnabled(isEnabled);

        if (chkProxyChainPrompt.isSelected()) {
            setProxyChainPromptEnabled(true);
        }
    }

    private void setProxyChainPromptEnabled(boolean isEnabled) {

        txtProxyChainPassword.setEnabled(!isEnabled);
        chkShowPassword.setEnabled(!isEnabled);
    }

    @Override
    public void validateParam(Object obj) throws Exception {

        if (chkUseProxyChain.isSelected()) {
            // YAP: empty proxy name validation
            if (txtProxyChainName.getText().isEmpty()) {
                txtProxyChainName.requestFocus();
                throw new Exception(
                        Constant.messages.getString("conn.options.proxy.address.empty"));
            }

            if (chkProxyChainAuth.isSelected()
                    && !chkProxyChainPrompt.isSelected()
                    && txtProxyChainUserName.getText().isEmpty()) {
                txtProxyChainUserName.requestFocus();
                throw new Exception(
                        Constant.messages.getString("conn.options.proxy.username.empty"));
            }
        }

        securityProtocolsPanel.validateSecurityProtocols();

        socksProxyPanel.validateParam();
    }

    @Override
    public void saveParam(Object obj) throws Exception {

        OptionsParam optionsParam = (OptionsParam) obj;
        org.parosproxy.paros.network.ConnectionParam connectionParam =
                optionsParam.getConnectionParam();

        connectionParam.setProxyChainName(txtProxyChainName.getText());
        // YAP: Do not allow invalid port numbers
        connectionParam.setProxyChainPort(spinnerProxyChainPort.getValue());

        connectionParam.setProxyExcludedDomains(getProxyExcludedDomainsTableModel().getElements());
        connectionParam.setConfirmRemoveProxyExcludedDomain(
                !getProxyExcludedDomainsPanel().isRemoveWithoutConfirmation());

        connectionParam.setProxyChainRealm(txtProxyChainRealm.getText());
        connectionParam.setProxyChainUserName(txtProxyChainUserName.getText());
        connectionParam.setProxyChainPrompt(chkProxyChainPrompt.isSelected());
        // YAP: Added prompt option
        if (chkUseProxyChain.isSelected()
                && chkProxyChainAuth.isSelected()
                && chkProxyChainPrompt.isSelected()) {
            if (View.isInitialised()) {
                // And prompt now
                org.yaproxy.yap.view.ProxyDialog dialog =
                        new org.yaproxy.yap.view.ProxyDialog(
                                View.getSingleton().getMainFrame(), true);
                dialog.init(Model.getSingleton().getOptionsParam());
                dialog.setVisible(true);
            }

        } else {
            connectionParam.setProxyChainPassword(new String(txtProxyChainPassword.getPassword()));
        }
        connectionParam.setTimeoutInSecs(spinnerTimeoutInSecs.getValue());
        connectionParam.setHttpStateEnabled(checkBoxHttpStateEnabled.isSelected());

        connectionParam.setUseProxyChain(chkUseProxyChain.isSelected());
        connectionParam.setUseProxyChainAuth(chkProxyChainAuth.isSelected());

        connectionParam.setDnsTtlSuccessfulQueries(dnsTtlSuccessfulQueriesNumberSpinner.getValue());

        connectionParam.setSecurityProtocolsEnabled(securityProtocolsPanel.getSelectedProtocols());

        connectionParam.setDefaultUserAgent(defaultUserAgent.getText());

        socksProxyPanel.saveParam(connectionParam);
    }

    /**
     * This method initializes txtProxyChainRealm
     *
     * @return org.yaproxy.yap.utils.YapTextField
     */
    private YapTextField getTxtProxyChainRealm() {
        if (txtProxyChainRealm == null) {
            txtProxyChainRealm = new YapTextField();
        }
        return txtProxyChainRealm;
    }

    /**
     * This method initializes txtProxyChainUserName
     *
     * @return org.yaproxy.yap.utils.YapTextField
     */
    private YapTextField getTxtProxyChainUserName() {
        if (txtProxyChainUserName == null) {
            txtProxyChainUserName = new YapTextField();
        }
        return txtProxyChainUserName;
    }

    /**
     * This method initializes txtProxyChainPassword
     *
     * @return The field that will be used to gather the password from the user
     */
    private JPasswordField getTxtProxyChainPassword() {
        if (txtProxyChainPassword == null) {
            txtProxyChainPassword = new JPasswordField();
        }
        return txtProxyChainPassword;
    }

    /**
     * This method initializes chkProxyChainAuth
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getChkProxyChainAuth() {
        if (chkProxyChainAuth == null) {
            chkProxyChainAuth = new JCheckBox();
            chkProxyChainAuth.setText(
                    Constant.messages.getString("conn.options.proxy.auth.required"));
            chkProxyChainAuth.addActionListener(
                    new java.awt.event.ActionListener() {

                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {

                            setProxyChainAuthEnabled(chkProxyChainAuth.isSelected());
                        }
                    });
        }
        return chkProxyChainAuth;
    }

    private JCheckBox getChkProxyChainPrompt() {
        if (chkProxyChainPrompt == null) {
            chkProxyChainPrompt = new JCheckBox();
            chkProxyChainPrompt.setText(
                    Constant.messages.getString("conn.options.proxy.auth.prompt"));
            chkProxyChainPrompt.addActionListener(
                    new java.awt.event.ActionListener() {

                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {

                            setProxyChainPromptEnabled(chkProxyChainPrompt.isSelected());
                        }
                    });
        }
        return chkProxyChainPrompt;
    }

    private JPanel getPanelGeneral() {
        if (panelGeneral == null) {
            java.awt.GridBagConstraints gridBagConstraints01 = new GridBagConstraints();
            java.awt.GridBagConstraints gridBagConstraints00 = new GridBagConstraints();

            javax.swing.JLabel jLabel = new JLabel();

            panelGeneral = new JPanel();
            panelGeneral.setLayout(new GridBagLayout());
            jLabel.setText(Constant.messages.getString("conn.options.timeout"));
            jLabel.setLabelFor(getTxtTimeoutInSecs());

            panelGeneral.setBorder(
                    javax.swing.BorderFactory.createTitledBorder(
                            null,
                            Constant.messages.getString("conn.options.general"),
                            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                            javax.swing.border.TitledBorder.DEFAULT_POSITION,
                            FontUtils.getFont(FontUtils.Size.standard)));

            gridBagConstraints00.gridx = 0;
            gridBagConstraints00.gridy = 0;
            gridBagConstraints00.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints00.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints00.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints00.weightx = 0.5D;

            gridBagConstraints01.gridx = 1;
            gridBagConstraints01.gridy = 0;
            gridBagConstraints01.weightx = 0.5D;
            gridBagConstraints01.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints01.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints01.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints01.ipadx = 50;

            panelGeneral.add(jLabel, gridBagConstraints00);
            panelGeneral.add(getTxtTimeoutInSecs(), gridBagConstraints01);

            JLabel uaLabel =
                    new JLabel(Constant.messages.getString("conn.options.defaultUserAgent"));
            uaLabel.setLabelFor(this.getDefaultUserAgent());
            panelGeneral.add(uaLabel, LayoutHelper.getGBC(0, 1, 1, 0.5D));
            panelGeneral.add(
                    getCommonUserAgents(),
                    LayoutHelper.getGBC(1, 1, 1, 0.5D, new Insets(2, 2, 2, 2)));
            panelGeneral.add(
                    getDefaultUserAgent(),
                    LayoutHelper.getGBC(0, 2, 2, 1.0D, new Insets(2, 2, 2, 2)));

            panelGeneral.add(
                    getCheckBoxHttpStateEnabled(),
                    LayoutHelper.getGBC(
                            0,
                            3,
                            3,
                            1.0D,
                            0,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(16, 2, 2, 2)));
        }
        return panelGeneral;
    }

    private SecurityProtocolsPanel getSecurityProtocolsPanel() {

        if (securityProtocolsPanel == null) {
            securityProtocolsPanel = new SecurityProtocolsPanel();
        }
        return securityProtocolsPanel;
    }

    private YapNumberSpinner getTxtTimeoutInSecs() {
        if (spinnerTimeoutInSecs == null) {
            spinnerTimeoutInSecs =
                    new YapNumberSpinner(
                            0,
                            org.parosproxy.paros.network.ConnectionParam.DEFAULT_TIMEOUT,
                            Integer.MAX_VALUE);
        }
        return spinnerTimeoutInSecs;
    }

    public JCheckBox getCheckBoxHttpStateEnabled() {

        if (checkBoxHttpStateEnabled == null) {
            checkBoxHttpStateEnabled =
                    new JCheckBox(Constant.messages.getString("conn.options.httpStateEnabled"));
        }
        return checkBoxHttpStateEnabled;
    }

    public void setCheckBoxHttpStateEnabled(JCheckBox checkBoxHttpStateEnabled) {

        this.checkBoxHttpStateEnabled = checkBoxHttpStateEnabled;
    }

    private ProxyExcludedDomainsMultipleOptionsPanel getProxyExcludedDomainsPanel() {

        if (proxyExcludedDomainsPanel == null) {
            proxyExcludedDomainsPanel =
                    new ProxyExcludedDomainsMultipleOptionsPanel(
                            getProxyExcludedDomainsTableModel());
        }
        return proxyExcludedDomainsPanel;
    }

    private ProxyExcludedDomainsTableModel getProxyExcludedDomainsTableModel() {

        if (proxyExcludedDomainsTableModel == null) {
            proxyExcludedDomainsTableModel = new ProxyExcludedDomainsTableModel();
        }
        return proxyExcludedDomainsTableModel;
    }

    @Override
    public String getHelpIndex() {
        // YAP: added help index
        return "ui.dialogs.options.connection";
    }

    private static class ProxyExcludedDomainsMultipleOptionsPanel
            extends AbstractMultipleOptionsTablePanel<DomainMatcher> {

        private static final long serialVersionUID = 2332044353650231701L;

        private static final String REMOVE_DIALOG_TITLE =
                Constant.messages.getString(
                        "conn.options.proxy.excluded.domain.dialog.remove.title");
        private static final String REMOVE_DIALOG_TEXT =
                Constant.messages.getString(
                        "conn.options.proxy.excluded.domain.dialog.remove.text");

        private static final String REMOVE_DIALOG_CONFIRM_BUTTON_LABEL =
                Constant.messages.getString(
                        "conn.options.proxy.excluded.domain.dialog.remove.button.confirm");
        private static final String REMOVE_DIALOG_CANCEL_BUTTON_LABEL =
                Constant.messages.getString(
                        "conn.options.proxy.excluded.domain.dialog.remove.button.cancel");

        private static final String REMOVE_DIALOG_CHECKBOX_LABEL =
                Constant.messages.getString(
                        "conn.options.proxy.excluded.domain.dialog.remove.checkbox.label");

        private DialogAddProxyExcludedDomain addDialog = null;
        private DialogModifyProxyExcludedDomain modifyDialog = null;

        public ProxyExcludedDomainsMultipleOptionsPanel(ProxyExcludedDomainsTableModel model) {
            super(model);

            getTable().setVisibleRowCount(5);
            getTable().setSortOrder(2, SortOrder.ASCENDING);
        }

        @Override
        public DomainMatcher showAddDialogue() {
            if (addDialog == null) {
                addDialog =
                        new DialogAddProxyExcludedDomain(
                                View.getSingleton().getOptionsDialog(null));
                addDialog.pack();
            }
            addDialog.setVisible(true);

            DomainMatcher hostAuthentication = addDialog.getProxyExcludedDomain();
            addDialog.clear();

            return hostAuthentication;
        }

        @Override
        public DomainMatcher showModifyDialogue(DomainMatcher e) {
            if (modifyDialog == null) {
                modifyDialog =
                        new DialogModifyProxyExcludedDomain(
                                View.getSingleton().getOptionsDialog(null));
                modifyDialog.pack();
            }
            modifyDialog.setProxyExcludedDomain(e);
            modifyDialog.setVisible(true);

            DomainMatcher excludedDomain = modifyDialog.getProxyExcludedDomain();
            modifyDialog.clear();

            if (!excludedDomain.equals(e)) {
                return excludedDomain;
            }

            return null;
        }

        @Override
        public boolean showRemoveDialogue(DomainMatcher e) {
            JCheckBox removeWithoutConfirmationCheckBox =
                    new JCheckBox(REMOVE_DIALOG_CHECKBOX_LABEL);
            Object[] messages = {REMOVE_DIALOG_TEXT, " ", removeWithoutConfirmationCheckBox};
            int option =
                    JOptionPane.showOptionDialog(
                            View.getSingleton().getMainFrame(),
                            messages,
                            REMOVE_DIALOG_TITLE,
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[] {
                                REMOVE_DIALOG_CONFIRM_BUTTON_LABEL,
                                REMOVE_DIALOG_CANCEL_BUTTON_LABEL
                            },
                            null);

            if (option == JOptionPane.OK_OPTION) {
                setRemoveWithoutConfirmation(removeWithoutConfirmationCheckBox.isSelected());

                return true;
            }

            return false;
        }
    }
} //  @jve:decl-index=0:visual-constraint="10,10"
