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

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_ENABLED;
import static de.cuioss.test.generator.Generators.enumValues;
import static de.cuioss.test.generator.Generators.letterStrings;
import static de.cuioss.test.juli.LogAsserts.assertLogMessagePresentContaining;
import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static okhttp3.tls.internal.TlsUtil.localhost;
import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.configuration.connections.impl.AuthenticationType;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.portal.configuration.connections.impl.StaticTokenResolver;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.generator.impl.URLGenerator;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.uimodel.application.LoginCredentials;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Setter;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import okhttp3.Headers;
import okhttp3.tls.HandshakeCertificates;
import okhttp3.tls.HeldCertificate;
import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serial;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableAutoWeld
@EnablePortalConfiguration(configuration = PORTAL_TRACING_ENABLED + ":true")
@EnableMockWebServer(manualStart = true)
@EnableTestLogger(trace = {CuiRestClientBuilder.class, CuiRestClientBuilderTest.class})
@AddExtensions(ResteasyCdiExtension.class)
class CuiRestClientBuilderTest implements MockWebServerHolder {

    private static final CuiLogger LOG = new CuiLogger(CuiRestClientBuilderTest.class);

    private static final String TEXT = "täscht";
    private static final String MEDIA_TYPE_FHIR_XML = "application/fhir+xml";
    private static final String STRING = Generators.nonEmptyStrings().next();
    private TestResource service;

    public interface TestResource extends Closeable {

        @GET
        @Path("test")
        @jakarta.ws.rs.Produces(MEDIA_TYPE_FHIR_XML)
        String test();

        @POST
        @Path("post")
        @Consumes(MediaType.TEXT_PLAIN)
        void postString(final String body);

        @GET
        @Path("collection")
        @jakarta.ws.rs.Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        List<String> collection();

        @GET
        @Path("test")
        Response getResponse();
    }

    @Setter
    private MockWebServer mockWebServer;

