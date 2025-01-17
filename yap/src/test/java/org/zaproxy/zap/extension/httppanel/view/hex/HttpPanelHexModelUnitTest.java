/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2012 The YAP Development Team
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
package org.yaproxy.yap.extension.httppanel.view.hex;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpPanelHexModelUnitTest {

    private HttpPanelHexModel model;

    @BeforeEach
    void setup() {
        model = new HttpPanelHexModel();
    }

    @Test
    void testGetData() {
        // setting value 'a0' in Hex view resulted in 'c2 a0'
        model.setData(new byte[] {(byte) 0xa0});

        assertThat(new byte[] {(byte) 0xa0}, is(equalTo(model.getData())));
    }
}
