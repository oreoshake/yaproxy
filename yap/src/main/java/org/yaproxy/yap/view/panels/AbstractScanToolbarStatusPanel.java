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
package org.yaproxy.yap.view.panels;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.control.Control.Mode;
import org.yaproxy.yap.model.Context;
import org.yaproxy.yap.scan.BaseScannerThread;
import org.yaproxy.yap.scan.BaseScannerThreadManager;
import org.yaproxy.yap.scan.ScanListener;
import org.yaproxy.yap.scan.ScanStartOptions;
import org.yaproxy.yap.utils.ThreadUtils;
import org.yaproxy.yap.view.LayoutHelper;
import org.yaproxy.yap.view.ScanPanel;
import org.yaproxy.yap.view.YapToggleButton;

/**
 * An extended implementation of a {@link AbstractContextSelectToolbarStatusPanel} that should be
 * used for status panels for scans. Contains a toolbar with the following elements: context
 * selection, scan control buttons (start, stop, pause) and a progress bar.
 *
 * <p>This panel should be used in a scan based on a <b>{@link BaseScannerThread}</b>. It also
 * requires a corresponding {@link BaseScannerThreadManager} that is used for obtaining the scanner
 * threads for given contexts. Certain control actions (stop, pause, resume) are being forwarded
 * directly to the {@link BaseScannerThread scanner thread}, while the start action is being left
 * unimplemented to be properly handled by implementing classes.
 *
 * @see BaseScannerThread
 * @see BaseScannerThreadManager
 * @see ScanStartOptions
 */
