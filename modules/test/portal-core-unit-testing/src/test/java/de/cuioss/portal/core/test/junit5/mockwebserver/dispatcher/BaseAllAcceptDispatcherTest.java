/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher;

import static de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.EndpointAnswerHandlerTest.assertMockResponse;
import static de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.HttpMethodMapper.DELETE;
import static de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.HttpMethodMapper.GET;
import static de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.HttpMethodMapper.POST;
import static de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.HttpMethodMapper.PUT;
import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED;
import static jakarta.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

import org.junit.jupiter.api.Test;

import mockwebserver3.RecordedRequest;

class BaseAllAcceptDispatcherTest {

    private static final String DEFAULT_PATH = "/hello";
    static final RecordedRequest DUMMY = CombinedDispatcherTest.createRequestFor(HttpMethodMapper.GET, null, "/");

    @Test
    void shouldDefaultToPositiveResponse() {
        var dispatcher = new BaseAllAcceptDispatcher(DEFAULT_PATH);
        assertMockResponse(dispatcher.handleDelete(DUMMY).get(), SC_NO_CONTENT);
        assertMockResponse(dispatcher.handleGet(DUMMY).get(), SC_OK);
        assertMockResponse(dispatcher.handlePut(DUMMY).get(), SC_CREATED);
        assertMockResponse(dispatcher.handlePost(DUMMY).get(), SC_OK);
    }

    @Test
    void shouldModifyMethodsWithNull() {
        var dispatcher = new BaseAllAcceptDispatcher(DEFAULT_PATH);

        dispatcher.setMethodToResult(null);
        assertMockResponse(dispatcher.handleDelete(DUMMY).get(), SC_NO_CONTENT);
        assertMockResponse(dispatcher.handleGet(DUMMY).get(), SC_OK);
        assertMockResponse(dispatcher.handlePut(DUMMY).get(), SC_CREATED);
        assertMockResponse(dispatcher.handlePost(DUMMY).get(), SC_OK);
    }

    @Test
    void shouldModifyMethodsToForbiddden() {
        var dispatcher = new BaseAllAcceptDispatcher(DEFAULT_PATH);

        dispatcher.setMethodToResult(EndpointAnswerHandler.RESPONSE_FORBIDDEN, DELETE, GET, POST, PUT);
        assertMockResponse(dispatcher.handleDelete(DUMMY).get(), SC_FORBIDDEN);
        assertMockResponse(dispatcher.handleGet(DUMMY).get(), SC_FORBIDDEN);
        assertMockResponse(dispatcher.handlePut(DUMMY).get(), SC_FORBIDDEN);
        assertMockResponse(dispatcher.handlePost(DUMMY).get(), SC_FORBIDDEN);
    }

    @Test
    void shouldModifyOtherMethodsWithNull() {
        var dispatcher = new BaseAllAcceptDispatcher(DEFAULT_PATH);

        dispatcher.setAllButGivenMethodToResult(EndpointAnswerHandler.RESPONSE_NOT_IMPLEMENTED);
        assertMockResponse(dispatcher.handleDelete(DUMMY).get(), SC_NOT_IMPLEMENTED);
        assertMockResponse(dispatcher.handleGet(DUMMY).get(), SC_NOT_IMPLEMENTED);
        assertMockResponse(dispatcher.handlePut(DUMMY).get(), SC_NOT_IMPLEMENTED);
        assertMockResponse(dispatcher.handlePost(DUMMY).get(), SC_NOT_IMPLEMENTED);

        // Reset
        dispatcher.reset();
        assertMockResponse(dispatcher.handleDelete(DUMMY).get(), SC_NO_CONTENT);
        assertMockResponse(dispatcher.handleGet(DUMMY).get(), SC_OK);
        assertMockResponse(dispatcher.handlePut(DUMMY).get(), SC_CREATED);
        assertMockResponse(dispatcher.handlePost(DUMMY).get(), SC_OK);
    }

    @Test
    void shouldModifyOtherMethodsToForbiddden() {
        var dispatcher = new BaseAllAcceptDispatcher(DEFAULT_PATH);

        dispatcher.setAllButGivenMethodToResult(EndpointAnswerHandler.RESPONSE_FORBIDDEN, DELETE, GET, POST, PUT);
        assertMockResponse(dispatcher.handleDelete(DUMMY).get(), SC_NO_CONTENT);
        assertMockResponse(dispatcher.handleGet(DUMMY).get(), SC_OK);
        assertMockResponse(dispatcher.handlePut(DUMMY).get(), SC_CREATED);
        assertMockResponse(dispatcher.handlePost(DUMMY).get(), SC_OK);
    }

}
