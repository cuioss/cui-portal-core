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

import static de.cuioss.tools.base.Preconditions.checkState;
import static de.cuioss.tools.string.MoreStrings.emptyToNull;
import static java.net.URLEncoder.encode;
import static java.util.Objects.requireNonNull;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.oauth.Oauth2AuthenticationFacade;
import de.cuioss.portal.authentication.oauth.Oauth2Configuration;
import de.cuioss.portal.authentication.oauth.Oauth2Service;
import de.cuioss.portal.authentication.oauth.Token;
import de.cuioss.portal.restclient.CuiRestClientBuilder;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.net.UrlParameter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Default implementation of {@link Oauth2Service}.
 *
 * @author Matthias Walliczek
 */
@ApplicationScoped
public class Oauth2ServiceImpl implements Serializable, Oauth2Service {

    private static final CuiLogger log = new CuiLogger(Oauth2ServiceImpl.class);

    @Serial
    private static final long serialVersionUID = 470127601291147747L;

    private static final String RETRIEVE_CLIENT_TOKEN_FAILED_MSG = "Portal-135: Retrieving client token failed";

    /**
     * The request to retrieve a user-specific token from the c2id-server after
     * being redirected from the login page to the application again.
     */
    public interface RequestToken extends Closeable {

        @POST
        @Produces(MediaType.APPLICATION_FORM_URLENCODED)
        Token requestToken(@FormParam("grant_type") String grantType, @FormParam("code") String code,
                @FormParam("state") String state, @FormParam("code_verifier") String codeVerifier,
                @FormParam("redirect_uri") String redirectUri);
    }

    public interface RequestRefreshToken extends Closeable {

        @POST
        @Produces(MediaType.APPLICATION_FORM_URLENCODED)
        Token requestToken(@FormParam("grant_type") String grantType, @FormParam("refresh_token") String refreshToken);
    }

    /**
     * The request to retrieve a client-application-specific token form the
     * c2d-server to be used for services without having an authenticated user
     * (registration, forget password, ...).
     */
    public interface RequestClientToken extends Closeable {

        @POST
        @Produces(MediaType.APPLICATION_FORM_URLENCODED)
        Token requestToken(@FormParam("grant_type") String grantType);
    }

    /**
     * The request to retrieve information about the current authenticated user.
     */
    public interface RequestUserInfo extends Closeable {

        @GET
        Map<String, Object> getUserInfo();
    }

    public static class AcceptJsonHeaderFilter implements ClientRequestFilter {

        @Override
        public void filter(ClientRequestContext requestContext) {
            requestContext.getHeaders().putSingle(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        }
    }

    @Inject
    private Provider<Oauth2Configuration> configurationProvider;

    @Override
    public AuthenticatedUserInfo createAuthenticatedUserInfo(final HttpServletRequest servletRequest,
            final UrlParameter code, final UrlParameter state, final String scopes, final String codeVerifier) {

        requireNonNull(servletRequest);
        requireNonNull(code);
        requireNonNull(state);
        requireNonNull(emptyToNull(scopes));

        var configuration = configurationProvider.get();
        Token token;

        final var builder = new CuiRestClientBuilder(log)
                .basicAuth(configuration.getClientId(), configuration.getClientSecret())
                .register(new AcceptJsonHeaderFilter());

        final String tokenUri = configuration.getTokenUri().trim();
        final String redirectUri = configuration.getExternalContextPath().trim() + servletRequest.getRequestURI();
        log.debug("creating auth user info with scopes='{}', tokenUri='{}', redirectUri='{}'",
                scopes, tokenUri, redirectUri);

        try (final RequestToken requestToken = builder.url(tokenUri).build(RequestToken.class)) {
            token = requestToken.requestToken(
                    "authorization_code",
                    code.getValue(),
                    state.getValue(),
                    codeVerifier,
                    redirectUri
            );
            log.trace("received token='{}' for scopes='{}', requestUri={}",
                    token, scopes, servletRequest.getRequestURI());
        } catch (IllegalArgumentException e) {
            log.warn("Portal-106: Retrieving request token failed", e);
            return null;
        } catch (WebApplicationException e) {
            log.warn("Portal-106: Retrieving request token failed", e);
            CuiRestClientBuilder.debugResponse(e.getResponse(), log);
            return null;
        } catch (IOException e) {
            log.error("Portal-540: IO Exception during request", e);
            return null;
        }

        return retrieveAuthenticatedUser(scopes, configuration, token, (int) (System.currentTimeMillis() / 1000L));
    }

    @Override
    public AuthenticatedUserInfo retrieveAuthenticatedUser(String scopes, Token token, int tokenTimestamp) {
        return retrieveAuthenticatedUser(scopes, configurationProvider.get(), token, tokenTimestamp);
    }

