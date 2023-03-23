package de.cuioss.portal.authentication.oauth.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.inject.Inject;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.portal.core.test.mocks.PortalTestConfiguration;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import lombok.Getter;
import lombok.Setter;
import okhttp3.mockwebserver.MockWebServer;

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
    @PortalConfigurationSource
    private PortalTestConfiguration configuration;

    @Getter
    private OIDCWellKnownDispatcher dispatcher = new OIDCWellKnownDispatcher();

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
    void testReactOnConfigChanges() {
        var result = underTest.getConfiguration();
        assertNull(result);

        dispatcher.configure(configuration, mockWebServer);

        result = underTest.getConfiguration();
        assertNotNull(result);
    }

}
