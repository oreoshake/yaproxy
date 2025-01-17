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
package org.yaproxy.yap.extension.authorization;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaproxy.yap.model.Context;
import org.yaproxy.yap.utils.YapXmlConfiguration;

/** Unit test for {@link ExtensionAuthorization}. */
class ExtensionAuthorizationUnitTest {

    private ExtensionAuthorization extensionAuthorization;

    @BeforeEach
    void setup() {
        extensionAuthorization = new ExtensionAuthorization();
    }

    @Test
    void shouldImportContextWithNoAuthorizationDetectionMethod() throws ConfigurationException {
        // Given
        Context context = mock(Context.class);
        Configuration config = new YapXmlConfiguration();
        // When
        extensionAuthorization.importContextData(context, config);
        // Then
        verify(context, times(0)).setAuthorizationDetectionMethod(any());
    }

    @Test
    void shouldImportContextWithUnknownAuthorizationDetectionMethod()
            throws ConfigurationException {
        // Given
        Context context = mock(Context.class);
        Configuration config = new YapXmlConfiguration();
        config.setProperty("context.authorization.type", Integer.MIN_VALUE);
        // When
        extensionAuthorization.importContextData(context, config);
        // Then
        verify(context, times(0)).setAuthorizationDetectionMethod(any());
    }
}
