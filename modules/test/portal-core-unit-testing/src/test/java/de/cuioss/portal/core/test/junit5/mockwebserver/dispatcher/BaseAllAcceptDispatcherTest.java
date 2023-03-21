package de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher;

import static de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.EndpointAnswerHandlerTest.assertMockResponse;
import static de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.HttpMethodMapper.DELETE;
import static de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.HttpMethodMapper.GET;
import static de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.HttpMethodMapper.POST;
import static de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.HttpMethodMapper.PUT;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import org.junit.jupiter.api.Test;

import okhttp3.mockwebserver.RecordedRequest;

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
