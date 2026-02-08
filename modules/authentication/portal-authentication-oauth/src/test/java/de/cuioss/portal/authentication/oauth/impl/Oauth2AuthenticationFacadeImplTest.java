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
package de.cuioss.portal.authentication.oauth.impl;

import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.oauth.LoginPagePath;
import de.cuioss.portal.authentication.oauth.OauthAuthenticationException;
import de.cuioss.portal.authentication.oauth.Token;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.test.jsf.mocks.CuiMockHttpServletRequest;
import de.cuioss.test.mockwebserver.EnableMockWebServer;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import de.cuioss.tools.collect.CollectionLiterals;
import de.cuioss.tools.net.UrlParameter;
import de.cuioss.tools.string.MoreStrings;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.Getter;
import mockwebserver3.MockWebServer;
import org.apache.myfaces.test.mock.MockHttpServletRequest;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static de.cuioss.test.generator.Generators.letterStrings;
import static de.cuioss.tools.collect.CollectionLiterals.mutableList;
import static org.junit.jupiter.api.Assertions.*;

@EnableMockWebServer
@EnableAutoWeld
@EnablePortalConfiguration(configuration = "authentication.oidc.validation.enabled:false")
@AddBeanClasses({Oauth2AuthenticationFacadeImpl.class, Oauth2DiscoveryConfigurationProducer.class,
        RedirectorMock.class})
