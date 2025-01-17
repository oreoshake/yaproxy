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

import java.net.URL;

public class YapRelease {
    private String version;
    private URL url;
    private String fileName;
    private long size;
    private String releaseNotes;
    private URL releaseNotesUrl;
    private String hash;

    public YapRelease() {}

    public YapRelease(String version) {
        this.version = version;
    }

    public YapRelease(
            String version,
            URL url,
            String fileName,
            long size,
            String releaseNotes,
            URL releaseNotesUrl,
            String hash) {
        super();
        this.version = version;
        this.url = url;
        this.fileName = fileName;
        this.size = size;
        this.releaseNotes = releaseNotes;
        this.releaseNotesUrl = releaseNotesUrl;
        this.hash = hash;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL getReleaseNotesUrl() {
        return releaseNotesUrl;
    }

    public void setReleaseNotesUrl(URL releaseNotesUrl) {
        this.releaseNotesUrl = releaseNotesUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public boolean isNewerThan(String otherVersion) {
        YapReleaseComparitor zrc = new YapReleaseComparitor();
        return zrc.compare(this, new YapRelease(otherVersion)) > 0;
    }

    public boolean isOlderThan(String otherVersion) {
        YapReleaseComparitor zrc = new YapReleaseComparitor();
        return zrc.compare(this, new YapRelease(otherVersion)) < 0;
    }

    public String getHash() {
        return hash;
    }
}
