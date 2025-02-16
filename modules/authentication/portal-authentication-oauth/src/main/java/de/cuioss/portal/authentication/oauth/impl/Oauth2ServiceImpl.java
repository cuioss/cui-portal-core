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
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static de.cuioss.portal.authentication.oauth.PortalAuthenticationOauthLogMessages.DEBUG;
import static de.cuioss.portal.authentication.oauth.PortalAuthenticationOauthLogMessages.ERROR;
import static de.cuioss.portal.authentication.oauth.PortalAuthenticationOauthLogMessages.WARN;
import static de.cuioss.tools.base.Preconditions.checkState;
import static de.cuioss.tools.string.MoreStrings.emptyToNull;
import static java.net.URLEncoder.encode;
import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@link Oauth2Service}.
 *
 * @author Matthias Walliczek
 */
@ApplicationScoped
public class Oauth2ServiceImpl implements Oauth2Service {

    private static final CuiLogger LOGGER = new CuiLogger(Oauth2ServiceImpl.class);

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

        final var builder = new CuiRestClientBuilder(LOGGER)
                .basicAuth(configuration.getClientId(), configuration.getClientSecret())
                .register(new AcceptJsonHeaderFilter());

        final String tokenUri = configuration.getTokenUri().trim();
        final String redirectUri = configuration.getExternalContextPath().trim() + servletRequest.getRequestURI();
        LOGGER.debug(DEBUG.CREATING_AUTH_USER_INFO.format(scopes, tokenUri, redirectUri));

        try (final RequestToken requestToken = builder.url(tokenUri).build(RequestToken.class)) {
            token = requestToken.requestToken(
                    "authorization_code",
                    code.getValue(),
                    state.getValue(),
                    codeVerifier,
                    redirectUri
            );
            LOGGER.trace("received token='%s' for scopes='%s', requestUri=%s",
                    token, scopes, servletRequest.getRequestURI());
        } catch (IllegalArgumentException e) {
            LOGGER.warn(WARN.REQUEST_TOKEN_FAILED.format(), e);
            return null;
        } catch (WebApplicationException e) {
            LOGGER.warn(WARN.REQUEST_TOKEN_FAILED.format(), e);
            CuiRestClientBuilder.debugResponse(e.getResponse(), LOGGER);
            return null;
        } catch (IOException e) {
            LOGGER.error(ERROR.IO_EXCEPTION.format(), e);
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
        final CuiRestClientBuilder builder = new CuiRestClientBuilder(LOGGER)
                .register(new AcceptJsonHeaderFilter())
                .bearerAuthToken(token.getAccess_token());

        LOGGER.trace("retrieving userinfo for authenticated user. userInfoUri=%s, access_token=%s",
                userInfoUri, token.getAccess_token());

        try (RequestUserInfo client = builder.url(userInfoUri).build(RequestUserInfo.class)) {

            Map<String, Object> userInfo = client.getUserInfo();
            LOGGER.debug("successfully retrieved userinfo");
            LOGGER.trace("userinfo: %s", userInfo);

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
            LOGGER.warn(WARN.GET_USERINFO_FAILED.format(), e);
            CuiRestClientBuilder.debugResponse(e.getResponse(), LOGGER);
            return null;
        } catch (Exception e) {
            LOGGER.warn(WARN.GET_USERINFO_FAILED.format(), e);
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

        final var builder = new CuiRestClientBuilder(LOGGER)
                .basicAuth(configuration.getClientId(), configuration.getClientSecret())
                .register(new AcceptJsonHeaderFilter());

        try (var requestToken = builder.url(configuration.getTokenUri().trim()).build(RequestClientToken.class)) {

            var token = requestToken.requestToken("client_credentials");
            return token.getAccess_token();
        } catch (WebApplicationException e) {
            LOGGER.warn(WARN.CLIENT_TOKEN_FAILED.format(), e);
            CuiRestClientBuilder.debugResponse(e.getResponse(), LOGGER);
            return null;
        } catch (Exception e) {
            LOGGER.warn(WARN.CLIENT_TOKEN_FAILED.format(), e);
            return null;
        }
    }

    @Override
    public String refreshToken(OauthAuthenticatedUserInfo currentUser) {
        var configuration = configurationProvider.get();
        final var builder = new CuiRestClientBuilder(LOGGER)
                .basicAuth(configuration.getClientId(), configuration.getClientSecret())
                .register(new AcceptJsonHeaderFilter());

        try (var requestToken = builder.url(configuration.getTokenUri().trim()).build(RequestRefreshToken.class)) {

            var token = requestToken.requestToken("refresh_token", currentUser.getToken().getRefresh_token());
            if (null != token) {
                LOGGER.debug("successfully retrieved new token");
                LOGGER.trace("new token: %s", token);
                currentUser.getContextMap().put(OauthAuthenticatedUserInfo.TOKEN_KEY, token);
                currentUser.getContextMap().put(OauthAuthenticatedUserInfo.TOKEN_TIMESTAMP_KEY,
                        (int) (System.currentTimeMillis() / 1000L));
                return token.getAccess_token();
            }
            LOGGER.debug("no token received");
            return null;
        } catch (WebApplicationException e) {
            LOGGER.warn(e, WARN.CLIENT_TOKEN_FAILED.format());
            CuiRestClientBuilder.debugResponse(e.getResponse(), LOGGER);
            return null;
        } catch (Exception e) {
            LOGGER.warn(e, WARN.CLIENT_TOKEN_FAILED.format());
            return null;
        }
    }

    @Override
    public String calcEncodedRedirectUrl(final String url) {
        requireNonNull(url);
        return encode(configurationProvider.get().getExternalContextPath().trim() + url, StandardCharsets.UTF_8);
    }
}
