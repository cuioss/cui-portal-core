package de.cuioss.portal.restclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Closeable;
import java.util.List;
import java.util.logging.LogRecord;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.test.generator.Generators;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.TestLoggerFactory;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.logging.CuiLogger;
import lombok.Setter;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@EnableAutoWeld
@EnableMockWebServer
// FIXME only works with root level TRACE since Java 11 compile level
@EnableTestLogger(rootLevel = TestLogLevel.TRACE, trace = { RestClientLoggingTest.class, LogClientRequestFilter.class,
        LogClientRequestFilter42.class, LogReaderInterceptor.class, LogClientResponseFilter.class })
@AddExtensions(ResteasyCdiExtension.class)
@Disabled("org.jboss.weld.exceptions.UnsatisfiedResolutionException: WELD-001334: Unsatisfied dependencies for type Tracing with qualifiers")
class RestClientLoggingTest implements MockWebServerHolder {

    private static final CuiLogger LOGGER = new CuiLogger(RestClientLoggingTest.class);

    private static final String TEXT = Generators.nonEmptyStrings().next();

    @Setter
    private MockWebServer mockWebServer;

    public interface TestService extends Closeable {

        @GET
        @Path("something")
        String getSomething();

        @POST
        @Path("something")
        void postSomething();
    }

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {

            @Override
            public MockResponse dispatch(final RecordedRequest request) {
                switch (request.getPath()) {
                case "/something":
                    if (HttpMethod.GET.equals(request.getMethod())) {
                        return new MockResponse().setResponseCode(HttpServletResponse.SC_OK)
                                .addHeader("x-header-key", "x-header-value").setBody(TEXT);
                    }
                    if (HttpMethod.POST.equals(request.getMethod())) {
                        return new MockResponse().setResponseCode(HttpServletResponse.SC_CREATED)
                                .addHeader("x-header-key", "x-header-value");
                    }
                default:
                    return new MockResponse().setResponseCode(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        };
    }

    @Test
    void shouldLogGetRequestsAndResponses() {
        final var underTest = new CuiRestClientBuilder(LOGGER).url(mockWebServer.url("").toString())
                .enableDefaultExceptionHandler().register(new LogClientRequestFilter42());
        final var service = underTest.build(TestService.class);
        assertEquals(TEXT, service.getSomething());

        var allMsgs = TestLoggerFactory.getTestHandler().resolveLogMessages(TestLogLevel.INFO);

        final var clientRequestInfoLog = allMsgs.stream().map(LogRecord::getMessage)
                .filter(msg -> msg.startsWith("-- Client request info --")).findFirst()
                .orElseThrow(() -> new AssertionError("client request info not logged"));
        assertTrue(clientRequestInfoLog.contains("Request URI: " + mockWebServer.url("something")));
        assertTrue(clientRequestInfoLog.contains("Method: GET"));
        assertTrue(clientRequestInfoLog.contains("Headers:"));
        assertTrue(clientRequestInfoLog.contains("Accept: [application/json]"));

        final var clientResponseInfoLog = allMsgs.stream().map(LogRecord::getMessage)
                .filter(msg -> msg.startsWith("-- Client response info --")).findFirst()
                .orElseThrow(() -> new AssertionError("client response info not logged"));
        assertTrue(clientResponseInfoLog.contains("MediaType: application/octet-stream"));
        assertTrue(clientResponseInfoLog.contains("GenericType: class java.lang.String"));
        assertTrue(clientResponseInfoLog.contains("Properties:"));
        assertTrue(clientResponseInfoLog
                .contains("org.eclipse.microprofile.rest.client.invokedMethod: public abstract java.lang.String "));
        assertTrue(clientResponseInfoLog.contains("Headers:"));
        assertTrue(clientResponseInfoLog.contains("Content-Length: [" + TEXT.length() + "]"));
        assertTrue(clientResponseInfoLog.contains("Content-Type: [application/octet-stream]"));
        assertTrue(clientResponseInfoLog.contains("x-header-key: [x-header-value]"));
        assertTrue(clientResponseInfoLog.contains("Body:"));
        assertTrue(clientResponseInfoLog.contains(TEXT));

        assertTrue(allMsgs.stream().anyMatch(msg -> msg.getMessage().contains("[First ClientResponseFilter]")));
        assertTrue(allMsgs.stream().anyMatch(msg -> msg.getMessage().contains("[Last ClientResponseFilter]")));

        assertClientResponseFIlter(allMsgs);

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.TRACE, "-- LogClientRequestFilter42 --");
        assertLogMessageBeforeOther(LogClientRequestFilter42.class, LogClientRequestFilter.class,
                "LogClientRequestFilter42 runs before LogClientRequestFilter because it has a lower prio");
    }

    private void assertClientResponseFIlter(List<LogRecord> allMsgs) throws AssertionError {
        final var lastClientResponseFilter = allMsgs.stream().map(LogRecord::getMessage)
                .filter(msg -> msg.contains("[Last ClientResponseFilter]")).findFirst()
                .orElseThrow(() -> new AssertionError("client response info not logged"));
        assertTrue(lastClientResponseFilter.contains("Status: 200"));
        assertTrue(lastClientResponseFilter.contains("StatusInfo: OK"));
        assertTrue(lastClientResponseFilter.contains("Allowed Methods: []"));
        assertTrue(lastClientResponseFilter.contains("EntityTag: null"));
        assertTrue(lastClientResponseFilter.contains("Cookies: {}"));
        assertTrue(lastClientResponseFilter.contains("Date: null"));
        assertTrue(lastClientResponseFilter
                .contains("Headers: [Content-Length=" + TEXT.length() + ",x-header-key=x-header-value]"));
        assertTrue(lastClientResponseFilter.contains("Language: null"));
        assertTrue(lastClientResponseFilter.contains("LastModified: null"));
        assertTrue(lastClientResponseFilter.contains("Links: []"));
        assertTrue(lastClientResponseFilter.contains("Location: null"));
        assertTrue(lastClientResponseFilter.contains("MediaType: null"));
    }

    @Test
    void shouldLogPostRequestsAndResponses() {
        final var underTest = new CuiRestClientBuilder(LOGGER).url(mockWebServer.url("").toString());
        underTest.register(new LogClientRequestFilter42());
        final var service = underTest.build(TestService.class);
        service.postSomething();

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "-- Client request info --");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO,
                "Request URI: " + mockWebServer.url("something"));
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
                "Headers: [Content-Length=0,x-header-key=x-header-value]");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Language: null");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "LastModified: null");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Links: []");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Location: null");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "MediaType: null");

        LogAsserts.assertLogMessagePresent(TestLogLevel.TRACE, "-- LogClientRequestFilter42 --");
        assertLogMessageBeforeOther(LogClientRequestFilter42.class, LogClientRequestFilter.class,
                "LogClientRequestFilter42 runs before LogClientRequestFilter because it has a lower prio");
    }

    void assertLogMessageBeforeOther(final Class firstClazz, final Class secondClazz, final String errorMsg) {
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