@AddExtensions(ResteasyCdiExtension.class)
@ExplicitParamInjection
class Oauth2AuthenticationFacadeImplTest
        implements ShouldHandleObjectContracts<Oauth2AuthenticationFacadeImpl> {

    @Inject
    @PortalAuthenticationFacade
    @Getter
    private Oauth2AuthenticationFacadeImpl underTest;

    @Produces
    @LoginPagePath
    private final String loginUrl = "login.jsf";

    @Inject
    private PortalTestConfiguration configuration;

    @Inject
    private RedirectorMock redirectorMock;

    @Produces
    private MockHttpServletRequest servletRequest;

    @Produces
    private final Oauth2ServiceMock service = new Oauth2ServiceMock();

    @Getter
    private final OIDCWellKnownDispatcher dispatcher = new OIDCWellKnownDispatcher();

    @BeforeEach
    void beforeEach(MockWebServer mockWebServer) {
        servletRequest = new CuiMockHttpServletRequest();
        servletRequest.setPathInfo("some.url");
        dispatcher.configure(configuration, mockWebServer);
        mockWebServer.setDispatcher(dispatcher);
        service.reset();
    }

    @Test
    void loginWithoutParams() {
        var result = underTest.testLogin(Collections.emptyList(), "scope");
        assertFalse(result.isAuthenticated());
        assertFalse(underTest.retrieveCurrentAuthenticationContext(servletRequest).isAuthenticated());
    }

    @Test
    void loginWithParams() {
        underTest.sendRedirect("scope");
        dispatcher.assertAuthorizeURL(redirectorMock.getRedirectUrl());
        var result = underTest.testLogin(calculateUrlParameter(), "scope");
        assertTrue(result.isAuthenticated());
        assertTrue(underTest.retrieveCurrentAuthenticationContext(servletRequest).isAuthenticated());
    }

    @Test
    void loginWithErrorFails() {
        underTest.sendRedirect("scope");
        var stateParameter = getStateParameter();
        var urlParameter = new UrlParameter("error", "server_error");
        List<UrlParameter> parameterList = mutableList(stateParameter, urlParameter);
        var exception = assertThrows(OauthAuthenticationException.class,
                () -> underTest.testLogin(parameterList, "scope"));
        assertEquals("system.exception.oauth.login", exception.getMessage());

        var parameterList2 = mutableList(stateParameter, new UrlParameter("error", "access_denied"));

        exception = assertThrows(OauthAuthenticationException.class,
                () -> underTest.testLogin(parameterList2, "scope"));
        assertEquals("system.exception.oauth.consent", exception.getMessage());

    }

    private UrlParameter getStateParameter() {
        if (null != servletRequest.getSession().getAttribute("State")) {
            return new UrlParameter("state", (String) servletRequest.getSession().getAttribute("State"));
        }
        return new UrlParameter("state", "abc");
    }

    private List<UrlParameter> calculateUrlParameter() {
        return mutableList(new UrlParameter("code", "123"), getStateParameter());
    }

    @Test
    void retrieveTokenWithoutSession() {
        var result = underTest.retrieveToken("scope");
        assertTrue(MoreStrings.isEmpty(result));
    }

    @Test
    void retrieveTokenWithSameScope() {
        underTest.sendRedirect("scope");
        underTest.testLogin(calculateUrlParameter(), "scope");
        var result = underTest.retrieveToken("scope");
        assertFalse(MoreStrings.isEmpty(result));
    }

    @Test
    void invalidateToken() {
        underTest.sendRedirect("scope");
        underTest.testLogin(calculateUrlParameter(), "scope");
        var result = underTest.retrieveToken("scope");
        assertFalse(MoreStrings.isEmpty(result));
        underTest.invalidateToken();
        result = underTest.retrieveToken("scope");
        assertTrue(MoreStrings.isEmpty(result));
    }

    @Test
    void retrieveTokenWithInvalidExpiresIn() {
        underTest.sendRedirect("scope");
        var userInfo = underTest.testLogin(calculateUrlParameter(), "scope");
        ((Token) userInfo.getContextMap().get(OauthAuthenticatedUserInfo.TOKEN_KEY)).setExpires_in("abc");
        var result = underTest.retrieveToken("scope");
        assertNull(result);
    }

    @Test
    void retrieveTokenWithOldExpiresIn() {
        underTest.sendRedirect("scope");
        var userInfo = underTest.testLogin(calculateUrlParameter(), "scope");
        ((Token) userInfo.getContextMap().get(OauthAuthenticatedUserInfo.TOKEN_KEY)).setExpires_in("100");
        userInfo.getContextMap().put(OauthAuthenticatedUserInfo.TOKEN_TIMESTAMP_KEY,
                (int) (System.currentTimeMillis() / 1000L) - 200);
        var result = underTest.retrieveToken("scope");
        assertNull(result);
    }

    @Test
    void retrieveTokenWithValidExpiresIn() {
        underTest.sendRedirect("scope");
        var userInfo = underTest.testLogin(calculateUrlParameter(), "scope");
        ((Token) userInfo.getContextMap().get(OauthAuthenticatedUserInfo.TOKEN_KEY)).setExpires_in("1000");
        var result = underTest.retrieveToken("scope");
        assertFalse(MoreStrings.isEmpty(result));
    }

    @Test
    void retrieveTokenWithNewScope() {
        underTest.sendRedirect("scope");
        underTest.testLogin(calculateUrlParameter(), "scope");
        var result = underTest.retrieveToken("scope new");
        assertNull(result);
        dispatcher.assertAuthorizeURL(redirectorMock.getRedirectUrl());
        result = underTest.retrieveToken("scope new");
        assertTrue(MoreStrings.isEmpty(result));
    }

    @Test
    void logout() {
        underTest.sendRedirect("scope");
        var result = underTest.testLogin(calculateUrlParameter(), "scope");
        assertTrue(result.isAuthenticated());
        assertTrue(underTest.retrieveCurrentAuthenticationContext(servletRequest).isAuthenticated());
        underTest.logout(servletRequest);
        assertFalse(underTest.retrieveCurrentAuthenticationContext(servletRequest).isAuthenticated());
    }

    @Test
    void retrieveOauth2RedirectUrlWithPKCEChallenge() throws Exception {
        var url = underTest.retrieveOauth2RedirectUrl("scope", null);
        dispatcher.assertAuthorizeURL(url,
                "response_type=code&scope=scope&client_id=" + OIDCWellKnownDispatcher.CLIENT_ID + "&state=");
        assertNotNull(servletRequest.getSession().getAttribute("PKCE_CODE"));
        assertTrue(((String) servletRequest.getSession().getAttribute("PKCE_CODE")).length() >= 43, """
                PKCE Code is too\
                 short (minimum 43 characters\
                """);
        assertTrue(((String) servletRequest.getSession().getAttribute("PKCE_CODE")).length() <= 128, """
                PKCE Code is too\
                 long (maximum 128 characters\
                """);
        MessageDigest digest;
        digest = MessageDigest.getInstance("SHA-256");
        final var code_challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest.digest(
                ((String) servletRequest.getSession().getAttribute("PKCE_CODE")).getBytes(StandardCharsets.US_ASCII)));
        assertTrue(url.contains("&code_challenge=" + code_challenge));
        assertTrue(url.endsWith("&redirect_uri=nulllogin.jsf"));
        url = underTest.retrieveOauth2RedirectUrl("scope", "idtoken");

        dispatcher.assertAuthorizeURL(url,
                "response_type=code&scope=scope&client_id=" + OIDCWellKnownDispatcher.CLIENT_ID + "&state=",
                "&id_token_hint=idtoken&redirect_uri=nulllogin.jsf");
    }

    @Test
    void pkceChallenge() {
        var pattern = Pattern.compile(".*&code_challenge=(.+?)&.*");
        var url1 = underTest.retrieveOauth2RedirectUrl("scope", null);
        var match1 = pattern.matcher(url1);
        assertTrue(match1.matches());
        var url2 = underTest.retrieveOauth2RedirectUrl("scope", null);
        var match2 = pattern.matcher(url2);
        assertTrue(match2.matches());
        assertEquals(match1.group(1), match2.group(1));
        underTest.testLogin(calculateUrlParameter(), "scope");
        var url3 = underTest.retrieveOauth2RedirectUrl("scope", null);
        var match3 = pattern.matcher(url3);
        assertTrue(match3.matches());
        assertNotEquals(match1.group(1), match3.group(1));
    }

    @Test
    void retrieveOauth2RenewUrl() {
        underTest.sendRedirect("scope");
        var userInfo = underTest.testLogin(calculateUrlParameter(), "scope");
        ((Token) userInfo.getContextMap().get(OauthAuthenticatedUserInfo.TOKEN_KEY)).setExpires_in("100");
        assertNotNull(underTest.retrieveOauth2RenewUrl());
        assertTrue(underTest.retrieveOauth2RenewUrl().contains("prompt=none"), underTest.retrieveOauth2RenewUrl());
        assertNotNull(underTest.retrieveRenewInterval());
        userInfo.getContextMap().put(OauthAuthenticatedUserInfo.TOKEN_TIMESTAMP_KEY,
                (int) (System.currentTimeMillis() / 1000L) - 200);
        assertNotNull(underTest.retrieveOauth2RenewUrl());
        assertFalse(underTest.retrieveOauth2RenewUrl().endsWith("prompt=none"));
        assertEquals("-110", underTest.retrieveRenewInterval());
    }

    @Test
    void retrieveIdToken() {
        var token = new Token();
        token.setId_token(
                """
                        eyJraWQiOiJXMGZvIiwiYWxnIjoiUlMyNTYifQ\
                        .eyJzdWIiOiIwMjNlYmU3NC1mMzI4LTQ0NjUtYjJkMi1hOTNjOGMwMzU0MzAiLCJhdWQiOiI1eGtnZnBob2pxbTY1NWJqbnc2czJqcGFycSIsImFjciI6Imh0dHA6XC9cL2lkbWFuYWdlbWVudC5nb3ZcL25zXC9hc3N1cmFuY2VcL2xvYVwvMiIsImlzcyI6Imh0dHBzOlwvXC9yaS11eC1pbmJvdW5kLTAxLmNpLmRldi5pY3cuaW50XC9jMmlkLWZhY2FkZVwvYzJpZCIsImV4cCI6MTUwODMzMzIyOCwiaWF0IjoxNTA4MzMyMzI4LCJub25jZSI6InVuYXA0ZGtsMzl0MmdzNWJrbGMxMTdhM3FoIn0.kFcTjbTLcAbUmWWK1uo6vvl_vSC09UBa2IOF8HuQBqKUnIbLRf1vbe-WnDN1r2Eh_-NgNnPDr07eRRUjmq3wh6e-wU2IcIILry_tH6GFjAeTajuO4JKfIvEyrHI7NbUu3Wvn_iieT0dOIb0Ugoh7nMR1DdpEGsKP5nfcl9P6R8d9ewwlDLyxeGLqrQUmKlBAe2xTHr3t6FFsRcHh0gBbfEg3D0KCKjPMfDJfMf1qAYqUagFJ4fp40XXpqvlRYOkv_8gZVYxxQsAJphoDHiZl_iQLRHl-boM4ePBIMyLSAaO0ye2wN6WBaduFVhPpLpxmpBV_izujji5oaIhiNY3m7w\
                        """);
        var testUser = BaseAuthenticatedUserInfo.builder()
                .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_KEY, token).build();
        var result = underTest.retrieveIdToken(testUser);
        assertNotNull(result);
        assertNotNull(result.get("acr"));
    }

    @Test
    void retrieveClientLogoutUrlWithParams() {
        var token = new Token();
        token.setId_token("idtoken");
        var testUser = BaseAuthenticatedUserInfo.builder()
                .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_KEY, token).build();
        servletRequest.getSession().setAttribute("AuthenticatedUserInfo", testUser);

        var logoutUrl = assertDoesNotThrow(() -> underTest.retrieveClientLogoutUrl(
                CollectionLiterals.immutableSet(new UrlParameter("foo", "bar"), new UrlParameter("baz", "buz"))));

        dispatcher.assertLogoutURL(logoutUrl, "foo=bar", "baz=buz", "id_token_hint=idtoken");
    }

    @Test
    void retrieveClientLogoutUrlWithNoParams() {

        var logoutUrl = assertDoesNotThrow(() -> underTest.retrieveClientLogoutUrl(null));

        dispatcher.assertLogoutURL(logoutUrl);
    }

    @Test
    void shouldResolveLoginURL() {
        assertNotNull(underTest.getLoginUrl());
    }

    @Test
    void shouldRefreshUserInfo() {
        underTest.sendRedirect("scope");
        var result = underTest.testLogin(calculateUrlParameter(), "scope");
        assertTrue(result.isAuthenticated());
        assertNotNull(underTest.refreshUserinfo());
    }

    @Test
    void shouldRetrieveToken() {
        underTest.sendRedirect("scope");
        var result = underTest.testLogin(calculateUrlParameter(), "scope");
        assertTrue(result.isAuthenticated());
        assertNotNull(underTest.retrieveToken(result, "scope"));
    }

    @Test
    void shouldRetrieveClientToken() {
        var clientToken = letterStrings(10, 12).next();
        service.setClientToken(clientToken);
        underTest.sendRedirect("scope");
        var result = underTest.testLogin(calculateUrlParameter(), "scope");
        assertTrue(result.isAuthenticated());
        assertEquals(clientToken, underTest.retrieveClientToken("scope"));
    }

    @Test
    void loginWithInvalidScopeError() {
        underTest.sendRedirect("scope");
        var stateParameter = getStateParameter();
        var errorParameter = new UrlParameter("error", "invalid_scope");
        List<UrlParameter> parameterList = mutableList(stateParameter, errorParameter);

        var exception = assertThrows(OauthAuthenticationException.class,
                () -> underTest.testLogin(parameterList, "scope"));
        assertEquals("system.exception.oauth.invalidScope", exception.getMessage());
    }

    @Test
    void loginWithStateMismatch() {
        underTest.sendRedirect("scope");
        // Use a different state value than what's stored in the session
        var wrongState = new UrlParameter("state", "wrong-state-value");
        var code = new UrlParameter("code", "123");
        List<UrlParameter> parameterList = mutableList(code, wrongState);

        // State mismatch results in null sessionUser, which triggers OauthAuthenticationException
        // when createAuthenticatedUserInfo returns null. But with a mock that always returns
        // non-null, the code proceeds. The actual behavior: sessionUser is set to null
        // and the code continues. The service mock creates a valid user, so login succeeds
        // but with a new session.
        var result = underTest.testLogin(parameterList, "scope");
        assertTrue(result.isAuthenticated());
    }

    @Test
    void loginWithNullSessionState() {
        // Don't call sendRedirect so no state is stored in session
        var stateParameter = new UrlParameter("state", "some-state");
        var code = new UrlParameter("code", "123");
        List<UrlParameter> parameterList = mutableList(code, stateParameter);

        var result = underTest.testLogin(parameterList, "scope");
        assertFalse(result.isAuthenticated());
    }

    @Test
    void retrieveOauth2RenewUrlWithNoUser() {
        assertNull(underTest.retrieveOauth2RenewUrl());
    }

    @Test
    void retrieveOauth2RenewUrlWithEmptyScopes() {
        underTest.sendRedirect("scope");
        var userInfo = underTest.testLogin(calculateUrlParameter(), "scope");
        // Remove scopes from session and user context
        servletRequest.getSession().removeAttribute("Scopes");
        userInfo.getContextMap().put(OauthAuthenticatedUserInfo.TOKEN_SCOPES_KEY, "");

        assertNull(underTest.retrieveOauth2RenewUrl());
    }

    @Test
    void retrieveRenewIntervalWithNoUser() {
        assertNull(underTest.retrieveRenewInterval());
    }

    @Test
    void retrieveIdTokenWithNullToken() {
        var testUser = BaseAuthenticatedUserInfo.builder().authenticated(true).build();
        var result = underTest.retrieveIdToken(testUser);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void retrieveIdTokenWithEmptyIdToken() {
        var token = new Token();
        token.setId_token("");
        var testUser = BaseAuthenticatedUserInfo.builder()
                .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_KEY, token).build();
        var result = underTest.retrieveIdToken(testUser);
        assertTrue(result.isEmpty());
    }

    @Test
    void retrieveIdTokenWithInvalidFormat() {
        var token = new Token();
        token.setId_token("only.twoparts");
        var testUser = BaseAuthenticatedUserInfo.builder()
                .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_KEY, token).build();
        var result = underTest.retrieveIdToken(testUser);
        assertTrue(result.isEmpty());
    }

    @Test
    void retrieveIdTokenWithInvalidBase64() {
        var token = new Token();
        // Use valid base64 that doesn't decode to valid JSON
        token.setId_token("header." + Base64.getEncoder().encodeToString("not-json".getBytes(StandardCharsets.UTF_8)) + ".signature");
        var testUser = BaseAuthenticatedUserInfo.builder()
                .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_KEY, token).build();
        var result = underTest.retrieveIdToken(testUser);
        assertTrue(result.isEmpty());
    }

    @Test
    void retrieveClientLogoutUrlWithEmptyParams() {
        // Verify logout URL is generated even with empty params and no user in session
        var logoutUrl = assertDoesNotThrow(() -> underTest.retrieveClientLogoutUrl(Collections.emptySet()));
        assertNotNull(logoutUrl);
        dispatcher.assertLogoutURL(logoutUrl);
    }

    @Test
    void logoutWithNullSession() {
        // new request has no session yet
        var freshRequest = new CuiMockHttpServletRequest();
        assertTrue(underTest.logout(freshRequest));
    }

    @Test
    void retrieveTokenWithRefreshToken() {
        underTest.sendRedirect("scope");
        var userInfo = underTest.testLogin(calculateUrlParameter(), "scope");
        // Expire the token
        ((Token) userInfo.getContextMap().get(OauthAuthenticatedUserInfo.TOKEN_KEY)).setExpires_in("100");
        userInfo.getContextMap().put(OauthAuthenticatedUserInfo.TOKEN_TIMESTAMP_KEY,
                (int) (System.currentTimeMillis() / 1000L) - 200);
        // Set refresh token
        ((Token) userInfo.getContextMap().get(OauthAuthenticatedUserInfo.TOKEN_KEY)).setRefresh_token("refresh-token-value");
        service.setRefreshToken("new-access-token");

        var result = underTest.retrieveToken("scope");
        assertEquals("new-access-token", result);
    }

    @Test
    void sendRedirectNoArgs() {
        underTest.sendRedirect("scope");
        // Store scopes in session via sendRedirect above, now call no-arg version
        assertDoesNotThrow(() -> underTest.sendRedirect());
    }

    @Test
    void getIdTokenFromCurrentUserWithNoUser() {
        // No user in session, test logout URL path that tries to get id token
        var logoutUrl = assertDoesNotThrow(() -> underTest.retrieveClientLogoutUrl(Collections.emptySet()));
        assertNotNull(logoutUrl);
    }

    @Test
    void shouldRetrieveTokenWithExternalUser() {
        underTest.sendRedirect("scope");
        var userInfo = underTest.testLogin(calculateUrlParameter(), "scope");
        assertTrue(userInfo.isAuthenticated());
        // Use the overloaded retrieveToken that takes an AuthenticatedUserInfo
        var token = underTest.retrieveToken(userInfo, "scope");
        assertNotNull(token);
    }

    @Test
    void shouldHandleRefreshUserInfoWithNoUser() {
        assertNull(underTest.refreshUserinfo());
    }
}
