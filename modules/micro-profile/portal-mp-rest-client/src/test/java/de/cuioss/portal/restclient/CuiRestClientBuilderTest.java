package de.cuioss.portal.restclient;

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_ENABLED;
import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_REPORTER_URL;
import static de.cuioss.test.generator.Generators.enumValues;
import static de.cuioss.test.generator.Generators.letterStrings;
import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static de.cuioss.tools.string.MoreStrings.nullToEmpty;
import static okhttp3.tls.internal.TlsUtil.localhost;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.ThreadContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import brave.Tracer;
import brave.Tracing;
import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.connections.impl.AuthenticationType;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.portal.configuration.connections.impl.StaticTokenResolver;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.portal.tracing.PortalTracing;
import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.generator.impl.URLGenerator;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.Joiner;
import de.cuioss.uimodel.application.LoginCredentials;
import lombok.Setter;
import okhttp3.Headers;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.tls.HandshakeCertificates;
import okhttp3.tls.HeldCertificate;

@EnableAutoWeld
@EnablePortalConfiguration(configuration = PORTAL_TRACING_ENABLED + ":true")
@EnableMockWebServer(manualStart = true)
@EnableTestLogger(trace = { CuiRestClientBuilder.class, CuiRestClientBuilderTest.class })
@AddBeanClasses({ PortalTracing.class })
@AddExtensions(ResteasyCdiExtension.class)
class CuiRestClientBuilderTest implements MockWebServerHolder {

    private static final CuiLogger LOG = new CuiLogger(CuiRestClientBuilderTest.class);

    private static final String TEXT = "täscht";
    private static final String MEDIA_TYPE_FHIR_XML = "application/fhir+xml";
    private static final String STRING = Generators.nonEmptyStrings().next();
    private final LinkedBlockingQueue<MutableSpan> spans = new LinkedBlockingQueue<>();
    private boolean spanEqualsMdc = true;
    private final List<Map<String, String>> mdcMap = new ArrayList<>();
    private TestResource service;

    public interface TestResource extends Closeable {

        @GET
        @Path("test")
        @javax.ws.rs.Produces(MEDIA_TYPE_FHIR_XML)
        String test();

        @POST
        @Path("post")
        @Consumes(MediaType.TEXT_PLAIN)
        void postString(final String body);

        @GET
        @Path("collection")
        @javax.ws.rs.Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        List<String> collection();

        @GET
        @Path("test")
        Response getResponse();
    }

    @Inject
    @PortalConfigurationSource
    private PortalTestConfiguration configuration;

    @Inject
    @ConfigProperty(name = PORTAL_TRACING_REPORTER_URL)
    private Provider<Optional<String>> tracingReporterUrl;

    @Inject
    private Provider<Tracer> tracerProvider;

    @Setter
    private MockWebServer mockWebServer;

