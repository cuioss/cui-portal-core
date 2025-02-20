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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.InetAddress;
import java.io.Closeable;

import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.Setter;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import okhttp3.Headers;
import okhttp3.tls.HeldCertificate;
import okhttp3.tls.HandshakeCertificates;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;

@EnableAutoWeld
@EnablePortalConfiguration(configuration = PORTAL_TRACING_ENABLED + ":true")
@EnableTestLogger
@AddExtensions({ ResteasyCdiExtension.class })
@EnableMockWebServer(manualStart = true)
public class HostnameVerificationTest implements MockWebServerHolder {

    private static final CuiLogger LOGGER = new CuiLogger(HostnameVerificationTest.class);

    private static final String TEXT = "Hello World";

    @Setter
    private MockWebServer mockWebServer;

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {
            @Override
            public @NotNull MockResponse dispatch(final @NotNull RecordedRequest request) {
                return new MockResponse(HttpServletResponse.SC_OK, Headers.of("Content-Type", "application/fhir+xml"), TEXT);
            }
        };
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

        var service = new CuiRestClientBuilder(LOGGER).sslContext(handshakeCertificates.sslContext())
                .url("https://" + hostname + ":" + port + "/success")
                .build(TestResource.class);
        final var result = service.test();
        assertEquals(TEXT, result);
        assertNotNull(mockWebServer.takeRequest(), "Request didn't happen");

        mockWebServer.shutdown();
    }

    @Path("/")
    public interface TestResource extends Closeable {
        @GET
        @Path("success/test")
        @jakarta.ws.rs.Produces("application/fhir+xml")
        String test();
    }
}
