/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2013 The YAP Development Team
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit test for {@link YapReleaseComparitor}. */
class YapReleaseComparitorUnitTest {

    private static final String DEV_BUILD = "Dev Build";

    @Test
    void testComparitor() {
        YapReleaseComparitor zrc = new YapReleaseComparitor();

        // Test equals
        assertTrue(zrc.compare(new YapRelease(DEV_BUILD), new YapRelease(DEV_BUILD)) == 0);
        assertTrue(zrc.compare(new YapRelease("2.0.0"), new YapRelease("2.0.0")) == 0);
        assertTrue(zrc.compare(new YapRelease("2.0.alpha"), new YapRelease("2.0.alpha")) == 0);
        assertTrue(
                zrc.compare(new YapRelease("D-2013-01-01"), new YapRelease("D-2013-01-01")) == 0);

        // Test first more recent that second
        assertTrue(zrc.compare(new YapRelease(DEV_BUILD), new YapRelease("D-2012-08-01")) > 0);
        assertTrue(zrc.compare(new YapRelease(DEV_BUILD), new YapRelease("1.4.1")) > 0);
        assertTrue(zrc.compare(new YapRelease(DEV_BUILD), new YapRelease("2.4.beta")) > 0);
        assertTrue(zrc.compare(new YapRelease("2.0.0.1"), new YapRelease("2.0.0")) > 0);
        assertTrue(zrc.compare(new YapRelease("2.0.0.1"), new YapRelease("2.0.alpha")) > 0);
        assertTrue(zrc.compare(new YapRelease("1.4"), new YapRelease("1.3.4")) > 0);
        assertTrue(zrc.compare(new YapRelease("2.0"), new YapRelease("1.3.4")) > 0);
        assertTrue(zrc.compare(new YapRelease("2.0.11"), new YapRelease("2.0.5")) > 0);
        assertTrue(zrc.compare(new YapRelease("1.4.alpha"), new YapRelease("1.3.4")) > 0);
        assertTrue(zrc.compare(new YapRelease("D-2012-08-02"), new YapRelease("D-2012-08-01")) > 0);
        assertTrue(zrc.compare(new YapRelease("D-2013-10-10"), new YapRelease("D-2012-01-01")) > 0);
        assertTrue(zrc.compare(new YapRelease("D-2013-01-01"), new YapRelease("D-2012-12-31")) > 0);
        assertTrue(zrc.compare(new YapRelease("D-2013-01-07"), new YapRelease("D-2012-12-31")) > 0);
        assertTrue(zrc.compare(new YapRelease("D-2013-01-07"), new YapRelease("2.0.1")) > 0);

        // Test first older that second
        assertTrue(zrc.compare(new YapRelease("1.4.1"), new YapRelease(DEV_BUILD)) < 0);
        assertTrue(zrc.compare(new YapRelease("2.4.beta"), new YapRelease(DEV_BUILD)) < 0);
        assertTrue(zrc.compare(new YapRelease("2.0.0"), new YapRelease("2.0.0.1")) < 0);
        assertTrue(zrc.compare(new YapRelease("2.0.alpha"), new YapRelease("2.0.0.1")) < 0);
        assertTrue(zrc.compare(new YapRelease("1.3.4"), new YapRelease("1.4")) < 0);
        assertTrue(zrc.compare(new YapRelease("1.3.4"), new YapRelease("2.0")) < 0);
        assertTrue(zrc.compare(new YapRelease("2.0.6"), new YapRelease("2.0.12")) < 0);
        assertTrue(zrc.compare(new YapRelease("1.3.4"), new YapRelease("1.4.alpha")) < 0);
        assertTrue(zrc.compare(new YapRelease("D-2012-08-01"), new YapRelease("D-2012-08-02")) < 0);
        assertTrue(zrc.compare(new YapRelease("D-2012-01-01"), new YapRelease("D-2013-10-10")) < 0);
        assertTrue(zrc.compare(new YapRelease("D-2012-12-31"), new YapRelease("D-2013-01-01")) < 0);
        assertTrue(zrc.compare(new YapRelease("D-2012-12-31"), new YapRelease("D-2013-01-07")) < 0);
        assertTrue(zrc.compare(new YapRelease("2.0.1"), new YapRelease("D-2013-01-07")) < 0);

        // Bad versions
        assertThrows(
                IllegalArgumentException.class,
                () -> zrc.compare(new YapRelease("1.4.1.theta"), new YapRelease("1.4.1.alpha")));
        assertThrows(
                IllegalArgumentException.class,
                () -> zrc.compare(new YapRelease("1.4.1.0"), new YapRelease("1.4.1.theta")));
    }
}
