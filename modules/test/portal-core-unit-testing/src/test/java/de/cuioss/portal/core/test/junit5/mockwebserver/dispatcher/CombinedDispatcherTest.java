package de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher;

import static de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.CombinedDispatcher.HTTP_CODE_NOT_FOUND;
import static de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.CombinedDispatcher.HTTP_CODE_TEAPOT;
import static de.cuioss.tools.collect.CollectionLiterals.mutableList;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.Socket;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import okhttp3.Headers;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;

class CombinedDispatcherTest {

    private static AllOkDispatcher okDispatcher = new AllOkDispatcher();

    private static PassThroughDispatcher passDispatcher = new PassThroughDispatcher();

    @Test
    void shouldHandleConstructor() {
        var dispatcher = new CombinedDispatcher(passDispatcher);
        assertDispatchWithCode(dispatcher, HTTP_CODE_TEAPOT, passDispatcher.getBaseUrl());

        dispatcher = new CombinedDispatcher(passDispatcher).endWithTeapot(false);
        assertDispatchWithCode(dispatcher, HTTP_CODE_NOT_FOUND, passDispatcher.getBaseUrl());

        dispatcher = new CombinedDispatcher(okDispatcher, okDispatcher);
        assertDispatchWithCode(dispatcher, SC_OK, okDispatcher.getBaseUrl());
    }

    @Test
    void shouldHandleBuilderVariants() {
        var dispatcher = new CombinedDispatcher();
        assertDispatchWithCode(dispatcher, HTTP_CODE_TEAPOT, passDispatcher.getBaseUrl());

        dispatcher = new CombinedDispatcher().addDispatcher(okDispatcher);
        assertDispatchWithCode(dispatcher, SC_OK, okDispatcher.getBaseUrl());

        dispatcher = new CombinedDispatcher().addDispatcher(okDispatcher, okDispatcher);
        assertDispatchWithCode(dispatcher, SC_OK, okDispatcher.getBaseUrl());

        dispatcher = new CombinedDispatcher().addDispatcher(mutableList(okDispatcher, okDispatcher));
        assertDispatchWithCode(dispatcher, SC_OK, okDispatcher.getBaseUrl());

    }

    @Test
    void shouldHandleMissingFilter() {
        var dispatcher = new CombinedDispatcher().addDispatcher(okDispatcher);
        assertDispatchWithCode(dispatcher, HTTP_CODE_TEAPOT, "/notThere");
    }

    private void assertDispatchWithCode(CombinedDispatcher dispatcher, int httpCode, String urlPart) {
        for (HttpMethodMapper mapper : HttpMethodMapper.values()) {
            var request = createRequestFor(mapper, dispatcher, urlPart);
            try {
                var result = dispatcher.dispatch(request);
                assertTrue(result.getStatus().contains(String.valueOf(httpCode)),
                        "Status was '" + result.getStatus() + "', expected was: " + httpCode);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        }

    }

    static RecordedRequest createRequestFor(HttpMethodMapper mapper, CombinedDispatcher dispatcher, String urlPart) {
        return new RecordedRequest(mapper.name() + " " + urlPart + "someResource  HTTP/1.1",
                Headers.of("key=value", "key2=value2"), Collections.emptyList(), 0,
                new Buffer(), 0, new Socket());
    }

}