    private final boolean doNotModifiedTest = false;

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {

            @Override
            public MockResponse dispatch(final RecordedRequest request) {
                switch (request.getPath()) {
                case "/success/test":
                    if (doNotModifiedTest) {
                        return new MockResponse().setResponseCode(HttpServletResponse.SC_NOT_MODIFIED);
                    }
                    return new MockResponse().setResponseCode(HttpServletResponse.SC_OK)
                            .addHeader("Content-Type", MEDIA_TYPE_FHIR_XML).addHeader("ETag", "W/123")
                            .addHeader("Expires", "Fri, 02 Dec 2050 16:00:00 GMT").setBody(TEXT);
                case "/error/test":
                    return new MockResponse().setResponseCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                case "/post":
                    return new MockResponse().setResponseCode(HttpServletResponse.SC_CREATED);
                case "/unauthorized/test":
                    return new MockResponse().setResponseCode(HttpServletResponse.SC_UNAUTHORIZED);
                case "/collection":
                    return new MockResponse().addHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .setBody("[\"a\", \"b\"]");
                default:
                    return new MockResponse().setResponseCode(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        };
    }

    @BeforeAll
    static void beforeAll() {
        assertDoesNotThrow(() -> {
            var client = new CuiRestClientBuilder(LOG).url("http://localhost")
                    // CDI not started yet. Resolving tracing not possible!
                    .tracingEnabled(false).build(TestResource.class);
            client.close();
        }, "Building CUI REST client before CDI is available should be possible.");
    }

    @BeforeEach
    void beforeEach() {
        spans.clear();
        mdcMap.clear();
        spanEqualsMdc = true;
    }

    @AfterEach
    void closeServiceResource() {
        if (null != service) {
            try {
                service.close();
                service = null;
            } catch (final Exception e) {
                LOG.debug("could not close service", e);
            }
        }
    }

    @Test
    void testError() {
        final var underTest = new CuiRestClientBuilder(LOG).url(mockWebServer.url("error").toString()).basicAuth("user",
                "pass");
        service = underTest.build(TestResource.class);

        assertThrows(WebApplicationException.class, service::test);

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "-- Client request info --");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO,
                "Request URI: " + mockWebServer.url("error/test"));
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Method: GET");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Headers:");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Accept: [application/fhir+xml]");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Authorization: [Basic dXNlcjpwYXNz]");
    }

    @Test
    void testResponseError() {
        service = new CuiRestClientBuilder(LOG).url(mockWebServer.url("error").toString()).build(TestResource.class);
        assertDoesNotThrow(service::getResponse);
    }

    @Test
    void requestWithBody() {
        service = new CuiRestClientBuilder(LOG).url(mockWebServer.url("").toString()).build(TestResource.class);
        assertDoesNotThrow(() -> service.postString(STRING));

        assertTraceHeaders(takeRequest().getHeaders());

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "-- Client request info --");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Request URI: " + mockWebServer.url("/post"));
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Method: POST");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Headers:");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Accept: [application/json]");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Content-Type: [text/plain]");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Body:");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, STRING);

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "traceId");
    }

    @Test
    void shouldLogHappyCase() {
        service = new CuiRestClientBuilder(LOG).url(mockWebServer.url("").toString()).build(TestResource.class);
        var response = service.getResponse();
        assertNotNull(response);
        CuiRestClientBuilder.debugResponse(response, LOG);
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.DEBUG, "-- Client response filter --");
    }

    @Test
    void shouldDeactivateLogTracing() {
        service = new CuiRestClientBuilder(LOG).traceLogEnabled(false).url(mockWebServer.url("success").toString())
                .build(TestResource.class);

        final var result = service.test();
        assertEquals(TEXT, result);
        assertNotNull(takeRequest(), "Request didn't happen");

        LogAsserts.assertNoLogMessagePresent(TestLogLevel.TRACE, "-- Client request info --");
        LogAsserts.assertNoLogMessagePresent(TestLogLevel.TRACE, "-- Client response info --");
    }

    @Test
    void testCorrectHostname() {
        final var handshakeCertificates = localhost();
        mockWebServer.useHttps(handshakeCertificates.sslSocketFactory(), false);

        service = new CuiRestClientBuilder(LOG).sslContext(handshakeCertificates.sslContext())
                .url(mockWebServer.url("success").toString()).build(TestResource.class);

        final var result = service.test();

        assertEquals(TEXT, result);
        assertNotNull(takeRequest(), "Request didn't happen");
    }

    @Test
    void testIncorrectHostname() throws Exception {
        final var hostname = InetAddress.getLocalHost().getCanonicalHostName();

        final var heldCertificate = new HeldCertificate.Builder().commonName(hostname).build();
        final var handshakeCertificates = new HandshakeCertificates.Builder().heldCertificate(heldCertificate)
                .addTrustedCertificate(heldCertificate.certificate()).build();
        mockWebServer.useHttps(handshakeCertificates.sslSocketFactory(), false);
        mockWebServer.start(InetAddress.getByName(hostname), 0);

        assertNotEquals("localhost", hostname);

        final var port = mockWebServer.getPort();
        mockWebServer.setDispatcher(getDispatcher());

        service = new CuiRestClientBuilder(LOG).sslContext(handshakeCertificates.sslContext())
                .url(mockWebServer.url("https://" + hostname + ":" + port + "/success").toString())
                .build(TestResource.class);
        final var result = service.test();
        assertEquals(TEXT, result);
        assertNotNull(takeRequest(), "Request didn't happen");
    }

    @Test
    void testIpAddress() throws IOException {
        final var hostname = InetAddress.getLocalHost().getHostAddress();

        final var handshakeCertificates = localhost();
        mockWebServer.useHttps(handshakeCertificates.sslSocketFactory(), false);
        mockWebServer.start(InetAddress.getLocalHost(), 0);

        final var port = mockWebServer.getPort();
        mockWebServer.setDispatcher(getDispatcher());

        service = new CuiRestClientBuilder(LOG).connectionMetadata(ConnectionMetadata.builder()
                .serviceUrl(mockWebServer.url("https://" + hostname + ":" + port + "/success").toString())
                .tracingEnabled(false).authenticationType(AuthenticationType.BASIC)
                .loginCredentials(LoginCredentials.builder().username("user").password("pass").build())
                .sslContext(handshakeCertificates.sslContext()).build()).build(TestResource.class);

        assertThrows(ProcessingException.class, () -> service.test());
    }

    @Test
    void registersExceptionMapper() {
        final var serviceBoom = new CuiRestClientBuilder(LOG).url(mockWebServer.url("b00m").toString())
                .registerExceptionMapper(response -> new ExceptionMapperTestException()).build(TestResource.class);
        assertThrows(ExceptionMapperTestException.class, () -> serviceBoom.test());

        final var serviceError = new CuiRestClientBuilder(LOG).url(mockWebServer.url("error").toString())
                .registerExceptionMapper(response -> new ExceptionMapperTestException()).build(TestResource.class);
        assertThrows(ExceptionMapperTestException.class, () -> serviceError.test());
    }

    @Test
    void shouldDisableTracing() {
        service = new CuiRestClientBuilder(LOG).url(mockWebServer.url("success").toString()).tracingEnabled(false)
                .build(TestResource.class);

        assertDoesNotThrow(() -> service.test());
        final var headers = takeRequest().getHeaders();
        assertNull(headers.get("X-B3-TraceId"));
        assertNull(headers.get("X-B3-SpanId"));
        LogAsserts.assertNoLogMessagePresent(TestLogLevel.INFO, "traceId");
    }

    @Test
    void basicAuth() {
        service = new CuiRestClientBuilder(LOG)
                .connectionMetadata(ConnectionMetadata.builder().serviceUrl(mockWebServer.url("success").toString())
                        .tracingEnabled(false).authenticationType(AuthenticationType.BASIC)
                        .loginCredentials(LoginCredentials.builder().username("user").password("pass").build()).build())
                .build(TestResource.class);

        assertDoesNotThrow(() -> service.test());
        final var headers = takeRequest().getHeaders();
        assertEquals("Basic dXNlcjpwYXNz", headers.get("Authorization"));
    }

    @Test
    void sendAuthenticationToken() {
        service = new CuiRestClientBuilder(LOG)
                .connectionMetadata(ConnectionMetadata.builder().serviceUrl(mockWebServer.url("success").toString())
                        .tracingEnabled(false).authenticationType(AuthenticationType.TOKEN_APPLICATION)
                        .tokenResolver(new StaticTokenResolver("Authentication", "BEARER abc")).build())
                .build(TestResource.class);

        assertDoesNotThrow(() -> service.test());
        final var headers = takeRequest().getHeaders();
        assertEquals("BEARER abc", headers.get("Authentication"));
    }

    @Test
    void shouldHandleAllAttributes() {
        var builder = new CuiRestClientBuilder(LOG)
                .connectionMetadata(ConnectionMetadata.builder().serviceUrl(mockWebServer.url("success").toString())
                        .authenticationType(AuthenticationType.NONE).connectionTimeout(4).proxyHost("host")
                        .proxyPort(8080).disableHostNameVerification(true).contextMap(immutableMap("key", "value"))
                        .connectionTimeoutUnit(TimeUnit.MICROSECONDS).readTimeout(7).build());
        assertDoesNotThrow(() -> builder.build(TestResource.class));

    }

    @Test
    void sendGeneralToken() {
        service = new CuiRestClientBuilder(LOG)
                .connectionMetadata(ConnectionMetadata.builder().serviceUrl(mockWebServer.url("success").toString())
                        .tracingEnabled(false).authenticationType(AuthenticationType.TOKEN_APPLICATION)
                        .tokenResolver(new StaticTokenResolver("Key", "Value")).build())
                .build(TestResource.class);

        assertDoesNotThrow(() -> service.test());
        final var headers = takeRequest().getHeaders();
        assertEquals("Value", headers.get("Key"));
    }

    @Test
    void sendsTracingSpansToReporter() {
        configureTracingReporter();

        service = new CuiRestClientBuilder(LOG).url(mockWebServer.url("success").toString()).build(TestResource.class);

        assertDoesNotThrow(() -> service.test());
        assertEquals(1, spans.size());
    }

    @Test
    void canEnableDefaultExceptionHandler() {
        service = new CuiRestClientBuilder(LOG).url(mockWebServer.url("error").toString()).build(TestResource.class);
        assertThrows(InternalServerErrorException.class, () -> service.test());

        service = new CuiRestClientBuilder(LOG).url(mockWebServer.url("error").toString())
                .enableDefaultExceptionHandler().build(TestResource.class);

        LogAsserts.assertNoLogMessagePresent(TestLogLevel.ERROR, "Portal-541");

        try {
            service.test();
        } catch (InternalServerErrorException e) {
            fail("WebApplicationException expected");
        } catch (Exception e) {
            assertEquals(WebApplicationException.class, e.getClass());
            assertTrue(e.getMessage().contains("code 500"));
        }
    }

    /**
     * FIXME handle collections with default JAX-B instead of Jackson2 provider.
     */
    @Test
    void handlesCollections() {
        service = new CuiRestClientBuilder(LOG).url(mockWebServer.url("").toString()).tracingEnabled(false)
                .build(TestResource.class);
        // RESTEASY003145: Unable to find a MessageBodyReader of content-type
        // application/json
        // and type interface java.util.List
        assertThrows(ProcessingException.class, () -> service.collection());
    }

    @Test
    void builderShouldHandleAdditionalAttributes() throws URISyntaxException {
        var builder = new CuiRestClientBuilder(LOG);
        TypedGenerator<URL> urls = new URLGenerator();
        builder.url(urls.next());
        builder.uri(urls.next().toURI());
        var name = letterStrings().next();
        assertThrows(IllegalArgumentException.class, () -> builder.url(name));
        builder.bearerAuthToken(name);
        builder.queryParamStyle(enumValues(QueryParamStyle.class).next());
        builder.followRedirects(true);
        assertNotNull(builder.getConfiguration());
        assertDoesNotThrow(() -> builder.build(TestResource.class));
    }

    @Test
    void metaTraces() {
        configureTracingReporter();

        final var tracer = tracerProvider.get();

        final var metaSpan = tracer
                // The span is in "scope" meaning downstream code such as loggers can see trace
                // IDs
                .startScopedSpan("meta-operations").tag("user-id", "masterAdmin");

        try {
            new CuiRestClientBuilder(LOG).url(mockWebServer.url("success").toString()).build(TestResource.class).test();

            new CuiRestClientBuilder(LOG).url(mockWebServer.url("").toString()).build(TestResource.class)
                    .postString("post-string");

            // this one is not traced due to https://github.com/openzipkin/brave/issues/1141
            new CuiRestClientBuilder(LOG).url(mockWebServer.url("unauthorized").toString()).build(TestResource.class)
                    .test();
        } catch (final WebApplicationException e) { // unauthorized
            metaSpan.error(e);
        } catch (final Throwable e) {
            metaSpan.error(e); // Unless you handle exceptions, you might not know the operation
                               // failed!
            fail("Error during request", e);
        } finally {
            metaSpan.finish(); // always finish the span
        }

        assertEquals(4, spans.size(), "Unexpected number of spans. Expected is 1 meta span + 2 request spans. Spans:"
                + System.lineSeparator() + Joiner.on(System.lineSeparator()).join(spans));

        assertTrue(spanEqualsMdc, "MDC does not match reported span. MDC entries are: " + System.lineSeparator()
                + Joiner.on(System.lineSeparator()).join(mdcMap));
    }

    @javax.enterprise.inject.Produces
    @ApplicationScoped
    private SpanHandler testSpanReporter() {
        return new SpanHandler() {

            @Override
            public boolean end(TraceContext context, MutableSpan span, Cause cause) {
                // ensure the MDC has the same data as the current TraceContext
                try (final var scope = Tracing.current().currentTraceContext().maybeScope(context)) {
                    LOG.info(span.toString()); // like brave.Tracing.LoggingReporter#report
                    spans.add(span);

                    final var mdc = ThreadContext.getContext();
                    mdcMap.add(mdc);

                    spanEqualsMdc &= span.id().equals(mdc.get("spanId"));
                    spanEqualsMdc &= span.traceId().equals(mdc.get("traceId"));
                    spanEqualsMdc &= nullToEmpty(span.parentId()).equals(nullToEmpty(mdc.get("parentId")));
                }
                return true;
            }
        };
    }

    private RecordedRequest takeRequest() {
        try {
            return mockWebServer.takeRequest(25, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            fail("Could not take request", e);
        }
        throw new IllegalStateException();
    }

    private void assertTraceHeaders(final Headers headers) {
        assertNotNull(headers.get("X-B3-TraceId"));
        assertNotNull(headers.get("X-B3-SpanId"));
        assertEquals("1", headers.get("X-B3-Sampled"));
    }

    private void configureTracingReporter() {
        final var reporterUrl = mockWebServer.url("/api/v2/spans").toString();
        configuration.fireEvent(PORTAL_TRACING_REPORTER_URL, reporterUrl);
    }

    private static class ExceptionMapperTestException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        ExceptionMapperTestException() {
        }
    }
}