@SuppressWarnings("serial")
public abstract class AbstractScanToolbarStatusPanel extends AbstractContextSelectToolbarStatusPanel
        implements ScanListener {

    private static final long serialVersionUID = -2351280081989616482L;
    private static final Logger LOGGER = LogManager.getLogger(AbstractScanToolbarStatusPanel.class);

    /**
     * Location provided to {@link #addToolBarElements(JToolBar, short, int)} to add items after the
     * buttons.
     */
    protected static final short TOOLBAR_LOCATION_AFTER_BUTTONS = 10;

    /**
     * Location provided to {@link #addToolBarElements(JToolBar, short, int)} to add items after the
     * progress bar.
     */
    protected static final short TOOLBAR_LOCATION_AFTER_PROGRESS_BAR = 11;

    private JButton startScanButton;
    private JButton stopScanButton;
    private YapToggleButton pauseScanButton;
    private JProgressBar progressBar;

    private Mode mode;
    private BaseScannerThreadManager<?> threadManager;

    public AbstractScanToolbarStatusPanel(
            String prefix, ImageIcon icon, BaseScannerThreadManager<?> threadManager) {
        super(prefix, icon);

        mode = Control.getSingleton().getMode();
        this.threadManager = threadManager;
    }

    @Override
    protected void setupToolbarElements(JToolBar toolbar) {
        // We need to override this method completely to add more components and properly call the
        // addToolbarElements method with the new locations
        int x = 0;
        Insets insets = new Insets(0, 4, 0, 2);

        x = this.addToolBarElements(toolbar, TOOLBAR_LOCATION_START, x);

        toolbar.add(
                new JLabel(Constant.messages.getString(panelPrefix + ".toolbar.context.label")),
                LayoutHelper.getGBC(x++, 0, 1, 0, insets));
        toolbar.add(getContextSelectComboBox(), LayoutHelper.getGBC(x++, 0, 1, 0, insets));

        x = this.addToolBarElements(toolbar, TOOLBAR_LOCATION_AFTER_CONTEXTS_SELECT, x);

        toolbar.add(getStartScanButton(), LayoutHelper.getGBC(x++, 0, 1, 0, insets));
        toolbar.add(getPauseScanButton(), LayoutHelper.getGBC(x++, 0, 1, 0, insets));
        toolbar.add(getStopScanButton(), LayoutHelper.getGBC(x++, 0, 1, 0, insets));

        x = this.addToolBarElements(toolbar, TOOLBAR_LOCATION_AFTER_BUTTONS, x);

        toolbar.add(getProgressBar(), LayoutHelper.getGBC(x++, 0, 1, 1, insets));

        x = this.addToolBarElements(toolbar, TOOLBAR_LOCATION_AFTER_PROGRESS_BAR, x);

        toolbar.add(new JLabel(), LayoutHelper.getGBC(x++, 0, 1, 1.0)); // Spacer
        if (hasOptions()) {
            toolbar.add(getOptionsButton(), LayoutHelper.getGBC(x++, 0, 1, 0, insets));
        }

        this.addToolBarElements(toolbar, TOOLBAR_LOCATION_END, x);
    }

    private JButton getStartScanButton() {
        if (startScanButton == null) {
            startScanButton = new JButton();
            startScanButton.setToolTipText(
                    Constant.messages.getString(panelPrefix + ".toolbar.button.start"));
            startScanButton.setIcon(
                    new ImageIcon(ScanPanel.class.getResource("/resource/icon/16/131.png")));
            startScanButton.setEnabled(false);
            startScanButton.addActionListener(
                    new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            startScan(getSelectedContext());
                        }
                    });
        }
        return startScanButton;
    }

    private JButton getStopScanButton() {
        if (stopScanButton == null) {
            stopScanButton = new JButton();
            stopScanButton.setToolTipText(
                    Constant.messages.getString(panelPrefix + ".toolbar.button.stop"));
            stopScanButton.setIcon(
                    new ImageIcon(ScanPanel.class.getResource("/resource/icon/16/142.png")));
            stopScanButton.setEnabled(false);
            stopScanButton.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            stopScan(getSelectedContext());
                        }
                    });
        }
        return stopScanButton;
    }

    private JToggleButton getPauseScanButton() {
        if (pauseScanButton == null) {
            pauseScanButton = new YapToggleButton();
            pauseScanButton.setToolTipText(
                    Constant.messages.getString(panelPrefix + ".toolbar.button.pause"));
            pauseScanButton.setSelectedToolTipText(
                    Constant.messages.getString(panelPrefix + ".toolbar.button.unpause"));
            pauseScanButton.setIcon(
                    new ImageIcon(ScanPanel.class.getResource("/resource/icon/16/141.png")));
            pauseScanButton.setRolloverIcon(
                    new ImageIcon(ScanPanel.class.getResource("/resource/icon/16/141.png")));
            pauseScanButton.setSelectedIcon(
                    new ImageIcon(ScanPanel.class.getResource("/resource/icon/16/131.png")));
            pauseScanButton.setRolloverSelectedIcon(
                    new ImageIcon(ScanPanel.class.getResource("/resource/icon/16/131.png")));
            pauseScanButton.setEnabled(false);
            pauseScanButton.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (isScanPaused(getSelectedContext())) {
                                resumeScan(getSelectedContext());
                            } else {
                                pauseScan(getSelectedContext());
                            }
                        }
                    });
        }
        return pauseScanButton;
    }

    private JProgressBar getProgressBar() {
        if (progressBar == null) {
            progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setSize(new Dimension(80, 20));
            progressBar.setStringPainted(true);
            progressBar.setEnabled(false);
        }
        return progressBar;
    }

    @Override
    protected void contextSelected(Context context) {
        if (context == null) {
            resetScanButtonsAndProgressBarStates(false);
            super.contextSelected(context);
            return;
        }

        // If we are in 'Safe' mode, there's no need to disable anything as it was already disabled
        // when switching the mode
        if (Mode.safe.equals(this.mode)) {
            super.contextSelected(context);
            return;
        }

        // If context is not in scope and we are in 'Protect' mode, disable scanning.
        if (Mode.protect.equals(this.mode) && !context.isInScope()) {
            resetScanButtonsAndProgressBarStates(false);
            super.contextSelected(context);
            return;
        }

        if (isScanStarted(context)) {
            getStartScanButton().setEnabled(false);
            getStopScanButton().setEnabled(true);
            getPauseScanButton().setEnabled(true);
            getPauseScanButton().setSelected(isScanPaused(context));
            getProgressBar().setEnabled(true);
        } else {
            resetScanButtonsAndProgressBarStates(true);
        }

        getProgressBar().setValue(getScanProgress(context));
        getProgressBar().setMaximum(getScanMaximumProgress(context));

        // Calling super takes care of updating the selectedContext and triggering a work panel view
        // switch
        super.contextSelected(context);
    }

    private void resetScanButtonsAndProgressBarStates(boolean allowStartScan) {
        setScanButtonsAndProgressBarStates(false, false, allowStartScan);
        getProgressBar().setValue(0);
    }

    /**
     * Method used for setting the state of the scan buttons and of the progress bar.
     *
     * <p><strong>NOTE:</strong> Must be called from the main thread (EDT).
     *
     * @param isStarted {@code true} if the scan is started, {@code false} otherwise.
     * @param isPaused {@code true} if the scan is paused, {@code false} otherwise.
     * @param allowStartScan {@code true} if should possible to start a scan, {@code false}
     *     otherwise.
     */
    private void setScanButtonsAndProgressBarStates(
            boolean isStarted, boolean isPaused, boolean allowStartScan) {
        if (isStarted) {
            getStartScanButton().setEnabled(false);
            getPauseScanButton().setEnabled(true);
            getPauseScanButton().setSelected(isPaused);
            getStopScanButton().setEnabled(true);
            getProgressBar().setEnabled(true);
        } else {
            getStartScanButton().setEnabled(allowStartScan);
            getStopScanButton().setEnabled(false);
            getPauseScanButton().setEnabled(false);
            getPauseScanButton().setSelected(false);
            getProgressBar().setEnabled(false);
        }
    }

    public void sessionModeChanged(Mode mode) {
        this.mode = mode;
        switch (mode) {
            case attack:
            case standard:
            case protect:
                // If the mode is standard or protect, make sure everything is set accordingly and
                // 'refresh' the UI if needed
                getContextSelectComboBox().setEnabled(true);
                if (getSelectedContext() != null) {
                    this.contextSelected(getSelectedContext());
                }
                break;
            case safe:
                // If the mode is 'safe', stop scans and disable controls
                resetScanButtonsAndProgressBarStates(false);
                getContextSelectComboBox().setEnabled(false);
        }
    }

    /*
     * Basic implementation for scanner thread related options.
     */
    /**
     * Method called when the pause button is pressed. Base implementation forward the calls to the
     * Scanner Thread that corresponds to the provided Context and obtained via the Thread Manager
     * specified in the constructor.
     *
     * @param context the context whose scan should be paused
     */
    protected void pauseScan(Context context) {
        LOGGER.debug("Access Control pause on Context: {}", context);
        threadManager.getScannerThread(context.getId()).pauseScan();
    }

    /**
     * Method called when the resume button is pressed. Base implementation forward the calls to the
     * Scanner Thread that corresponds to the provided Context and obtained via the Thread Manager
     * specified in the constructor.
     *
     * @param context the context whose scan should be resumed
     */
    protected void resumeScan(Context context) {
        LOGGER.debug("Access Control resume on Context: {}", context);
        threadManager.getScannerThread(context.getId()).resumeScan();
    }

    /**
     * Method called when the stop button is pressed. Base implementation forward the calls to the
     * Scanner Thread that corresponds to the provided Context and obtained via the Thread Manager
     * specified in the constructor.
     *
     * @param context the context whose scan should be stopped
     */
    protected void stopScan(Context context) {
        LOGGER.debug("Access Control stop on Context: {}", context);
        threadManager.getScannerThread(context.getId()).stopScan();
    }

    /**
     * Method called to check whether the scan for a given Context has started. Base implementation
     * forward the calls to the Scanner Thread that corresponds to the provided Context and obtained
     * via the Thread Manager specified in the constructor.
     *
     * @param context the context whose scan should be checked
     * @return {@code true} if the scan is paused, {@code false} otherwise.
     */
    protected boolean isScanStarted(Context context) {
        return threadManager.getScannerThread(context.getId()).isRunning();
    }

    /**
     * Method called to check whether the scan for a given Context is paused. Base implementation
     * forward the calls to the Scanner Thread that corresponds to the provided Context and obtained
     * via the Thread Manager specified in the constructor.
     *
     * @param context the context whose scan should be checked
     * @return {@code true} if the scan is paused, {@code false} otherwise.
     */
    protected boolean isScanPaused(Context context) {
        return threadManager.getScannerThread(context.getId()).isPaused();
    }

    /**
     * Method called to check the scan progress for a given Context. Base implementation forward the
     * calls to the Scanner Thread that corresponds to the provided Context and obtained via the
     * Thread Manager specified in the constructor.
     *
     * @param context the context whose scan should be checked
     * @return the progress
     */
    protected int getScanProgress(Context context) {
        return threadManager.getScannerThread(context.getId()).getScanProgress();
    }

    /**
     * Method called to check the scan maximum progress for a given Context. Base implementation
     * forward the calls to the Scanner Thread that corresponds to the provided Context and obtained
     * via the Thread Manager specified in the constructor.
     *
     * @param context the context whose scan should be checked
     * @return the maximum value of the progress
     */
    protected int getScanMaximumProgress(Context context) {
        return threadManager.getScannerThread(context.getId()).getScanMaximumProgress();
    }

    @Override
    public void scanStarted(final int contextId) {
        Runnable handler =
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.debug("ScanStarted {} on context {}", panelPrefix, contextId);
                        if (getSelectedContext() != null
                                && contextId == getSelectedContext().getId()) {
                            setScanButtonsAndProgressBarStates(true, false, false);
                            getProgressBar().setValue(0);
                        }
                    }
                };

        try {
            ThreadUtils.invokeAndWait(handler);
        } catch (InvocationTargetException | InterruptedException e) {
            LOGGER.error("Error while starting scan: {}", e.getMessage(), e);
        }
    }

    @Override
    public void scanFinished(final int contextId) {
        Runnable handler =
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.debug("ScanFinished {} on context {}", panelPrefix, contextId);
                        if (getSelectedContext() != null
                                && contextId == getSelectedContext().getId()) {
                            setScanButtonsAndProgressBarStates(false, false, true);
                        }
                    }
                };

        try {
            ThreadUtils.invokeAndWait(handler);
        } catch (InvocationTargetException | InterruptedException e) {
            LOGGER.error("Error while finishing scan: {}", e.getMessage(), e);
        }
    }

    @Override
    public void scanProgress(final int contextId, final int progress, final int maximum) {
        Runnable handler =
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.debug(
                                "scanProgress {} on context {} {}",
                                panelPrefix,
                                contextId,
                                progress);
                        if (getSelectedContext() != null
                                && contextId == getSelectedContext().getId()) {
                            getProgressBar().setValue(progress);
                            getProgressBar().setMaximum(maximum);
                        }
                    }
                };

        try {
            ThreadUtils.invokeAndWait(handler);
        } catch (InvocationTargetException | InterruptedException e) {
            LOGGER.error("Error while updating progress: {}", e.getMessage(), e);
        }
    }

    /**
     * Method called when the user has pressed the 'start scan' button.
     *
     * <p>Normally, implementing classes could create a dialog for specifying scan options or
     * directly build the proper {@link ScanStartOptions} and start the {@link BaseScannerThread}.
     *
     * @param context the context selected when starting the scan.
     */
    protected abstract void startScan(Context context);
}
