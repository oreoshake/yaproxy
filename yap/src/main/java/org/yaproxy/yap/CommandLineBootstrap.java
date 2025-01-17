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
package org.yaproxy.yap;

import java.io.FileNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.CommandLine;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.model.Model;
import org.yaproxy.yap.utils.YapSupportUtils;

/**
 * The bootstrap process for command line mode.
 *
 * @since 2.4.2
 */
public class CommandLineBootstrap extends HeadlessBootstrap {

    private static final Logger LOGGER = LogManager.getLogger(CommandLineBootstrap.class);

    public CommandLineBootstrap(CommandLine cmdLineArgs) {
        super(cmdLineArgs);
    }

    @Override
    public int start() {
        int rc = super.start();
        if (rc != 0) {
            return rc;
        }

        if (!getArgs().isNoStdOutLog()) {
            disableStdOutLog();
        }

        LOGGER.info(getStartingMessage());

        try {
            initModel();
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                System.out.println(Constant.messages.getString("start.db.error"));
                System.out.println(e.getLocalizedMessage());
            }

            throw new RuntimeException(e);
        }

        Control control;
        try {
            control = initControl();
        } catch (IllegalStateException e) {
            System.err.println("Failed to start YAP. " + e.getMessage());
            return 1;
        }

        warnAddOnsAndExtensionsNoLongerRunnable();

        try {
            control.getExtensionLoader().hookCommandLineListener(getArgs());
            if (getArgs().isEnabled(CommandLine.HELP) || getArgs().isEnabled(CommandLine.HELP2)) {
                System.out.println(getArgs().getHelp());

            } else if (getArgs().isReportVersion()) {
                System.out.println(Constant.PROGRAM_VERSION);

            } else if (getArgs().isDisplaySupportInfo()) {
                System.out.println(YapSupportUtils.getAll(false));

            } else {
                if (handleCmdLineSessionArgsSynchronously(control)) {
                    control.runCommandLine();

                    try {
                        Thread.sleep(1000);

                    } catch (final InterruptedException e) {
                    }

                } else {
                    rc = 1;
                }
            }
        } catch (ShutdownRequestedException e) {
            rc = 1;
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.out.println(e.getMessage());
            System.out.println();
            // Help is kind of useful too ;)
            System.out.println(getArgs().getHelp());
            rc = 1;

        } finally {
            control.shutdown(
                    Model.getSingleton().getOptionsParam().getDatabaseParam().isCompactDatabase());
            LOGGER.info("{} terminated.", Constant.PROGRAM_TITLE);
        }
        if (rc == 0) {
            rc = control.getExitStatus();
        }

        return rc;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
