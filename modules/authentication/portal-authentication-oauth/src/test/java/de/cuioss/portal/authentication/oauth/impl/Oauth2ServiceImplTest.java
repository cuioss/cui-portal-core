package de.cuioss.portal.authentication.oauth.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsIterableContaining.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.deltaspike.core.api.common.DeltaSpike;
import org.apache.myfaces.test.mock.MockHttpServletRequest;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.authentication.oauth.DeprecatedOauth2ConfigurationKeys;
import de.cuioss.portal.authentication.oauth.Oauth2AuthenticationFacade;
import de.cuioss.portal.configuration.OAuthConfigKeys;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.portal.core.test.mocks.PortalTestConfiguration;
import de.cuioss.test.jsf.mocks.CuiMockHttpServletRequest;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.FileLoaderUtility;
import de.cuioss.tools.io.FileTypePrefix;
import de.cuioss.tools.net.UrlParameter;
import lombok.Getter;
import lombok.Setter;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@EnableAutoWeld
@EnablePortalConfiguration
@EnableMockWebServer
@AddBeanClasses({ Oauth2DiscoveryConfigurationProducer.class })
@AddExtensions(ResteasyCdiExtension.class)
class Oauth2ServiceImplTest implements ShouldHandleObjectContracts<Oauth2ServiceImpl>, MockWebServerHolder {

    static final FileLoader CONFIGURATION =
        FileLoaderUtility.getLoaderForPath(FileTypePrefix.CLASSPATH + "/openid-configuration");

    static final FileLoader TOKEN =
        FileLoaderUtility.getLoaderForPath(FileTypePrefix.CLASSPATH + "/token.json");

    static final FileLoader CLIENT_TOKEN =
        FileLoaderUtility.getLoaderForPath(FileTypePrefix.CLASSPATH + "/clientToken.json");

    static final FileLoader USER_INFO =
        FileLoaderUtility.getLoaderForPath(FileTypePrefix.CLASSPATH + "/userInfo.json");

    String wellknown = null;

    FileLoader actualToken;

    FileLoader actualUserInfo;

    @Setter
    private MockWebServer mockWebServer;

    @Inject
    @Getter
    private Oauth2ServiceImpl underTest;

    @Inject
    @PortalConfigurationSource
    private PortalTestConfiguration configuration;

    @Produces
    @DeltaSpike
    private MockHttpServletRequest servletRequest;

    @BeforeEach
    void beforeEach() {
        servletRequest = new CuiMockHttpServletRequest();
        servletRequest.setPathInfo("some.url");
        actualToken = TOKEN;
        actualUserInfo = USER_INFO;
    }

    @Test
    void testCalcEncodedRedirectUrl() {
        configuration.put(DeprecatedOauth2ConfigurationKeys.CLIENT_ID, "clientId");
        configuration.put(DeprecatedOauth2ConfigurationKeys.CLIENT_SECRET, "secret");
        configuration.put(DeprecatedOauth2ConfigurationKeys.CURRENT_DEPLOYMENT_EXTERNAL_CONTEXT_PATH, "https://test/");
        configuration.fireEvent();
        // test default
        assertEquals("https%3A%2F%2Ftest%2Ftest", underTest.calcEncodedRedirectUrl("test"));
    }

    @Test
    void testDeprecatedCreateAuthenticatedUserInfoFailed() {
        configuration.put(DeprecatedOauth2ConfigurationKeys.OAUTH2_SERVER_REST_URI, "invalid");
        configuration.put(DeprecatedOauth2ConfigurationKeys.CLIENT_ID, "clientId");
        configuration.put(DeprecatedOauth2ConfigurationKeys.CLIENT_SECRET, "secret");
        configuration.put(DeprecatedOauth2ConfigurationKeys.CURRENT_DEPLOYMENT_EXTERNAL_CONTEXT_PATH, "path");
        configuration.fireEvent();
        var result =
            underTest.createAuthenticatedUserInfo(servletRequest, new UrlParameter("code", "123"),
                    new UrlParameter("state", "456"), "scopes", "code");
        assertNull(result);
    }

    @Test
    void testCreateAuthenticatedUserInfoFailed() {
        configureWellKnown();
        actualUserInfo = null;
        configuration.put(OAuthConfigKeys.OPEN_ID_SERVER_BASE_URL,
                "http://localhost:" + mockWebServer.getPort());
        configuration.put(OAuthConfigKeys.OPEN_ID_DISCOVER_PATH, ".well-known/openid-configuration");
        configuration.put(OAuthConfigKeys.OPEN_ID_CLIENT_ID, "clientId");
        configuration.put(OAuthConfigKeys.OPEN_ID_CLIENT_SECRET, "secret");
        configuration.put(OAuthConfigKeys.EXTERNAL_HOSTNAME, "http://path");
        configuration.fireEvent();

        var result =
            underTest.createAuthenticatedUserInfo(servletRequest, new UrlParameter("code", "123"),
                    new UrlParameter("state", "456"), "scopes", "code");
        assertNull(result);
    }

