/*
 *
 * Paros and its related class files.
 *
 * Paros is an HTTP/HTTPS proxy for assessing web application security.
 * Copyright (C) 2005 Chinotec Technologies Company
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
// YAP: 2012/12/19 Code Cleanup: Moved array brackets from variable name to type
// YAP: 2013/03/03 Issue 546: Remove all template Javadoc comments
// YAP: 2013/05/02 Removed redundant public modifiers from interface method declarations
// YAP: 2013/12/03 Issue 934: Handle files on the command line via extension
// YAP: 2019/06/01 Normalise line endings.
// YAP: 2019/06/05 Normalise format/style.
// YAP: 2022/05/02 Document usage of ShutdownRequestedException.
// YAP: 2022/08/17 Added preExecute.
package org.parosproxy.paros.extension;

import java.io.File;
import java.util.List;
import org.yaproxy.yap.ShutdownRequestedException;

public interface CommandLineListener {
    /**
     * Execute the command line using the argument provided.
     *
     * @param args the command line arguments
     * @throws ShutdownRequestedException (since 2.12.0) if YAP should shutdown immediately.
     */
    void execute(CommandLineArgument[] args);

    /**
     * Execute any command line args that need to be run before the others. This should typically
     * only be done by specific core add-ons, such as ExtensionAutoUpdate.
     *
     * @since 2.12.0
     * @param args the command line arguments
     * @throws ShutdownRequestedException if YAP should shutdown immediately.
     */
    default void preExecute(CommandLineArgument[] args) {}

    /**
     * Handle the specified file (in whatever way is appropriate). This will only be called for
     * files specified on the command line without switches and which match one of the extensions
     * returned by getHandledExtensions()
     *
     * @param file the file provided through the command line
     * @return true if the listener handled the file, false otherwise
     */
    boolean handleFile(File file);

    /**
     * Get the list of extensions this listener can handle
     *
     * @return a {@code List} with the handled extensions
     */
    List<String> getHandledExtensions();
}
