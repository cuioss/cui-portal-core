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
package de.cuioss.portal.restclient;

import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.TestLoggerFactory;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.test.mockwebserver.EnableMockWebServer;
import de.cuioss.test.mockwebserver.URIBuilder;
import de.cuioss.test.mockwebserver.dispatcher.HttpMethodMapper;
import de.cuioss.test.mockwebserver.mockresponse.MockResponseConfig;
import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.util.List;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableAutoWeld
@EnableMockWebServer
@EnableTestLogger(info = {RestClientLoggingTest.class, LogClientRequestFilter.class,
        LogReaderInterceptor.class, LogClientResponseFilter.class}, trace = LogClientRequestFilter42.class)
@AddExtensions(ResteasyCdiExtension.class)
@ExplicitParamInjection
@MockResponseConfig(
        path = "/something",
        status = HttpServletResponse.SC_OK,
        method = HttpMethodMapper.GET,
        headers = "x-header-key=x-header-value",
        textContent = RestClientLoggingTest.TEXT
)
class RestClientLoggingTest {

    private static final CuiLogger LOGGER = new CuiLogger(RestClientLoggingTest.class);

    static final String TEXT = "Some text";

    public interface TestService extends Closeable {

        @GET
        @Path("something")
        String getSomething();

        @POST
        @Path("something-post")
        void postSomething();
    }

