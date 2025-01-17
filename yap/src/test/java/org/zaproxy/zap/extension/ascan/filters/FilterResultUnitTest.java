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
package org.yaproxy.yap.extension.ascan.filters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** Unit test for {@link FilterResult}. */
class FilterResultUnitTest {

    @Test
    void shouldHaveNotFilteredInstance() {
        assertThat(FilterResult.NOT_FILTERED.isFiltered(), is(equalTo(false)));
        assertThat(FilterResult.NOT_FILTERED.getReason(), is(nullValue()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "Reason")
    void shouldCreateFilterResultWithReason(String reason) {
        // Given / When
        FilterResult filterResult = new FilterResult(reason);
        // Then
        assertThat(filterResult.isFiltered(), is(equalTo(true)));
        assertThat(filterResult.getReason(), is(equalTo(reason)));
    }

    @Test
    void shouldContainFilteredValueAndNullReasonInToStringForNotFilteredResult() {
        // Given / When
        String string = FilterResult.NOT_FILTERED.toString();
        // Then
        assertThat(
                string, both(containsString("filtered=false")).and(containsString("reason=null")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "Reason")
    void shouldContainFilteredValueAndReasonInToStringForFilteredResult(String reason) {
        // Given
        FilterResult filterResult = new FilterResult(reason);
        // When
        String string = filterResult.toString();
        // Then
        assertThat(
                string,
                both(containsString("filtered=true")).and(containsString("reason=" + reason)));
    }
}
