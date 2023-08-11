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
package org.yaproxy.yap.extension.forceduser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.configuration.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parosproxy.paros.Constant;
import org.yaproxy.yap.WithConfigsTest;
import org.yaproxy.yap.extension.users.ExtensionUserManagement;
import org.yaproxy.yap.model.Context;
import org.yaproxy.yap.utils.I18N;
import org.yaproxy.yap.utils.YapXmlConfiguration;

/** Unit test for {@link ExtensionForcedUser}. */
class ExtensionForcedUserUnitTest extends WithConfigsTest {

    private ExtensionForcedUser extensionForcedUser;

    @BeforeEach
    void setup() {
        Constant.messages = mock(I18N.class);

        extensionForcedUser = new ExtensionForcedUser();
    }

    @Test
    void shouldImportContextWithNoForcedUser() {
        // Given
        Context context = mock(Context.class);
        Configuration config = new YapXmlConfiguration();
        // When
        extensionForcedUser.importContextData(context, config);
        // Then
        verify(context, times(0)).getId();
    }

    @Test
    void shouldNotImportContextWithUnknownForcedUser() {
        // Given
        given(extensionLoader.getExtension(ExtensionUserManagement.class))
                .willReturn(new ExtensionUserManagement());
        Context context = mock(Context.class);
        Configuration config = new YapXmlConfiguration();
        config.setProperty("context.forceduser", Integer.MIN_VALUE);
        // When / Then
        assertThrows(
                IllegalStateException.class,
                () -> extensionForcedUser.importContextData(context, config));
    }
}
