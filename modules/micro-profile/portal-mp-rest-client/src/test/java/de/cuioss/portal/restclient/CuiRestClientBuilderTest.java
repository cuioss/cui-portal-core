/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
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
package de.cuioss.portal.restclient;

import de.cuioss.portal.configuration.connections.impl.AuthenticationType;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.portal.configuration.connections.impl.StaticTokenResolver;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.generator.impl.URLGenerator;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.test.mockwebserver.EnableMockWebServer;
import de.cuioss.test.mockwebserver.URIBuilder;
import de.cuioss.test.mockwebserver.dispatcher.HttpMethodMapper;
import de.cuioss.test.mockwebserver.mockresponse.MockResponseConfig;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.uimodel.application.LoginCredentials;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mockwebserver3.MockWebServer;
import okhttp3.tls.HandshakeCertificates;
import okhttp3.tls.HeldCertificate;
import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serial;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_ENABLED;
import static de.cuioss.test.generator.Generators.enumValues;
import static de.cuioss.test.generator.Generators.letterStrings;
import static de.cuioss.test.juli.LogAsserts.assertLogMessagePresentContaining;
import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static okhttp3.tls.internal.TlsUtil.localhost;
import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
@EnablePortalConfiguration(configuration = PORTAL_TRACING_ENABLED + ":true")
@EnableMockWebServer
@EnableTestLogger(trace = {CuiRestClientBuilder.class, CuiRestClientBuilderTest.class})
@AddExtensions(ResteasyCdiExtension.class)
@ExplicitParamInjection
class CuiRestClientBuilderTest {

    private static final CuiLogger LOGGER = new CuiLogger(CuiRestClientBuilderTest.class);

    private static final String TEXT = "täscht";
    private static final String MEDIA_TYPE_FHIR_XML = "application/fhir+xml";
    private static final String STRING = Generators.nonEmptyStrings().next();
    private TestResource service;

    public interface TestResource extends Closeable {

        @GET
        @Path("test")
        @Produces(MEDIA_TYPE_FHIR_XML)
        String test();

        @POST
        @Path("post")
        @Consumes(MediaType.TEXT_PLAIN)
        void postString(final String body);

        @GET
        @Path("collection")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        List<String> collection();

        @GET
        @Path("test")
        Response getResponse();
    }

    @BeforeAll
    static void beforeAll() {
        assertDoesNotThrow(() -> {
            var client = new CuiRestClientBuilder(LOGGER).url("http://localhost").build(TestResource.class);
            client.close();
        }, "Building CUI REST client before CDI is available should be possible.");
    }

    @AfterEach
    void closeServiceResource() {
        if (null != service) {
            try {
                service.close();
                service = null;
            } catch (final IOException e) {
                LOGGER.debug("could not close service", e);
            }
        }
    }

