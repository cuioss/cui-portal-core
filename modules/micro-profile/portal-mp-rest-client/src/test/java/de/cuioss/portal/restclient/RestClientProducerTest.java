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

import de.cuioss.portal.configuration.impl.producer.ConnectionMetadataProducer;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.test.mockwebserver.EnableMockWebServer;
import de.cuioss.test.mockwebserver.URIBuilder;
import de.cuioss.test.mockwebserver.mockresponse.MockResponse;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.Getter;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import java.io.Closeable;

import static org.junit.jupiter.api.Assertions.*;


@EnableAutoWeld
@EnablePortalConfiguration
@EnableMockWebServer
@AddBeanClasses({ConnectionMetadataProducer.class, RestClientProducer.class})
@AddExtensions(ResteasyCdiExtension.class)
@MockResponse(path = "/success/test", status = HttpServletResponse.SC_OK, textContent = "test", headers = "ETag=W/123")
class RestClientProducerTest {

    public interface TestResource extends Closeable {

        @GET
        @Path("test")
        String test();

    }

    @Inject
    @PortalRestClient(baseName = "abc", failOnInvalidConfiguration = false)
    @Getter
    private Provider<RestClientHolder<TestResource>> underTestProvider;

    @Inject
    private PortalTestConfiguration configuration;

    @Test

    void serviceNotAvailable() {
        assertNotNull(underTestProvider.get());
        assertFalse(underTestProvider.get().isServiceAvailable());
    }

    @Test
    @ExplicitParamInjection
    void serviceAvailable(URIBuilder uriBuilder) {
        configuration.update("abc.url", uriBuilder.addPathSegment("success").build().toString());
        assertNotNull(underTestProvider.get());
        assertTrue(underTestProvider.get().isServiceAvailable());
        assertEquals("test", underTestProvider.get().get().test());
    }

}
