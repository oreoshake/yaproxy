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
package org.yaproxy.yap.spider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link org.yaproxy.yap.spider.URLResolver}.
 *
 * @author bjoern.kimminich@gmx.de
 */
@SuppressWarnings("deprecation")
class URLResolverUnitTest {

    @Test
    void shouldThrowExceptionOnMissingBaseUrl() {
        // Given
        String baseUrl = null;
        // When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> org.yaproxy.yap.spider.URLResolver.resolveUrl(baseUrl, "notNull"));
    }

    @Test
    void shouldThrowExceptionOnMissingRelativeUrl() {
        // Given
        String relativeUrl = null;
        // When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> org.yaproxy.yap.spider.URLResolver.resolveUrl("notNull", relativeUrl));
    }

    @Test
    void shouldAppendRelativeUrlToBaseUrlHost() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl("http://www.abc.de", "/xy/z"),
                is("http://www.abc.de/xy/z"));
    }

    @Test
    void shouldInsertSlashBetweenBaseUrlAndRelativeUrlIfMissing() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl("http://www.abc.de", "xyz"),
                is("http://www.abc.de/xyz"));
    }

    @Test
    void shouldReplaceLastPartOfUrlPathFromBaseUrlWithRelativeUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl("http://www.abc.de/w/x", "y/z"),
                is("http://www.abc.de/w/y/z"));
    }

    @Test
    void shouldRemoveFragmentFromBaseUrlBeforeAppendingRelativeUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl("http://www.abc.de#anchor", "y"),
                is("http://www.abc.de/y"));
    }

    @Test
    void shouldRemoveQueryFromBaseUrlBeforeAppendingRelativeUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl("http://www.abc.de?y=z", "test"),
                is("http://www.abc.de/test"));
    }

    @Test
    void shouldRemoveParametersFromBaseUrlBeforeAppendingRelativeUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl("http://www.abc.de;y;z", "test"),
                is("http://www.abc.de/test"));
    }

    @Test
    void shouldReturnOriginalBaseUrlForGivenEmptyRelativeUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl(
                        "http://www.abc.de/x?y=z&u=v#123", ""),
                is("http://www.abc.de/x?y=z&u=v#123"));
    }

    @Test
    void shouldReturnOriginalRelativeUrlForGivenAbsoluteUrlAsRelativeUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl(
                        "http://base.url", "http://www.abc.de/x?y=z&u=v#123"),
                is("http://www.abc.de/x?y=z&u=v#123"));
    }

    @Test
    void shouldUseSchemeOfBaseUrlForGivenUrlWithHostAsRelativeUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl("https://base.url", "//www.test.com"),
                is("https://www.test.com"));
    }

    @Test
    void shouldAppendQueryGivenAsRelativeUrlToBaseUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl("http://abc.de/123", "?x=y"),
                is("http://abc.de/123?x=y"));
    }

    @Test
    void shouldAppendParametersGivenAsRelativeUrlToBaseUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl("http://abc.de/123", ";x=y"),
                is("http://abc.de/123;x=y"));
    }

    @Test
    void shouldAppendFragmentGivenAsRelativeUrlToBaseUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl("http://abc.de/123", "#test"),
                is("http://abc.de/123#test"));
    }

    @Test
    void shouldRemoveLeadingSlashPointsFromRelativeUrlBeforeAppendingToBaseUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl("http://abc.de/123/xyz", "../test"),
                is("http://abc.de/test"));
    }

    @Test
    void shouldRemoveAllSlashPointSlashOccurrencesFromResolvedUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl(
                        "http://abc.de/./", "test/./xyz/./123"),
                is("http://abc.de/test/xyz/123"));
    }

    @Test
    void shouldRemoveTrailingPointFromResolvedUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl("http://abc.de", "test/."),
                is("http://abc.de/test/"));
    }

    @Test
    void shouldApplyDirectoryTraversalWithSlashPointsInResolvedUrl() {
        assertThat(
                org.yaproxy.yap.spider.URLResolver.resolveUrl(
                        "http://abc.de/x/../", "y/../z/../test/123/.."),
                is("http://abc.de/test/"));
    }
}