    private final boolean doNotModifiedTest = false;

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {

            @Override
            public @NotNull MockResponse dispatch(final @NotNull RecordedRequest request) {
                return switch (request.getPath()) {
                    case "/success/test" -> {
                        if (doNotModifiedTest) {
                            yield new MockResponse(HttpServletResponse.SC_NOT_MODIFIED);
                        }
                        yield new MockResponse(HttpServletResponse.SC_OK, Headers.of("Content-Type", MEDIA_TYPE_FHIR_XML, "ETag", "W/123", "Expires", "Fri, 02 Dec 2050 16:00:00 GMT"), TEXT);
                    }
                    case "/error/test" -> new MockResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    case "/post" -> new MockResponse(HttpServletResponse.SC_CREATED);
                    case "/unauthorized/test" -> new MockResponse(HttpServletResponse.SC_UNAUTHORIZED);
                    case "/collection" ->
                        new MockResponse(HttpServletResponse.SC_OK, Headers.of("Content-Type", MediaType.APPLICATION_JSON), "[\"a\", \"b\"]");
                    default -> new MockResponse(HttpServletResponse.SC_NOT_FOUND);
                };
            }
        };
    }

    @BeforeAll
    static void beforeAll() {
        assertDoesNotThrow(() -> {
            var client = new CuiRestClientBuilder(LOG).url("http://localhost").build(TestResource.class);
            client.close();
        }, "Building CUI REST client before CDI is available should be possible.");
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
    void error() {
        final var underTest = new CuiRestClientBuilder(LOG)
                .url(mockWebServer.url("error").toString())
                .basicAuth("user", "pass");
        service = underTest.build(TestResource.class);

        assertThrows(WebApplicationException.class, service::test);

        assertLogMessagePresentContaining(TestLogLevel.INFO, "-- Client request info --");
        assertLogMessagePresentContaining(TestLogLevel.INFO,
                "Request URI: " + mockWebServer.url("error/test"));
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Method: GET");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Headers:");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Accept: [application/fhir+xml]");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Authorization: [Basic dXNlcjpwYXNz]");
    }

    @Test
    void responseError() {
        service = new CuiRestClientBuilder(LOG)
                .url(mockWebServer.url("error").toString())
                .build(TestResource.class);

        assertDoesNotThrow(service::getResponse);
    }

    @Test
    void requestWithBody() {
        service = new CuiRestClientBuilder(LOG)
                .url(mockWebServer.url("").toString())
                .build(TestResource.class);
        assertDoesNotThrow(() -> service.postString(STRING));

        assertLogMessagePresentContaining(TestLogLevel.INFO, "-- Client request info --");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Request URI: " + mockWebServer.url("/post"));
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Method: POST");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Headers:");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Accept: [application/json]");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Content-Type: [text/plain]");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Body:");
        assertLogMessagePresentContaining(TestLogLevel.INFO, STRING);
    }

    @Test
    void shouldLogHappyCase() {
        service = new CuiRestClientBuilder(LOG).url(mockWebServer.url("").toString()).build(TestResource.class);
        var response = service.getResponse();
        assertNotNull(response);
        CuiRestClientBuilder.debugResponse(response, LOG);
        assertLogMessagePresentContaining(TestLogLevel.DEBUG, "-- Client response filter --");
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
    @Disabled
    void correctHostname() {
        final var handshakeCertificates = localhostCerts();
        mockWebServer.useHttps(handshakeCertificates.sslSocketFactory());
        service = new CuiRestClientBuilder(LOG)
                .sslContext(handshakeCertificates.sslContext())
                .url(mockWebServer.url("success").toString())
                .build(TestResource.class);

        final var result = service.test();

        assertEquals(TEXT, result);
        assertNotNull(takeRequest(), "Request didn't happen");
    }

    @Test
    void incorrectHostname() throws Exception {
        final var hostname = InetAddress.getLocalHost().getCanonicalHostName();

        final var heldCertificate = new HeldCertificate.Builder().commonName(hostname).build();
        final var handshakeCertificates = new HandshakeCertificates.Builder().heldCertificate(heldCertificate)
                .addTrustedCertificate(heldCertificate.certificate()).build();
        mockWebServer.useHttps(handshakeCertificates.sslSocketFactory());
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
    void ipAddress() throws IOException {
        final var hostname = InetAddress.getLocalHost().getHostAddress();

        final var handshakeCertificates = localhost();
        mockWebServer.useHttps(handshakeCertificates.sslSocketFactory());
        mockWebServer.start(InetAddress.getLocalHost(), 0);

        final var port = mockWebServer.getPort();
        mockWebServer.setDispatcher(getDispatcher());

        service = new CuiRestClientBuilder(LOG).connectionMetadata(ConnectionMetadata.builder()
                .serviceUrl(mockWebServer.url("https://" + hostname + ":" + port + "/success").toString())
                .authenticationType(AuthenticationType.BASIC)
                .loginCredentials(LoginCredentials.builder().username("user").password("pass").build())
                .sslContext(handshakeCertificates.sslContext()).build()).build(TestResource.class);

        assertThrows(ProcessingException.class, () -> service.test());
    }

    @Test
    void registersExceptionMapper() {
        final var serviceBoom = new CuiRestClientBuilder(LOG).url(mockWebServer.url("b00m").toString())
                .registerExceptionMapper(response -> new ExceptionMapperTestException()).build(TestResource.class);
        assertThrows(ExceptionMapperTestException.class, serviceBoom::test);

        final var serviceError = new CuiRestClientBuilder(LOG).url(mockWebServer.url("error").toString())
                .registerExceptionMapper(response -> new ExceptionMapperTestException()).build(TestResource.class);
        assertThrows(ExceptionMapperTestException.class, serviceError::test);
    }

    @Test
    void basicAuth() {
        service = new CuiRestClientBuilder(LOG)
                .connectionMetadata(ConnectionMetadata.builder()
                        .serviceUrl(mockWebServer.url("success").toString())
                        .authenticationType(AuthenticationType.BASIC)
                        .loginCredentials(LoginCredentials.builder()
                                .username("user")
                                .password("pass")
                                .build())
                        .build())
                .build(TestResource.class);

        assertDoesNotThrow(() -> service.test());
        final var headers = takeRequest().getHeaders();
        assertEquals("Basic dXNlcjpwYXNz", headers.get("Authorization"));
    }

    @Test
    void sendAuthenticationToken() {
        service = new CuiRestClientBuilder(LOG)
                .connectionMetadata(ConnectionMetadata.builder()
                        .serviceUrl(mockWebServer.url("success").toString())
                        .authenticationType(AuthenticationType.TOKEN_APPLICATION)
                        .tokenResolver(new StaticTokenResolver("Authentication", "BEARER abc")).build())
                .build(TestResource.class);

        assertDoesNotThrow(() -> service.test());
        final var headers = takeRequest().getHeaders();
        assertEquals("BEARER abc", headers.get("Authentication"));
    }

    @Test
    void shouldHandleAllAttributes() {
        var builder = new CuiRestClientBuilder(LOG)
                .connectionMetadata(ConnectionMetadata.builder()
                        .serviceUrl(mockWebServer.url("success").toString())
                        .authenticationType(AuthenticationType.NONE).connectionTimeout(4).proxyHost("host")
                        .proxyPort(8080).disableHostNameVerification(true).contextMap(immutableMap("key", "value"))
                        .connectionTimeoutUnit(TimeUnit.MICROSECONDS).readTimeout(7).build());
        assertDoesNotThrow(() -> builder.build(TestResource.class));

    }

    @Test
    void sendGeneralToken() {
        CuiRestClientBuilder cuiRestClientBuilder = new CuiRestClientBuilder(LOG);
        cuiRestClientBuilder.connectionMetadata(ConnectionMetadata.builder()
                .serviceUrl(mockWebServer.url("success").toString())
                .authenticationType(AuthenticationType.TOKEN_APPLICATION)
                .tokenResolver(new StaticTokenResolver("Key", "Value")).build());
        service = cuiRestClientBuilder.build(TestResource.class);

        assertDoesNotThrow(() -> service.test());
        final var headers = takeRequest().getHeaders();
        assertEquals("Value", headers.get("Key"));
    }

    @Test
    void canEnableDefaultExceptionHandler() {
        service = new CuiRestClientBuilder(LOG)
                .url(mockWebServer.url("error").toString())
                .build(TestResource.class);
        assertThrows(InternalServerErrorException.class, () -> service.test());

        service = new CuiRestClientBuilder(LOG).url(mockWebServer.url("error").toString())
                .enableDefaultExceptionHandler().build(TestResource.class);

        LogAsserts.assertNoLogMessagePresent(TestLogLevel.ERROR, "Portal-541");

        try {
            service.test();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("500"));
        }
    }

    /**
     * FIXME handle collections with default JAX-B instead of Jackson2 provider.
     */
    @Test
    void handlesCollections() {
        service = new CuiRestClientBuilder(LOG)
                .url(mockWebServer.url("").toString())
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

    private RecordedRequest takeRequest() {
        try {
            return mockWebServer.takeRequest(25, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            fail("Could not take request", e);
        }
        throw new IllegalStateException();
    }

    private static class ExceptionMapperTestException extends RuntimeException {

        @Serial
        private static final long serialVersionUID = 1L;

        ExceptionMapperTestException() {
        }
    }

    /**
     * @return same as #{okhttp3.tls.internal.TlsUtil#localhost()} but
     * additionally with subjectAlternativeName {@code 127.0.0.1}.
     */
    private HandshakeCertificates localhostCerts() {
        // Generate a self-signed cert for the server to serve and the client to trust.
        var heldCertificate = new HeldCertificate.Builder()
                .commonName("localhost")
                .addSubjectAlternativeName("localhost")
                .addSubjectAlternativeName("127.0.01")
                .addSubjectAlternativeName("localhost.localdomain")
                .build();
        return new HandshakeCertificates.Builder()
                .heldCertificate(heldCertificate)
                .addTrustedCertificate(heldCertificate.certificate())
                .build();
    }
}
