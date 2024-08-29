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
import de.cuioss.portal.authentication.oauth.Oauth2AuthenticationFacade;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.EndpointAnswerHandler;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.test.jsf.mocks.CuiMockHttpServletRequest;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import de.cuioss.tools.net.UrlParameter;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.Setter;
import mockwebserver3.MockWebServer;
import org.apache.myfaces.test.mock.MockHttpServletRequest;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static de.cuioss.test.generator.Generators.letterStrings;
import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
@EnablePortalConfiguration(configuration = "authentication.oidc.validation.enabled:false")
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
    private PortalTestConfiguration configuration;

    @Produces
    private MockHttpServletRequest servletRequest;

    @Getter
    private final OIDCWellKnownDispatcher dispatcher = new OIDCWellKnownDispatcher();

    @BeforeEach
    void beforeEach() {
        servletRequest = new CuiMockHttpServletRequest();
        servletRequest.setPathInfo("some.url");
        dispatcher.reset();
        dispatcher.configure(configuration, mockWebServer);
    }

    @Test
    void testCalcEncodedRedirectUrl() {
        // test default
        assertEquals("http%3A%2F%2Fpathtest", underTest.calcEncodedRedirectUrl("test"));
    }

    @Test
    void testCreateAuthenticatedUserInfoFailed() {
        dispatcher.setTokenResult(EndpointAnswerHandler.RESPONSE_NOT_FOUND);

        var result = underTest.createAuthenticatedUserInfo(servletRequest, new UrlParameter("code", "123"),
                new UrlParameter("state", "456"), "scopes", "code");
        assertNull(result);
    }

    @Test
    void testDeprecatedCreateAuthenticatedUserInfo() throws InterruptedException {

        var result = underTest.createAuthenticatedUserInfo(servletRequest, new UrlParameter("code", "123"),
                new UrlParameter("state", "456"), "scopes", "code");

        var requests = dispatcher.nonWellKnownRequests(mockWebServer);

        assertFalse(requests.isEmpty());

        var request = requests.get(1);
        var authHeader = request.getHeaders().values("Authorization");
        assertFalse(authHeader.isEmpty());
        assertEquals("Bearer SlAV32hkKG", authHeader.get(0));
        assertNotNull(result);
        assertTrue(result.getContextMap().containsKey(Oauth2AuthenticationFacade.USERINFO_PREFIX_KEY + "account_type"));
        assertEquals("user",
                result.getContextMap().get(Oauth2AuthenticationFacade.USERINFO_PREFIX_KEY + "account_type"));
    }

    @Test
    void testCreateAuthenticatedUserInfo() throws InterruptedException {

        configuration.update(OAuthConfigKeys.OPEN_ID_ROLE_MAPPER_CLAIM, "ehealth-suite-roles");

        var code = new BigInteger(260, new Random()).toString(32);

        var result = underTest.createAuthenticatedUserInfo(servletRequest, new UrlParameter("code", "123"),
                new UrlParameter("state", "456"), "scopes", code);

        var requests = dispatcher.nonWellKnownRequests(mockWebServer);

        assertFalse(requests.isEmpty());

        var request = requests.get(0);

        var contentTypeHeader = request.getHeaders().values("Content-Type");
        assertFalse(contentTypeHeader.isEmpty());
        assertEquals(MediaType.APPLICATION_FORM_URLENCODED, contentTypeHeader.get(0));
        var acceptHeader = request.getHeaders().values("Accept");
        assertFalse(acceptHeader.isEmpty());
        assertEquals(MediaType.APPLICATION_JSON, acceptHeader.get(0));
        assertEquals("grant_type=authorization_code&code=123&state=456&code_verifier=" + code
                + "&redirect_uri=http%3A%2F" + "%2Fpathsome.url", request.getBody().readUtf8());

        request = requests.get(1);
        assertNotNull(request);
        var authHeader = request.getHeaders().values("Authorization");
        assertFalse(authHeader.isEmpty());
        assertEquals("Bearer SlAV32hkKG", authHeader.get(0));
        assertNotNull(result);
        assertEquals("j.doe", result.getDisplayName());
        assertTrue(result.getRoles().contains("testRole"));
        assertTrue(result.getRoles().contains("patientRole"));
        assertTrue(result.getContextMap().containsKey(Oauth2AuthenticationFacade.USERINFO_PREFIX_KEY + "account_type"));
        assertEquals("user",
                result.getContextMap().get(Oauth2AuthenticationFacade.USERINFO_PREFIX_KEY + "account_type"));
    }

    @Test
    void testRetrieveClientToken() throws InterruptedException {

        var token = underTest.retrieveClientToken(null);
        assertNotNull(token);

        var requests = dispatcher.nonWellKnownRequests(mockWebServer);

        assertFalse(requests.isEmpty());

        var request = requests.get(0);

        var contentTypeHeader = request.getHeaders().values("Content-Type");
        assertFalse(contentTypeHeader.isEmpty());
        assertEquals(MediaType.APPLICATION_FORM_URLENCODED, contentTypeHeader.get(0));
    }

    @Test
    void shouldRefreshTokenHappyCase() throws InterruptedException {
        var user = setupAuthorizedUser();

        var refreshToken = letterStrings(4, 24).next();

        user.getToken().setRefresh_token(refreshToken);
        user.getToken().setAccess_token(null);

        var result = underTest.refreshToken(user);
        assertNotNull(result);

        assertNotNull(user.getToken().getAccess_token());

        var requests = dispatcher.nonWellKnownRequests(mockWebServer);
        assertFalse(requests.isEmpty());
        var request = requests.get(2);
        assertNotNull(request);
        var body = request.getBody().toString();
        assertTrue(body.contains("grant_type=refresh_token"));
        assertTrue(body.contains("refresh_token=" + refreshToken));
    }

    private OauthAuthenticatedUserInfo setupAuthorizedUser() {
        var code = new BigInteger(260, new Random()).toString(32);

        var initialUser = underTest.createAuthenticatedUserInfo(servletRequest, new UrlParameter("code", "123"),
                new UrlParameter("state", "456"), "scopes", code);
        return OauthAuthenticatedUserInfo.createOf(initialUser);
    }

    @Test
    void shouldHandleMissingRefreshToken() {
        var user = setupAuthorizedUser();
        user.getToken().setRefresh_token(null);
        user.getToken().setAccess_token(null);

        dispatcher.setTokenResult(EndpointAnswerHandler.RESPONSE_NOT_FOUND);

        var result = underTest.refreshToken(user);
        assertNull(result);

        assertNull(user.getToken().getAccess_token());
    }

    @Test
    void shouldRetrieveAuthenticatedUser() {
        var user = setupAuthorizedUser();
        var result = underTest.retrieveAuthenticatedUser("scoe", user.getToken(), 0);
        assertNotNull(result);
        assertTrue(user.isAuthenticated());
    }
}