    @Test
    void testDeprecatedCreateAuthenticatedUserInfo() throws IOException, InterruptedException {

        configuration
                .put(DeprecatedOauth2ConfigurationKeys.OAUTH2_SERVER_REST_URI,
                        "http://localhost:" + mockWebServer.getPort());
        configuration.put(DeprecatedOauth2ConfigurationKeys.CLIENT_ID, "clientId");
        configuration.put(DeprecatedOauth2ConfigurationKeys.CLIENT_SECRET, "secret");
        configuration.put(DeprecatedOauth2ConfigurationKeys.CURRENT_DEPLOYMENT_EXTERNAL_CONTEXT_PATH, "path");
        configuration.fireEvent();
        var result =
            underTest.createAuthenticatedUserInfo(servletRequest, new UrlParameter("code", "123"),
                    new UrlParameter("state", "456"), "scopes", "code");
        var request1 = mockWebServer.takeRequest();
        var request2 = mockWebServer.takeRequest();
        assertNotNull(request1);
        assertNotNull(request2);
        var authHeader = request2.getHeaders().values("Authorization");
        assertFalse(authHeader.isEmpty());
        assertEquals("Bearer SlAV32hkKG", authHeader.get(0));
        assertNotNull(result);
        assertTrue(
                result.getContextMap().containsKey(Oauth2AuthenticationFacade.USERINFO_PREFIX_KEY + "account_type"));
        assertEquals("user",
                result.getContextMap().get(Oauth2AuthenticationFacade.USERINFO_PREFIX_KEY + "account_type"));
    }

    @Test
    void testCreateAuthenticatedUserInfo() throws InterruptedException {

        configureWellKnown();

        configuration.put(OAuthConfigKeys.OPEN_ID_SERVER_BASE_URL, "http://localhost:" + mockWebServer.getPort());
        configuration.put(OAuthConfigKeys.OPEN_ID_DISCOVER_PATH, ".well-known/openid-configuration");
        configuration.put(OAuthConfigKeys.OPEN_ID_CLIENT_ID, "clientId");
        configuration.put(OAuthConfigKeys.OPEN_ID_CLIENT_SECRET, "secret");
        configuration.put(OAuthConfigKeys.OPEN_ID_ROLE_MAPPER_CLAIM, "ehealth-suite-roles");
        configuration.put(OAuthConfigKeys.EXTERNAL_HOSTNAME, "http://path");
        configuration.fireEvent();

        var code = new BigInteger(260, new Random()).toString(32);

        var result = underTest.createAuthenticatedUserInfo(
                servletRequest,
                new UrlParameter("code", "123"),
                new UrlParameter("state", "456"),
                "scopes",
                code);

        var request1 = mockWebServer.takeRequest();
        var request2 = mockWebServer.takeRequest();
        var request3 = mockWebServer.takeRequest();
        var request4 = mockWebServer.takeRequest();

        assertNotNull(request1);

        assertNotNull(request2);

        assertNotNull(request3);
        var contentTypeHeader = request3.getHeaders().values("Content-Type");
        assertFalse(contentTypeHeader.isEmpty());
        assertEquals(MediaType.APPLICATION_FORM_URLENCODED, contentTypeHeader.get(0));
        var acceptHeader = request3.getHeaders().values("Accept");
        assertFalse(acceptHeader.isEmpty());
        assertEquals(MediaType.APPLICATION_JSON, acceptHeader.get(0));
        assertEquals(
                "grant_type=authorization_code&code=123&state=456&code_verifier=" + code + "&redirect_uri=http%3A%2F"
                        + "%2Fpathsome.url",
                request3.getBody().readUtf8());

        assertNotNull(request4);
        var authHeader = request4.getHeaders().values("Authorization");
        assertFalse(authHeader.isEmpty());
        assertEquals("Bearer SlAV32hkKG", authHeader.get(0));
        assertNotNull(result);
        assertEquals("j.doe", result.getDisplayName());
        assertThat(result.getRoles(), hasItem("testRole"));
        assertThat(result.getRoles(), hasItem("patientRole"));
        assertTrue(
                result.getContextMap().containsKey(Oauth2AuthenticationFacade.USERINFO_PREFIX_KEY + "account_type"));
        assertEquals("user",
                result.getContextMap().get(Oauth2AuthenticationFacade.USERINFO_PREFIX_KEY + "account_type"));
    }

    private void configureWellKnown() {
        wellknown =
            "{\"token_endpoint\":\"http://localhost:" + mockWebServer.getPort() + "/token\", "
                    + "\"userinfo_endpoint\":\"http://localhost:" + mockWebServer.getPort() + "/userinfo\"}";
    }

    @Test
    void testRetrieveClientToken() throws IOException, InterruptedException {
        actualToken = CLIENT_TOKEN;

        configuration
                .put(DeprecatedOauth2ConfigurationKeys.OAUTH2_SERVER_REST_URI,
                        "http://localhost:" + mockWebServer.getPort());
        configuration.put(DeprecatedOauth2ConfigurationKeys.CLIENT_ID, "clientId");
        configuration.put(DeprecatedOauth2ConfigurationKeys.CLIENT_SECRET, "secret");
        configuration.fireEvent();

        var token = underTest.retrieveClientToken(null);
        assertNotNull(token);
        var request1 = mockWebServer.takeRequest();
        var contentTypeHeader = request1.getHeaders().values("Content-Type");
        assertFalse(contentTypeHeader.isEmpty());
        assertEquals(MediaType.APPLICATION_FORM_URLENCODED, contentTypeHeader.get(0));
    }

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                switch (request.getPath()) {
                    case "/.well-known/openid-configuration":
                        return new MockResponse().setResponseCode(HttpServletResponse.SC_OK)
                                .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                                .setBody(wellknown);
                    case "/userinfo":
                        if (null != actualUserInfo) {
                            return new MockResponse().setResponseCode(HttpServletResponse.SC_OK)
                                    .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                                    .setBody(FileLoaderUtility.toStringUnchecked(actualUserInfo));
                        }
                        return new MockResponse().setResponseCode(HttpServletResponse.SC_NOT_FOUND)
                                .addHeader("Content-Type", MediaType.APPLICATION_JSON);
                    case "/token":
                        return new MockResponse().setResponseCode(HttpServletResponse.SC_OK)
                                .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                                .setBody(FileLoaderUtility.toStringUnchecked(actualToken));
                    default:
                        return new MockResponse().setResponseCode(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        };
    }
}