    @Test
    void shouldLogGetRequestsAndResponses(URIBuilder uriBuilder) {
        final var underTest = new CuiRestClientBuilder(LOGGER).url(uriBuilder.build().toString()).traceLogEnabled(true)
                .enableDefaultExceptionHandler().register(new LogClientRequestFilter42());
        final var service = underTest.build(TestService.class);

        assertEquals(TEXT, service.getSomething());

        var allMessages = TestLoggerFactory.getTestHandler().resolveLogMessages(TestLogLevel.INFO);

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "First ClientResponseFilter]");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "[Last ClientResponseFilter]");

        assertTrue(allMessages.stream().anyMatch(msg -> msg.getMessage().contains("[First ClientResponseFilter]")));
        assertTrue(allMessages.stream().anyMatch(msg -> msg.getMessage().contains("[Last ClientResponseFilter]")));

        assertClientResponseFilter(allMessages);

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.TRACE, "-- LogClientRequestFilter42 --");
        assertLogMessageBeforeOther(LogClientRequestFilter42.class, LogClientRequestFilter.class,
                "LogClientRequestFilter42 runs before LogClientRequestFilter because it has a lower prio");
    }

    @Test
    void shouldLogClientRequestInfo(URIBuilder uriBuilder) {
        final var underTest = new CuiRestClientBuilder(LOGGER).url(uriBuilder.build().toString()).traceLogEnabled(true)
                .enableDefaultExceptionHandler().register(new LogClientRequestFilter42());
        final var service = underTest.build(TestService.class);
        assertEquals(TEXT, service.getSomething());

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Client request info");
        var allMessages = TestLoggerFactory.getTestHandler().resolveLogMessages(TestLogLevel.INFO);

        final var clientRequestInfoLog = allMessages.stream().map(LogRecord::getMessage)
                .filter(msg -> msg.contains("-- Client request info --")).findFirst()
                .orElseThrow(() -> new AssertionError("client request info not logged"));

        assertTrue(clientRequestInfoLog.contains("Request URI: " + uriBuilder.addPathSegment("something").build()));
        assertTrue(clientRequestInfoLog.contains("Method: GET"));
        assertTrue(clientRequestInfoLog.contains("Headers:"));
        assertTrue(clientRequestInfoLog.contains("Accept: [application/json]"));

        final var clientResponseInfoLog = allMessages.stream().map(LogRecord::getMessage)
                .filter(msg -> msg.contains("-- Client response info --")).findFirst()
                .orElseThrow(() -> new AssertionError("client response info not logged"));
        assertTrue(clientResponseInfoLog.contains("MediaType: text/plain"));
        assertTrue(clientResponseInfoLog.contains("GenericType: class java.lang.String"));
        assertTrue(clientResponseInfoLog.contains("Properties:"));
        assertTrue(clientResponseInfoLog
                .contains("org.eclipse.microprofile.rest.client.invokedMethod: public abstract java.lang.String "));
        assertTrue(clientResponseInfoLog.contains("Headers:"));
        assertTrue(clientResponseInfoLog.contains("Content-Length: [" + TEXT.length() + "]"));
        assertTrue(clientResponseInfoLog.contains("Content-Type: [text/plain]"));
        assertTrue(clientResponseInfoLog.contains("x-header-key: [x-header-value]"));
        assertTrue(clientResponseInfoLog.contains("Body:"));
        assertTrue(clientResponseInfoLog.contains(TEXT));
    }

    private void assertClientResponseFilter(List<LogRecord> allMessages) throws AssertionError {
        final var lastClientResponseFilter = allMessages.stream().map(LogRecord::getMessage)
                .filter(msg -> msg.contains("[Last ClientResponseFilter]")).findFirst()
                .orElseThrow(() -> new AssertionError("client response info not logged"));
        assertTrue(lastClientResponseFilter.contains("Status: 200"));
        assertTrue(lastClientResponseFilter.contains("StatusInfo: OK"));
        assertTrue(lastClientResponseFilter.contains("Allowed Methods: []"));
        assertTrue(lastClientResponseFilter.contains("EntityTag: null"));
        assertTrue(lastClientResponseFilter.contains("Cookies: {}"));
        assertTrue(lastClientResponseFilter.contains("Date: null"));
        assertTrue(lastClientResponseFilter
                .contains("Headers: [Content-Length=" + TEXT.length() + ",Content-Type=text/plain,x-header-key=x-header-value]"));
        assertTrue(lastClientResponseFilter.contains("Language: null"));
        assertTrue(lastClientResponseFilter.contains("LastModified: null"));
        assertTrue(lastClientResponseFilter.contains("Links: []"));
        assertTrue(lastClientResponseFilter.contains("Location: null"));
        assertTrue(lastClientResponseFilter.contains("MediaType: text/plain"));
    }

    @Test
    @MockResponseConfig(
            path = "/something-post",
            status = HttpServletResponse.SC_CREATED,
            method = HttpMethodMapper.POST,
            headers = "x-header-key=x-header-value",
            textContent = TEXT
    )
    void shouldLogPostRequestsAndResponses(URIBuilder uriBuilder) {
        final var underTest = new CuiRestClientBuilder(LOGGER).url(uriBuilder.build().toString()).traceLogEnabled(true);
        underTest.register(new LogClientRequestFilter42());
        final var service = underTest.build(TestService.class);
        service.postSomething();

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "-- Client request info --");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO,
                "Request URI: " + uriBuilder.addPathSegment("something-post").build());
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Method: POST");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Headers:");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Accept: [application/json]");

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO,
                "-- Client response filter [First ClientResponseFilter] --");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO,
                "-- Client response filter [Last ClientResponseFilter] --");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Status: 201");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "StatusInfo: Created");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Allowed Methods: []");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "EntityTag: null");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Cookies: {}");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Date: null");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO,
                "Headers: [Content-Length=9,Content-Type=text/plain,x-header-key=x-header-value]");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Language: null");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "LastModified: null");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Links: []");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Location: null");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "MediaType: text/plain");

        LogAsserts.assertLogMessagePresent(TestLogLevel.TRACE, "-- LogClientRequestFilter42 --");
        assertLogMessageBeforeOther(LogClientRequestFilter42.class, LogClientRequestFilter.class,
                "LogClientRequestFilter42 runs before LogClientRequestFilter because it has a lower prio");
    }

    void assertLogMessageBeforeOther(final Class<?> firstClazz, final Class<?> secondClazz, final String errorMsg) {
        var posCurrentLog = 0;
        var posFirstClazzLog = -1;
        var posSecondClazzLog = -1;
        final var recordedLogs = CollectionBuilder.copyFrom(TestLoggerFactory.getTestHandler().getRecords())
                .toImmutableList();
        for (LogRecord logRecord : recordedLogs) {
            if (firstClazz.getName().equals(logRecord.getSourceClassName())) {
                posFirstClazzLog = posCurrentLog;
                LOGGER.info("posFirstClazzLog={}", posFirstClazzLog);
            }

            if (secondClazz.getName().equals(logRecord.getSourceClassName())) {
                posSecondClazzLog = posCurrentLog;
                LOGGER.info("posSecondClazzLog={}", posSecondClazzLog);
            }

            if (posFirstClazzLog >= 0 && posSecondClazzLog >= 0) {
                break;
            }

            posCurrentLog++;
        }
        assertTrue(posFirstClazzLog < posSecondClazzLog, errorMsg);
    }
}
