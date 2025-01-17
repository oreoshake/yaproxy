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

import java.util.Comparator;
import org.parosproxy.paros.Constant;

public class YapReleaseComparitor implements Comparator<YapRelease> {

    @Override
    public int compare(YapRelease zr1, YapRelease zr2) {

        // Null checks
        if (zr1 == null) {
            if (zr2 == null) {
                return 0;
            }
            return 1;
        } else if (zr2 == null) {
            return -1;
        }
        if (zr1.getVersion().equals(zr2.getVersion())) {
            // Exactly the same
            return 0;
        }
        // Special cases
        if (Constant.isDevBuild(zr1.getVersion())) {
            return 1;
        }
        if (Constant.isDevBuild(zr2.getVersion())) {
            return -1;
        }
        if (Constant.isDailyBuild(zr1.getVersion())) {
            if (Constant.isDailyBuild(zr2.getVersion())) {
                // Can just do string comparison
                return zr1.getVersion().compareTo(zr2.getVersion());
            } else {
                // Daily build always trump numbered releases
                return 1;
            }
        }
        if (Constant.isDailyBuild(zr2.getVersion())) {
            // Daily build always trump numbered releases
            return -1;
        }

        String[] zr1Array = zr1.getVersion().split("\\.");
        String[] zr2Array = zr2.getVersion().split("\\.");

        for (int i = 0; i < zr1Array.length; i++) {
            if (i >= zr2Array.length) {
                // Equal up to now, zr1 longer so more recent (e.g. 2.0.0.1 is more recent that
                // 2.0.0)
                return 1;
            }
            if (zr1Array[i].equals(zr2Array[i])) {
                // same elements, carry on to next one
                continue;
            }
            Integer zr1Int = null;
            Integer zr2Int = null;
            try {
                zr1Int = Integer.parseInt(zr1Array[i]);
            } catch (NumberFormatException e) {
                // Ignore
            }
            try {
                zr2Int = Integer.parseInt(zr2Array[i]);
            } catch (NumberFormatException e) {
                // Ignore
            }
            if (zr1Int != null) {
                if (zr2Int != null) {
                    // both different integer elements
                    return zr1Int - zr2Int;
                } else {
                    if (zr2Array[i].equals(Constant.ALPHA_VERSION)
                            || zr2Array[i].equals(Constant.BETA_VERSION)) {
                        // zr2 element alpha or beta, so before any number (e.g. 2.0.0 is more
                        // recent
                        // than 2.0.alpha)
                        return 1;
                    } else {
                        throw new IllegalArgumentException("Invalid release number: " + zr2);
                    }
                }
            } else if (zr2Int != null) {
                // zr1 element not a number, zr2 is
                if (zr1Array[i].equals(Constant.ALPHA_VERSION)
                        || zr1Array[i].equals(Constant.BETA_VERSION)) {
                    // zr1 element alpha or beta, so before any number (e.g. 2.0.0 is more recent
                    // than
                    // 2.0.alpha)
                    return -1;
                } else {
                    throw new IllegalArgumentException(
                            "Invalid release number: " + zr1.getVersion());
                }
            } else {
                // neither numbers, must be different
                if (zr1Array[i].equals(Constant.ALPHA_VERSION)) {
                    return -1;
                } else if (zr1Array[i].equals(Constant.BETA_VERSION)) {
                    return +1;
                } else {
                    throw new IllegalArgumentException(
                            "Invalid release number: " + zr1.getVersion());
                }
            }
        }
        // All elements the same, but zr2 longer so more recent
        return -1;
    }
}
