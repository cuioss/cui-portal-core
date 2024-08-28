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
package de.cuioss.portal.authentication.oauth.impl;

import de.cuioss.portal.authentication.oauth.OAuthConfigKeys;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.test.generator.impl.URLGenerator;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import mockwebserver3.MockWebServer;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.exceptions.WeldException;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
@EnablePortalConfiguration
@EnableMockWebServer
@AddExtensions(ResteasyCdiExtension.class)
class Oauth2DiscoveryConfigurationProducerTest
    implements ShouldHandleObjectContracts<Oauth2DiscoveryConfigurationProducer>, MockWebServerHolder {

    @Setter
    private MockWebServer mockWebServer;

    @Inject
    @Getter
    private Oauth2DiscoveryConfigurationProducer underTest;

    @Inject
    private PortalTestConfiguration configuration;

    @Getter
    private final OIDCWellKnownDispatcher dispatcher = new OIDCWellKnownDispatcher();

    @BeforeEach
    void beforeEach() {
        configuration.update(OAuthConfigKeys.CONFIG_VALIDATION_ENABLED, "false");
    }

    @Test
    void testInit() {
        dispatcher.configure(configuration, mockWebServer);

        var result = underTest.getConfiguration();

        assertNotNull(result);
        assertNotNull(result.getTokenUri());
        assertNotNull(result.getAuthorizeUri());
        assertNotNull(result.getUserInfoUri());
        assertNotNull(result.getLogoutUri());
    }

    @Test
    void shouldOverwriteInternalUrls() {
        dispatcher.configure(configuration, mockWebServer);
        var url1 = new URLGenerator().next().toString();
        var url2 = new URLGenerator().next().toString();
        configuration.update(
            OAuthConfigKeys.OPEN_ID_SERVER_TOKEN_URL, url1,
            OAuthConfigKeys.OPEN_ID_SERVER_USER_INFO_URL, url2);

        var result = underTest.getConfiguration();

        assertEquals(url1, result.getTokenUri());
        assertEquals(url2, result.getUserInfoUri());
    }

    @Test
    void invalidOpenIdConfig() {
        configuration.update(OAuthConfigKeys.CONFIG_VALIDATION_ENABLED, "true");
        dispatcher.setSimulateInvalidOidcConfig(true);
        dispatcher.configure(configuration, mockWebServer);

        WeldException ex = assertThrows(WeldException.class, underTest::getConfiguration);

        assertNotNull(ex.getCause());
        assertNotNull(ex.getCause().getCause());
        assertEquals(IllegalStateException.class, ex.getCause().getCause().getClass());
    }
}