    @Test
    @MockResponseConfig(path = "/error/test", status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
    void error(MockWebServer mockWebServer) {
        final var underTest = new CuiRestClientBuilder(LOGGER)
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
    @MockResponseConfig(path = "/error/test", status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
    void responseError(MockWebServer mockWebServer) {
        service = new CuiRestClientBuilder(LOGGER)
                .url(mockWebServer.url("error").toString())
                .build(TestResource.class);

        assertDoesNotThrow(service::getResponse);
    }

    @Test
    @MockResponseConfig(path = "/post", status = HttpServletResponse.SC_CREATED, method = HttpMethodMapper.POST)
    void requestWithBody(URIBuilder uriBuilder) {
        service = new CuiRestClientBuilder(LOGGER)
                .url(uriBuilder.build().toString())
                .build(TestResource.class);
        assertDoesNotThrow(() -> service.postString(STRING));

        assertLogMessagePresentContaining(TestLogLevel.INFO, "-- Client request info --");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Request URI: " + uriBuilder.addPathSegment("/post").build().toString());
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Method: POST");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Headers:");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Accept: [application/json]");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Content-Type: [text/plain]");
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Body:");
        assertLogMessagePresentContaining(TestLogLevel.INFO, STRING);
    }

    @Test
    @MockResponseConfig(path = "/test", status = HttpServletResponse.SC_OK)
    void shouldLogHappyCase(MockWebServer mockWebServer) {
        service = new CuiRestClientBuilder(LOGGER).url(mockWebServer.url("").toString()).build(TestResource.class);
        var response = service.getResponse();
        assertNotNull(response);
        CuiRestClientBuilder.debugResponse(response, LOGGER);
        assertLogMessagePresentContaining(TestLogLevel.DEBUG, "-- Client response filter --");
    }

    @Test
    @MockResponseConfig(
            path = "/success/test",
            status = HttpServletResponse.SC_OK,
            headers = "Content-Type=" + MEDIA_TYPE_FHIR_XML + ";ETag=W/123;Expires=Fri, 02 Dec 2050 16:00:00 GMT",
            textContent = TEXT
    )
    void shouldDeactivateLogTracing(MockWebServer mockWebServer, URIBuilder uriBuilder) throws Exception {
        service = new CuiRestClientBuilder(LOGGER).traceLogEnabled(false).url(uriBuilder.addPathSegment("success").build().toString())
                .build(TestResource.class);

        final var result = service.test();
        assertEquals(TEXT, result);
        assertNotNull(mockWebServer.takeRequest(), "Request didn't happen");

        LogAsserts.assertNoLogMessagePresent(TestLogLevel.TRACE, "-- Client request info --");
        LogAsserts.assertNoLogMessagePresent(TestLogLevel.TRACE, "-- Client response info --");
    }

    @Test
    @MockResponseConfig(
            path = "/success/test",
            status = HttpServletResponse.SC_OK,
            headers = "Content-Type=" + MEDIA_TYPE_FHIR_XML + ";ETag=W/123;Expires=Fri, 02 Dec 2050 16:00:00 GMT",
            textContent = TEXT
    )
    void correctHostname(MockWebServer mockWebServer) throws Exception {
        final var handshakeCertificates = localhostCerts();
        mockWebServer.useHttps(handshakeCertificates.sslSocketFactory());
        service = new CuiRestClientBuilder(LOGGER)
                .sslContext(handshakeCertificates.sslContext())
                .url(mockWebServer.url("success").toString())
                .build(TestResource.class);

        final var result = service.test();

        assertEquals(TEXT, result);
        assertNotNull(mockWebServer.takeRequest(), "Request didn't happen");
    }

    @Test
    @MockResponseConfig(
            path = "/success/test",
            status = HttpServletResponse.SC_OK,
            headers = "Content-Type=" + MEDIA_TYPE_FHIR_XML + ";ETag=W/123;Expires=Fri, 02 Dec 2050 16:00:00 GMT",
            textContent = TEXT
    )
    void ipAddress(MockWebServer mockWebServer) throws Exception {
        final var hostname = InetAddress.getLocalHost().getHostAddress();

        final var handshakeCertificates = localhost();
        mockWebServer.useHttps(handshakeCertificates.sslSocketFactory());
        mockWebServer.start(InetAddress.getLocalHost(), 0);

        final var port = mockWebServer.getPort();

        service = new CuiRestClientBuilder(LOGGER).connectionMetadata(ConnectionMetadata.builder()
                .serviceUrl(mockWebServer.url("https://" + hostname + ":" + port + "/success").toString())
                .authenticationType(AuthenticationType.BASIC)
                .loginCredentials(LoginCredentials.builder().username("user").password("pass").build())
                .sslContext(handshakeCertificates.sslContext()).build()).build(TestResource.class);

        assertThrows(ProcessingException.class, () -> service.test());
    }

    @Test
    @MockResponseConfig(path = "/b00m/test", status = HttpServletResponse.SC_NOT_FOUND)
    @MockResponseConfig(path = "/error/test", status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
    void registersExceptionMapper(MockWebServer mockWebServer) {
        final var serviceBoom = new CuiRestClientBuilder(LOGGER).url(mockWebServer.url("b00m").toString())
                .registerExceptionMapper(response -> new ExceptionMapperTestException()).build(TestResource.class);
        assertThrows(ExceptionMapperTestException.class, serviceBoom::test);

        final var serviceError = new CuiRestClientBuilder(LOGGER).url(mockWebServer.url("error").toString())
                .registerExceptionMapper(response -> new ExceptionMapperTestException()).build(TestResource.class);
        assertThrows(ExceptionMapperTestException.class, serviceError::test);
    }

    @Test
    @MockResponseConfig(
            path = "/success/test",
            status = HttpServletResponse.SC_OK,
            headers = "Content-Type=" + MEDIA_TYPE_FHIR_XML + ";ETag=W/123;Expires=Fri, 02 Dec 2050 16:00:00 GMT",
            textContent = TEXT
    )
    void basicAuth(MockWebServer mockWebServer) throws Exception {
        service = new CuiRestClientBuilder(LOGGER)
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
        final var headers = mockWebServer.takeRequest().getHeaders();
        assertEquals("Basic dXNlcjpwYXNz", headers.get("Authorization"));
    }

    @Test
    @MockResponseConfig(
            path = "/success/test",
            status = HttpServletResponse.SC_OK,
            headers = "Content-Type=" + MEDIA_TYPE_FHIR_XML + ";ETag=W/123;Expires=Fri, 02 Dec 2050 16:00:00 GMT",
            textContent = TEXT
    )
    void sendAuthenticationToken(MockWebServer mockWebServer) throws Exception {
        service = new CuiRestClientBuilder(LOGGER)
                .connectionMetadata(ConnectionMetadata.builder()
                        .serviceUrl(mockWebServer.url("success").toString())
                        .authenticationType(AuthenticationType.TOKEN_APPLICATION)
                        .tokenResolver(new StaticTokenResolver("Authentication", "BEARER abc")).build())
                .build(TestResource.class);

        assertDoesNotThrow(() -> service.test());
        final var headers = mockWebServer.takeRequest().getHeaders();
        assertEquals("BEARER abc", headers.get("Authentication"));
    }

    @Test
    @MockResponseConfig(
            path = "/success/test",
            status = HttpServletResponse.SC_OK,
            headers = "Content-Type=" + MEDIA_TYPE_FHIR_XML + ";ETag=W/123;Expires=Fri, 02 Dec 2050 16:00:00 GMT",
            textContent = TEXT
    )
    void shouldHandleAllAttributes(MockWebServer mockWebServer) {
        var builder = new CuiRestClientBuilder(LOGGER)
                .connectionMetadata(ConnectionMetadata.builder()
                        .serviceUrl(mockWebServer.url("success").toString())
                        .authenticationType(AuthenticationType.NONE).connectionTimeout(4).proxyHost("host")
                        .proxyPort(8080).disableHostNameVerification(true).contextMap(immutableMap("key", "value"))
                        .connectionTimeoutUnit(TimeUnit.MICROSECONDS).readTimeout(7).build());
        assertDoesNotThrow(() -> builder.build(TestResource.class));

    }

    @Test
    @MockResponseConfig(
            path = "/success/test",
            status = HttpServletResponse.SC_OK,
            headers = "Content-Type=" + MEDIA_TYPE_FHIR_XML + ";ETag=W/123;Expires=Fri, 02 Dec 2050 16:00:00 GMT",
            textContent = TEXT
    )
    void sendGeneralToken(MockWebServer mockWebServer) throws Exception {
        CuiRestClientBuilder cuiRestClientBuilder = new CuiRestClientBuilder(LOGGER);
        cuiRestClientBuilder.connectionMetadata(ConnectionMetadata.builder()
                .serviceUrl(mockWebServer.url("success").toString())
                .authenticationType(AuthenticationType.TOKEN_APPLICATION)
                .tokenResolver(new StaticTokenResolver("Key", "Value")).build());
        service = cuiRestClientBuilder.build(TestResource.class);

        assertDoesNotThrow(() -> service.test());
        final var headers = mockWebServer.takeRequest().getHeaders();
        assertEquals("Value", headers.get("Key"));
    }

    @Test
    @MockResponseConfig(path = "/error/test", status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
    void canEnableDefaultExceptionHandler(MockWebServer mockWebServer) {
        service = new CuiRestClientBuilder(LOGGER)
                .url(mockWebServer.url("error").toString())
                .build(TestResource.class);
        assertThrows(InternalServerErrorException.class, () -> service.test());

        service = new CuiRestClientBuilder(LOGGER).url(mockWebServer.url("error").toString())
                .enableDefaultExceptionHandler().build(TestResource.class);

        LogAsserts.assertNoLogMessagePresent(TestLogLevel.ERROR, "Portal-541");

        try {
            service.test();
        } catch (WebApplicationException e) {
            assertTrue(e.getMessage().contains("500"));
        }
    }

    /**
     * Verifies that collection deserialization fails without a Jackson2 provider.
     */
    @Test
    @MockResponseConfig(
            path = "/collection",
            status = HttpServletResponse.SC_OK,
            headers = "Content-Type=" + MediaType.APPLICATION_JSON,
            textContent = "[\"a\", \"b\"]"
    )
    void handlesCollections(MockWebServer mockWebServer) {
        service = new CuiRestClientBuilder(LOGGER)
                .url(mockWebServer.url("").toString())
                .build(TestResource.class);
        // RESTEASY003145: Unable to find a MessageBodyReader of content-type
        // application/json
        // and type interface java.util.List
        assertThrows(ProcessingException.class, () -> service.collection());
    }

    @Test
    @MockResponseConfig(path = "/test", status = HttpServletResponse.SC_OK)
    void builderShouldHandleAdditionalAttributes(MockWebServer mockWebServer) throws Exception {
        var builder = new CuiRestClientBuilder(LOGGER);
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
