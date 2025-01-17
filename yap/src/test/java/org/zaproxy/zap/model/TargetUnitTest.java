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
package org.yaproxy.yap.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import org.parosproxy.paros.model.SiteNode;

/** Unit test for {@link Target}. */
class TargetUnitTest {

    @Test
    void shouldNotSetNullSiteNodeWhenConstructing() {
        // Given
        SiteNode startNode = null;
        // When
        Target target = new Target(startNode, null, true, true);
        // Then
        assertThat(target.getStartNode(), is(nullValue()));
        assertThat(target.getStartNodes(), is(nullValue()));
    }

    @Test
    void shouldNotSetNullStartSiteNode() {
        // Given
        SiteNode siteNode = null;
        Target target = new Target();
        // When
        target.setStartNode(siteNode);
        // Then
        assertThat(target.getStartNode(), is(nullValue()));
        assertThat(target.getStartNodes(), is(nullValue()));
    }
}
