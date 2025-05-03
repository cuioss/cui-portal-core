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

import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.test.mockwebserver.EnableMockWebServer;
import de.cuioss.test.mockwebserver.mockresponse.MockResponseConfig;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import mockwebserver3.MockWebServer;
import okhttp3.tls.HandshakeCertificates;
import okhttp3.tls.HeldCertificate;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.net.InetAddress;

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_ENABLED;
import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
@EnablePortalConfiguration(configuration = PORTAL_TRACING_ENABLED + ":true")
@EnableTestLogger
@AddExtensions({ResteasyCdiExtension.class})
@EnableMockWebServer(manualStart = true)
@MockResponseConfig(path = "/success/test", status = HttpServletResponse.SC_OK, textContent = "Hello World")
class HostnameVerificationTest {

    private static final CuiLogger LOGGER = new CuiLogger(HostnameVerificationTest.class);

    private static final String TEXT = "Hello World";

    @Test
    @ExplicitParamInjection
    void incorrectHostname(MockWebServer mockWebServer) throws Exception {
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
        @Path("test")
        String test();
    }
}