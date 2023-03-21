package de.cuioss.portal.authentication.oauth.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.OAuthConfigKeys;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.portal.core.test.mocks.PortalTestConfiguration;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.FileLoaderUtility;
import de.cuioss.tools.io.FileTypePrefix;
import lombok.Getter;
import lombok.Setter;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

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

    static final FileLoader CONFIGURATION =
        FileLoaderUtility.getLoaderForPath(FileTypePrefix.CLASSPATH + "/openid-configuration");

    @Test
    void testInit() {

        configuration.put(OAuthConfigKeys.OPEN_ID_SERVER_BASE_URL,
                "http://localhost:" + mockWebServer.getPort());
        configuration.put(OAuthConfigKeys.OPEN_ID_DISCOVER_PATH, "oidc");
        configuration.fireEvent();

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

        configuration.put(OAuthConfigKeys.OPEN_ID_SERVER_BASE_URL,
                "http://localhost:" + mockWebServer.getPort());
        configuration.put(OAuthConfigKeys.OPEN_ID_DISCOVER_PATH, "oidc");
        configuration.fireEvent();

        result = underTest.getConfiguration();
        assertNotNull(result);
    }

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                switch (request.getPath()) {
                    case "/oidc":
                        return new MockResponse().setResponseCode(HttpServletResponse.SC_OK)
                                .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                                .setBody(FileLoaderUtility.toStringUnchecked(CONFIGURATION));
                    default:
                        return new MockResponse().setResponseCode(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        };
    }
}