    private AuthenticatedUserInfo retrieveAuthenticatedUser(String scopes, Oauth2Configuration configuration,
            Token token, int tokenTimestamp) {

        final String userInfoUri = configuration.getUserInfoUri().trim();
        final CuiRestClientBuilder builder = new CuiRestClientBuilder(log)
                .register(new AcceptJsonHeaderFilter())
                .bearerAuthToken(token.getAccess_token());

        log.trace("retrieving userinfo for authenticated user. userInfoUri={}, access_token={}",
                userInfoUri, token.getAccess_token());

        try (RequestUserInfo client = builder.url(userInfoUri).build(RequestUserInfo.class)) {

            Map<String, Object> userInfo = client.getUserInfo();
            log.debug("successfully retrieved userinfo");
            log.trace("userinfo: {}", userInfo);

            var baseAuthenticatedUserInfoBuilder = BaseAuthenticatedUserInfo.builder().authenticated(true)
                    .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_SCOPES_KEY, scopes)
                    .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_KEY, token)
                    .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_TIMESTAMP_KEY, tokenTimestamp);

            for (Entry<String, Object> entry : userInfo.entrySet()) {
                if ("preferred_username".equals(entry.getKey())) {
                    baseAuthenticatedUserInfoBuilder.displayName(entry.getValue().toString());
                } else if ("sub".equals(entry.getKey())) {
                    baseAuthenticatedUserInfoBuilder.identifier(entry.getValue().toString());
                } else if ("email".equals(entry.getKey())) {
                    baseAuthenticatedUserInfoBuilder.contextMapElement(Oauth2AuthenticationFacade.EMAIL_KEY,
                            entry.getValue().toString());
                } else if (configuration.getRoleMapperClaims().contains(entry.getKey())) {
                    baseAuthenticatedUserInfoBuilder.roles(asStringList(entry.getValue()));
                } else {
                    // parse domain-specific user info entries as key / value information in the
                    // contextMap
                    baseAuthenticatedUserInfoBuilder.contextMapElement(
                            Oauth2AuthenticationFacade.USERINFO_PREFIX_KEY + entry.getKey(),
                            entry.getValue().toString());
                }
            }

            return baseAuthenticatedUserInfoBuilder.build();
        } catch (WebApplicationException e) {
            log.warn("Portal-107: Get userinfo failed", e);
            CuiRestClientBuilder.debugResponse(e.getResponse(), log);
            return null;
        } catch (Exception e) {
            log.warn("Portal-107: Get userinfo failed", e);
            return null;
        }
    }

    private static List<String> asStringList(Object value) {
        if (null == value) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        if (value instanceof Iterable<?> iterable) {
            iterable.forEach(item -> result.addAll(asStringList(item)));
        } else {
            result.add(value.toString());
        }
        return result;
    }

    @Override
    public String retrieveClientToken(String scopes) {
        var configuration = configurationProvider.get();
        checkState(null != configuration.getTokenUri(), "tokenUri must not be null");

        final var builder = new CuiRestClientBuilder(log)
                .basicAuth(configuration.getClientId(), configuration.getClientSecret())
                .register(new AcceptJsonHeaderFilter());

        try (var requestToken = builder.url(configuration.getTokenUri().trim()).build(RequestClientToken.class)) {

            var token = requestToken.requestToken("client_credentials");
            return token.getAccess_token();
        } catch (WebApplicationException e) {
            log.warn(e, RETRIEVE_CLIENT_TOKEN_FAILED_MSG);
            CuiRestClientBuilder.debugResponse(e.getResponse(), log);
            return null;
        } catch (Exception e) {
            log.warn(e, RETRIEVE_CLIENT_TOKEN_FAILED_MSG);
            return null;
        }
    }

    @Override
    public String refreshToken(OauthAuthenticatedUserInfo currentUser) {
        var configuration = configurationProvider.get();
        final var builder = new CuiRestClientBuilder(log)
                .basicAuth(configuration.getClientId(), configuration.getClientSecret())
                .register(new AcceptJsonHeaderFilter());

        try (var requestToken = builder.url(configuration.getTokenUri().trim()).build(RequestRefreshToken.class)) {

            var token = requestToken.requestToken("refresh_token", currentUser.getToken().getRefresh_token());
            if (null != token) {
                log.debug("successfully retrieved new token");
                log.trace("new token: {}", token);
                currentUser.getContextMap().put(OauthAuthenticatedUserInfo.TOKEN_KEY, token);
                currentUser.getContextMap().put(OauthAuthenticatedUserInfo.TOKEN_TIMESTAMP_KEY,
                        (int) (System.currentTimeMillis() / 1000L));
                return token.getAccess_token();
            }
            log.debug("no token received");
            return null;
        } catch (WebApplicationException e) {
            log.warn(e, RETRIEVE_CLIENT_TOKEN_FAILED_MSG);
            CuiRestClientBuilder.debugResponse(e.getResponse(), log);
            return null;
        } catch (Exception e) {
            log.warn(e, RETRIEVE_CLIENT_TOKEN_FAILED_MSG);
            return null;
        }
    }

    @Override
    public String calcEncodedRedirectUrl(final String url) {
        requireNonNull(url);
        return encode(configurationProvider.get().getExternalContextPath().trim() + url, StandardCharsets.UTF_8);
    }
}
