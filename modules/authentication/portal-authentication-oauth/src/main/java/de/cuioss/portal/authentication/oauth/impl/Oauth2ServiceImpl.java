package de.cuioss.portal.authentication.oauth.impl;

import static de.cuioss.tools.string.MoreStrings.emptyToNull;
import static java.net.URLEncoder.encode;
import static java.util.Objects.requireNonNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.oauth.Oauth2AuthenticationFacade;
import de.cuioss.portal.authentication.oauth.Oauth2Configuration;
import de.cuioss.portal.authentication.oauth.Oauth2Service;
import de.cuioss.portal.authentication.oauth.Token;
import de.cuioss.portal.restclient.CuiRestClientBuilder;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.net.UrlParameter;

/**
 * Default implementation of {@link Oauth2Service}.
 *
 * @author Matthias Walliczek
 */
@ApplicationScoped
public class Oauth2ServiceImpl implements Serializable, Oauth2Service {

    private static final CuiLogger log = new CuiLogger(Oauth2ServiceImpl.class);

    private static final long serialVersionUID = 470127601291147747L;

    private static final String RETRIEVE_CLIENT_TOKEN_FAILED_MSG = "Portal-135: Retrieving client token failed";

    /**
     * The request to retrieve a user specific token from the c2id-server after being redirected
     * from the login page to
     * the application again.
     */
    public interface RequestToken extends Closeable {

        @POST
        @Produces(MediaType.APPLICATION_FORM_URLENCODED)
        Token requestToken(
                @FormParam("grant_type") String grant_type,
                @FormParam("code") String code,
                @FormParam("state") String state,
                @FormParam("code_verifier") String code_verifier,
                @FormParam("redirect_uri") String redirect_uri);
    }

    public interface RequestRefreshToken extends Closeable {

        @POST
        @Produces(MediaType.APPLICATION_FORM_URLENCODED)
        Token requestToken(
                @FormParam("grant_type") String grant_type,
                @FormParam("refresh_token") String refresh_token);
    }

    /**
     * The request to retrieve a client application specific token form the c2d-server to be used
     * for services without
     * having an authenticated user (registration, forget password e.g.).
     */
    public interface RequestClientToken extends Closeable {

        @POST
        @Produces(MediaType.APPLICATION_FORM_URLENCODED)
        Token requestToken(@FormParam("grant_type") String grant_type);
    }

    /**
     * The request to retrieve information about the current authenticated user.
     */
    public interface RequestUserInfo extends Closeable {

        @GET
        Map<String, Object> getUserInfo();
    }

    public class AcceptJsonHeaderFilter implements ClientRequestFilter {

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
        try (final var requestToken = builder.url(configuration.getTokenUri().trim()).build(RequestToken.class)) {
            token = requestToken.requestToken("authorization_code", code.getValue(), state.getValue(), codeVerifier,
                    configuration.getExternalContextPath().trim() + servletRequest.getRequestURI());
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
        final var builder =
            new CuiRestClientBuilder(log).register(new AcceptJsonHeaderFilter());

        try (var client = builder.url(configuration.getUserInfoUri().trim()).bearerAuthToken(token.getAccess_token())
                .build(RequestUserInfo.class)) {
            var userInfo = client.getUserInfo();

            var baseAuthenticatedUserInfoBuilder =
                BaseAuthenticatedUserInfo.builder().authenticated(true)
                        .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_SCOPES_KEY, scopes)
                        .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_KEY, token)
                        .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_TIMESTAMP_KEY,
                                tokenTimestamp);

            for (Entry<String, Object> entry : userInfo.entrySet()) {
                if ("preferred_username".equals(entry.getKey())) {
                    baseAuthenticatedUserInfoBuilder.displayName(entry.getValue().toString());
                } else if ("sub".equals(entry.getKey())) {
                    baseAuthenticatedUserInfoBuilder.identifier(entry.getValue().toString());
                } else if ("email".equals(entry.getKey())) {
                    baseAuthenticatedUserInfoBuilder
                            .contextMapElement(Oauth2AuthenticationFacade.EMAIL_KEY, entry.getValue().toString());
                } else if (configuration.getRoleMapperClaims().contains(entry.getKey())) {
                    baseAuthenticatedUserInfoBuilder.roles(asStringList(entry.getValue()));
                } else {
                    // parse domain specific user info entries as key / value information in the
                    // contextMap
                    baseAuthenticatedUserInfoBuilder
                            .contextMapElement(Oauth2AuthenticationFacade.USERINFO_PREFIX_KEY + entry.getKey(),
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
        if (value instanceof Iterable) {
            ((Iterable<?>) value).forEach(item -> result.addAll(asStringList(item)));
        } else {
            result.add(value.toString());
        }
        return result;
    }

    @Override
    public String retrieveClientToken(String scopes) {
        var configuration = configurationProvider.get();
        final var builder = new CuiRestClientBuilder(log).basicAuth(configuration.getClientId(),
                configuration.getClientSecret())
                .register(new AcceptJsonHeaderFilter());

        try (var requestToken = builder.url(configuration.getTokenUri().trim())
                .build(RequestClientToken.class)) {

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
        final var builder = new CuiRestClientBuilder(log).basicAuth(configuration.getClientId(),
                configuration.getClientSecret())
                .register(new AcceptJsonHeaderFilter());

        try (var requestToken = builder.url(configuration.getTokenUri().trim())
                .build(RequestRefreshToken.class)) {

            var token = requestToken.requestToken("refresh_token", currentUser.getToken().getRefresh_token());
            if (null != token) {
                currentUser.getContextMap().put(OauthAuthenticatedUserInfo.TOKEN_KEY, token);
                currentUser.getContextMap().put(OauthAuthenticatedUserInfo.TOKEN_TIMESTAMP_KEY,
                        (int) (System.currentTimeMillis() / 1000L));
                return token.getAccess_token();
            }
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
        try {
            return encode(configurationProvider.get().getExternalContextPath().trim() + url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }

    }
}
