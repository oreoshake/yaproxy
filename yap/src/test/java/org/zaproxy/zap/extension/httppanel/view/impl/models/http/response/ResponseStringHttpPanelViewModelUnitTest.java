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
package org.yaproxy.yap.extension.httppanel.view.impl.models.http.response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpResponseHeader;
import org.yaproxy.yap.extension.httppanel.view.impl.models.http.StringHttpPanelViewModelTest;
import org.yaproxy.yap.network.HttpResponseBody;

/** Unit test for {@link ResponseStringHttpPanelViewModel}. */
class ResponseStringHttpPanelViewModelUnitTest
        extends StringHttpPanelViewModelTest<HttpResponseHeader, HttpResponseBody> {

    @Override
    protected ResponseStringHttpPanelViewModel createModel() {
        return new ResponseStringHttpPanelViewModel();
    }

    @Override
    protected Class<HttpResponseHeader> getHeaderClass() {
        return HttpResponseHeader.class;
    }

    @Override
    protected void prepareHeader() {
        super.prepareHeader();
        given(header.isEmpty()).willReturn(false);
    }

    @Override
    protected void verifyHeader(String header) throws HttpMalformedHeaderException {
        verify(message).setResponseHeader(header);
    }

    @Override
    protected void headerThrowsHttpMalformedHeaderException() throws HttpMalformedHeaderException {
        willThrow(HttpMalformedHeaderException.class).given(message).setResponseHeader(anyString());
    }

    @Override
    protected Class<HttpResponseBody> getBodyClass() {
        return HttpResponseBody.class;
    }

    @Override
    protected void prepareMessage() {
        given(message.getResponseHeader()).willReturn(header);
        given(message.getResponseBody()).willReturn(body);
    }

    @Test
    void shouldGetEmptyDataWithEmptyHeader() {
        // Given
        given(header.isEmpty()).willReturn(true);
        model.setMessage(message);
        // When
        String data = model.getData();
        // Then
        assertThat(data, is(emptyString()));
    }

    @Override
    protected void verifyBodySet(HttpMessage message, String body) {
        verify(message).setResponseBody(body);
    }

    @Override
    protected void verifyBodyNotSet(HttpMessage message) {
        verify(message, times(0)).setResponseBody(anyString());
    }
}
