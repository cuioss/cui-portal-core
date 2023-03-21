package de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY;
import static javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import okhttp3.mockwebserver.MockResponse;

class EndpointAnswerHandlerTest {

    @Test
    void shouldHandleDefaults() {
        assertMockResponse(EndpointAnswerHandler.forPositiveGetRequest().getResponse(), SC_OK);
        assertMockResponse(EndpointAnswerHandler.forPositiveDeleteRequest().getResponse(), SC_NO_CONTENT);
        assertMockResponse(EndpointAnswerHandler.forPositivePutRequest().getResponse(), SC_CREATED);
        assertMockResponse(EndpointAnswerHandler.forPositivePostRequest().getResponse(), SC_OK);
    }

    @Test
    void shouldChangeExistingResponse() {
        var handler = EndpointAnswerHandler.forPositiveGetRequest();
        assertMockResponse(handler.respond().get(), SC_OK);
        handler.respondCreated();
        assertMockResponse(handler.respond().get(), SC_CREATED);
        handler.respondForbidden();
        assertMockResponse(handler.respond().get(), SC_FORBIDDEN);
        handler.respondNoContent();
        assertMockResponse(handler.respond().get(), SC_NO_CONTENT);
        handler.respondNotFound();
        assertMockResponse(handler.respond().get(), SC_NOT_FOUND);
        handler.respondNotImplemented();
        assertMockResponse(handler.respond().get(), SC_NOT_IMPLEMENTED);
        handler.respondOk();
        assertMockResponse(handler.respond().get(), SC_OK);
        handler.respondUnauthorized();
        assertMockResponse(handler.respond().get(), SC_UNAUTHORIZED);
        handler.respondMovedPermanently();
        assertMockResponse(handler.respond().get(), SC_MOVED_PERMANENTLY);
        handler.respondMovedTemporarily();
        assertMockResponse(handler.respond().get(), SC_MOVED_TEMPORARILY);
        // Now check Reset
        handler.resetToDefaultResponse();
        assertMockResponse(handler.respond().get(), SC_OK);
    }

    @Test
    void shouldHandleNullResponse() {
        var handler = EndpointAnswerHandler.noContent(HttpMethodMapper.DELETE);
        assertTrue(handler.respond().isEmpty());
        handler.respondCreated();
        assertFalse(handler.respond().isEmpty());
        handler.resetToDefaultResponse();
        assertTrue(handler.respond().isEmpty());
    }

    static void assertMockResponse(MockResponse mockResponse, int httpCode) {
        assertNotNull(mockResponse);
        assertTrue(mockResponse.getStatus().contains(String.valueOf(httpCode)),
                mockResponse.getStatus() + " does not contain " + httpCode);
    }
}
