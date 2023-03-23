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
import javax.ws.rs.core.MediaType;

import org.apache.deltaspike.core.api.common.DeltaSpike;
import org.apache.myfaces.test.mock.MockHttpServletRequest;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.authentication.oauth.Oauth2AuthenticationFacade;
import de.cuioss.portal.configuration.OAuthConfigKeys;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.portal.core.test.mocks.PortalTestConfiguration;
import de.cuioss.test.jsf.mocks.CuiMockHttpServletRequest;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import de.cuioss.tools.net.UrlParameter;
import lombok.Getter;
import lombok.Setter;
import okhttp3.mockwebserver.MockWebServer;

@EnableAutoWeld
@EnablePortalConfiguration
@EnableMockWebServer
@AddBeanClasses({ Oauth2DiscoveryConfigurationProducer.class })
@AddExtensions(ResteasyCdiExtension.class)
class Oauth2ServiceImplTest implements ShouldHandleObjectContracts<Oauth2ServiceImpl>, MockWebServerHolder {

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

    @Getter
    private OIDCWellKnownDispatcher dispatcher = new OIDCWellKnownDispatcher();

    @BeforeEach
    void beforeEach() {
        servletRequest = new CuiMockHttpServletRequest();
        servletRequest.setPathInfo("some.url");
        dispatcher.configure(configuration, mockWebServer);
        dispatcher.reset();
    }

    @Test
    void testCalcEncodedRedirectUrl() {
        // test default
        assertEquals("http%3A%2F%2Fpathtest", underTest.calcEncodedRedirectUrl("test"));
    }

    @Test
    void testCreateAuthenticatedUserInfoFailed() {
        dispatcher.setActualUserInfo(null);

        var result =
            underTest.createAuthenticatedUserInfo(servletRequest, new UrlParameter("code", "123"),
                    new UrlParameter("state", "456"), "scopes", "code");
        assertNull(result);
    }

    @Test
    void testDeprecatedCreateAuthenticatedUserInfo() throws IOException, InterruptedException {

        var result =
            underTest.createAuthenticatedUserInfo(servletRequest, new UrlParameter("code", "123"),
                    new UrlParameter("state", "456"), "scopes", "code");

        var requests = dispatcher.nonWellKnownRequests(mockWebServer);

        assertFalse(requests.isEmpty());

        var request = requests.get(1);
        var authHeader = request.getHeaders().values("Authorization");
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

        configuration.put(OAuthConfigKeys.OPEN_ID_ROLE_MAPPER_CLAIM, "ehealth-suite-roles");
        configuration.fireEvent();

        var code = new BigInteger(260, new Random()).toString(32);

        var result = underTest.createAuthenticatedUserInfo(
                servletRequest,
                new UrlParameter("code", "123"),
                new UrlParameter("state", "456"),
                "scopes",
                code);

        var requests = dispatcher.nonWellKnownRequests(mockWebServer);

        assertFalse(requests.isEmpty());

        var request = requests.get(0);

        var contentTypeHeader = request.getHeaders().values("Content-Type");
        assertFalse(contentTypeHeader.isEmpty());
        assertEquals(MediaType.APPLICATION_FORM_URLENCODED, contentTypeHeader.get(0));
        var acceptHeader = request.getHeaders().values("Accept");
        assertFalse(acceptHeader.isEmpty());
        assertEquals(MediaType.APPLICATION_JSON, acceptHeader.get(0));
        assertEquals(
                "grant_type=authorization_code&code=123&state=456&code_verifier=" + code + "&redirect_uri=http%3A%2F"
                        + "%2Fpathsome.url",
                request.getBody().readUtf8());

        request = requests.get(1);
        assertNotNull(request);
        var authHeader = request.getHeaders().values("Authorization");
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

    @Test
    void testRetrieveClientToken() throws IOException, InterruptedException {
        dispatcher.setActualToken(OIDCWellKnownDispatcher.CLIENT_TOKEN);

        var token = underTest.retrieveClientToken(null);
        assertNotNull(token);

        var requests = dispatcher.nonWellKnownRequests(mockWebServer);

        assertFalse(requests.isEmpty());

        var request = requests.get(0);

        var contentTypeHeader = request.getHeaders().values("Content-Type");
        assertFalse(contentTypeHeader.isEmpty());
        assertEquals(MediaType.APPLICATION_FORM_URLENCODED, contentTypeHeader.get(0));
    }

}
