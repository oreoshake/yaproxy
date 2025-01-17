/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2020 The YAP Development Team
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
package org.yaproxy.yap.extension.sessions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parosproxy.paros.extension.ExtensionHook;
import org.yaproxy.yap.WithConfigsTest;
import org.yaproxy.yap.model.Context;
import org.yaproxy.yap.session.CookieBasedSessionManagementMethodType.CookieBasedSessionManagementMethod;
import org.yaproxy.yap.session.HttpAuthSessionManagementMethodType.HttpAuthSessionManagementMethod;
import org.yaproxy.yap.session.ScriptBasedSessionManagementMethodType;
import org.yaproxy.yap.session.ScriptBasedSessionManagementMethodType.ScriptBasedSessionManagementMethod;
import org.yaproxy.yap.utils.YapXmlConfiguration;

class ExtensionSessionManagementUnitTest extends WithConfigsTest {

    private ExtensionSessionManagement extSessMgmt;

    @BeforeEach
    void setUp() throws Exception {
        ScriptBasedSessionManagementMethodType.setExtensionScript(null);
        extSessMgmt = new ExtensionSessionManagement();
        extSessMgmt.hook(mock(ExtensionHook.class));
    }

    @Test
    void shouldImportContextWithNoSessionMgmtType() throws ConfigurationException {
        // Given
        Context context = mock(Context.class);
        Configuration config = new YapXmlConfiguration();

        // When
        extSessMgmt.importContextData(context, config);

        // Then
        verify(context, times(0)).setSessionManagementMethod(any());
    }

    @Test
    void shouldImportContextWithCookieSessionMgmtType() throws ConfigurationException {
        // Given
        Context context = mock(Context.class);
        Configuration config = new YapXmlConfiguration();
        int sessMgmtTypeId = 0;
        config.addProperty(ExtensionSessionManagement.CONTEXT_CONFIG_SESSION_TYPE, sessMgmtTypeId);

        // When
        extSessMgmt.importContextData(context, config);

        // Then
        verify(context).setSessionManagementMethod(any(CookieBasedSessionManagementMethod.class));
    }

    @Test
    void shouldImportContextWithHttpSessionMgmtType() throws ConfigurationException {
        // Given
        Context context = mock(Context.class);
        Configuration config = new YapXmlConfiguration();
        int sessMgmtTypeId = 1;
        config.addProperty(ExtensionSessionManagement.CONTEXT_CONFIG_SESSION_TYPE, sessMgmtTypeId);

        // When
        extSessMgmt.importContextData(context, config);

        // Then
        verify(context).setSessionManagementMethod(any(HttpAuthSessionManagementMethod.class));
    }

    @Test
    void shouldImportContextWithScriptSessionMgmtType() throws ConfigurationException {
        // Given
        Context context = mock(Context.class);
        Configuration config = new YapXmlConfiguration();
        int sessMgmtTypeId = 2;
        config.addProperty(ExtensionSessionManagement.CONTEXT_CONFIG_SESSION_TYPE, sessMgmtTypeId);

        // When
        extSessMgmt.importContextData(context, config);

        // Then
        verify(context).setSessionManagementMethod(any(ScriptBasedSessionManagementMethod.class));
    }

    @Test
    void shouldImportContextWithUnknownSessionMgmtType() throws ConfigurationException {
        // Given
        Context context = mock(Context.class);
        Configuration config = new YapXmlConfiguration();
        int sessMgmtTypeId = 100;
        config.addProperty(ExtensionSessionManagement.CONTEXT_CONFIG_SESSION_TYPE, sessMgmtTypeId);

        // When
        extSessMgmt.importContextData(context, config);

        // Then
        verify(context, times(0)).setSessionManagementMethod(any());
    }
}
