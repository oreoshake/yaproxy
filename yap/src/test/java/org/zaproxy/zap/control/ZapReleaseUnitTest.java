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
package org.yaproxy.yap.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit test for {@link YapRelease}. */
class YapReleaseUnitTest {

    private static final String DEV_BUILD = "Dev Build";

    @Test
    void testDevBuildLaterThan1_4_1() {
        YapRelease rel = new YapRelease();
        rel.setVersion(DEV_BUILD);
        assertTrue(rel.isNewerThan("1.4.1"));
    }

    @Test
    void test1_4_2LaterThan1_4_1() {
        YapRelease rel = new YapRelease();
        rel.setVersion("1.4.2");
        assertTrue(rel.isNewerThan("1.4.1"));
    }

    @Test
    void test1_5_1LaterThan1_4_2() {
        YapRelease rel = new YapRelease();
        rel.setVersion("1.5.1");
        assertTrue(rel.isNewerThan("1.4.2"));
    }

    @Test
    void test1_5_1LaterThan1_4_2_1() {
        YapRelease rel = new YapRelease();
        rel.setVersion("1.5.1");
        assertTrue(rel.isNewerThan("1.4.2.1"));
    }

    @Test
    void testLots() {
        // Imported from old CheckForUpdates code
        assertFalse(new YapRelease("1.3.4").isNewerThan("1.4"));
        assertFalse(new YapRelease("1.3.4").isNewerThan("1.4"));
        assertFalse(new YapRelease("1.3.4").isNewerThan("2.0"));
        assertFalse(new YapRelease("1.4").isNewerThan("1.4.1"));
        assertFalse(new YapRelease("1.4.1").isNewerThan("1.4.2"));
        assertFalse(new YapRelease("1.4.2").isNewerThan("1.4.11"));
        // Dont support this right now
        // assertFalse(new YapRelease("1.4.alpha.1").isNewerThan("1.4"));
        // assertFalse(new YapRelease("1.4.alpha.1").isNewerThan("1.4.1"));
        assertFalse(new YapRelease("1.4.beta.1").isNewerThan("1.5"));
        assertFalse(new YapRelease("D-2012-08-01").isNewerThan("D-2012-08-02"));
        assertFalse(new YapRelease("D-2012-01-01").isNewerThan("D-2013-10-10"));
        assertFalse(new YapRelease("1.4").isNewerThan("1.4"));

        assertTrue(new YapRelease("1.4").isNewerThan("1.3.4"));
        assertTrue(new YapRelease("1.4.2").isNewerThan("1.4.1"));
        assertTrue(new YapRelease("1.4.20").isNewerThan("1.4.11"));
        assertTrue(new YapRelease("1.4.alpha.1").isNewerThan("1.3.4"));
        // Dont support this right now
        // assertTrue(new YapRelease("1.4").isNewerThan("1.4.alpha.1"));
        assertTrue(new YapRelease("Dev Build").isNewerThan("1.5"));
        assertTrue(new YapRelease("D-2012-08-02").isNewerThan("D-2012-08-01"));
        assertTrue(new YapRelease("D-2013-10-10").isNewerThan("D-2012-01-01"));
        assertTrue(new YapRelease("D-2013-01-01").isNewerThan("D-2012-12-31"));
        assertTrue(new YapRelease("D-2013-01-07").isNewerThan("D-2012-12-31"));
    }
}
