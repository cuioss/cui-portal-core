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

import static de.cuioss.tools.string.MoreStrings.emptyToNull;
import static java.net.URLEncoder.encode;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.facade.AuthenticationSource;
import de.cuioss.portal.authentication.facade.BaseAuthenticationFacade;
import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.oauth.LoginPagePath;
import de.cuioss.portal.authentication.oauth.Oauth2AuthenticationFacade;
import de.cuioss.portal.authentication.oauth.Oauth2Configuration;
import de.cuioss.portal.authentication.oauth.Oauth2Service;
import de.cuioss.portal.authentication.oauth.OauthAuthenticationException;
import de.cuioss.portal.authentication.oauth.OauthRedirector;
import de.cuioss.portal.authentication.oauth.OidcRpInitiatedLogoutParams;
import de.cuioss.portal.authentication.oauth.Token;
import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.net.UrlParameter;
import de.cuioss.tools.string.MoreStrings;
import de.cuioss.tools.string.Splitter;

/**
 * Default implementation of {@link Oauth2AuthenticationFacade}. Uses
 * {@link Oauth2Service} to access the oauth2 rest interface.
 * <p>
 * Stores the requested and confirmed scopes with key SCOPES_KEY in the current
 * session.
 * <p>
 * Other internal values like STATE_KEY, NONCE_KEY and PKCE_CODE_KEY are also
 * stored in the current session.
 *
 * @author Matthias Walliczek
 */
@PortalAuthenticationFacade
@ApplicationScoped
public class Oauth2AuthenticationFacadeImpl extends BaseAuthenticationFacade
        implements Serializable, Oauth2AuthenticationFacade {

    private static final CuiLogger LOGGER = new CuiLogger(Oauth2AuthenticationFacadeImpl.class);

    private static final String ERROR_ACCESS_DENIED = "access_denied";

    private static final String ERROR_INVALID_SCOPE = "invalid_scope";

    @Serial
    private static final long serialVersionUID = -7635870199193359039L;

    private static final String AUTHENTICATED_USER_INFO_KEY = "AuthenticatedUserInfo";
    private static final String STATE_KEY = "State";
    private static final String NONCE_KEY = "Nonce";
    private static final String SCOPES_KEY = "Scopes";
    private static final String PKCE_CODE_KEY = "PKCE_CODE";

    private final transient Object codeLock = new Object();

    private static final AuthenticatedUserInfo NOT_LOGGED_IN = BaseAuthenticatedUserInfo.builder().authenticated(false)
            .build();

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    private Oauth2Service oauth2ServiceImpl;

    @Inject
    private Provider<Oauth2Configuration> configurationProvider;

    @Inject
    @LoginPagePath
    private Provider<String> loginUrl;

    @Inject
    private Provider<OauthRedirector> oauthRedirector;

    @Inject
    private Provider<HttpServletRequest> servletRequestProvider;

    private final SecureRandom random = new SecureRandom();

    @Override
    public boolean logout(final HttpServletRequest servletRequest) {
        final var currentSession = servletRequest.getSession(false);
        if (null != currentSession) {
            currentSession.invalidate();
        }
        return true;
    }

    @Override
    public AuthenticatedUserInfo retrieveCurrentAuthenticationContext(final HttpServletRequest servletRequest) {
        final var currentUser = retrieveCurrentUserIfPresent(servletRequest);
        return currentUser.orElse(new OauthAuthenticatedUserInfo(NOT_LOGGED_IN));
    }

    @Override
    public AuthenticatedUserInfo testLogin(final List<UrlParameter> parameters, final String scopes) {
        final var result = triggerAuthenticate(parameters, scopes);
        return result.orElse(NOT_LOGGED_IN);
    }

    @Override
    public void invalidateToken() {
        final var currentUser = retrieveCurrentUserIfPresent(servletRequestProvider.get());
        currentUser
                .ifPresent(oauthAuthenticatedUserInfo -> oauthAuthenticatedUserInfo.getToken().setAccess_token(null));
    }

    @Override
    public void sendRedirect(final String scopes) {
        requireNonNull(emptyToNull(scopes));
        sendRedirect(scopes, null);
    }

    @Override
    public void sendRedirect() {
        sendRedirect((String) servletRequestProvider.get().getSession().getAttribute(SCOPES_KEY), null);
    }

    private void sendRedirect(final String scopes, final String idToken) {
        try {
            var retrieveUrl = retrieveOauth2RedirectUrl(scopes, idToken);
            LOGGER.debug("Calling redirect to %s", retrieveUrl);
            oauthRedirector.get().sendRedirect(retrieveUrl);
        } catch (final IllegalStateException e) {
            LOGGER.warn("Portal-146: Oauth2 sendRedirect failed", e);
        }
    }

    private Optional<AuthenticatedUserInfo> triggerAuthenticate(final List<UrlParameter> parameters,
            final String scopes) {
        final var code = parameters.stream().filter(parameter -> "code".equals(parameter.getName())).findAny();
        final var state = parameters.stream().filter(parameter -> "state".equals(parameter.getName())).findAny();
        final var error = parameters.stream().filter(parameter -> "error".equals(parameter.getName())).findAny();
        if (state.isPresent()) {
            if (code.isPresent()) {
                return handleTriggerAuthenticate(scopes, code.get(), state.get());
            }
            if (error.isPresent()) {
                LOGGER.debug("state and error {} parameter are present", error.get().getValue());
                if (ERROR_ACCESS_DENIED.equals(error.get().getValue())) {
                    throw new OauthAuthenticationException("system.exception.oauth.consent");
                }
                if (ERROR_INVALID_SCOPE.equals(error.get().getValue())) {
                    LOGGER.warn(ERROR_INVALID_SCOPE + ": {}", parameters);
                    throw new OauthAuthenticationException("system.exception.oauth.invalidScope");
                }
                LOGGER.warn("Portal-147: Oauth2 login error: {}", parameters);
                throw new OauthAuthenticationException("system.exception.oauth.login");
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("squid:S3655") // already checked
    private Optional<AuthenticatedUserInfo> handleTriggerAuthenticate(final String scopes, final UrlParameter code,
            final UrlParameter state) {
        final var servletRequest = servletRequestProvider.get();
        LOGGER.debug("code and state parameter are present");
        final AuthenticatedUserInfo sessionUser;
        synchronized (codeLock) {
            if (null == servletRequest.getSession().getAttribute(STATE_KEY)) {
                LOGGER.warn("Portal-148: Oauth2 unexpected login call with unknown state {}, redirecting to login",
                        state.getValue());
                return Optional.empty();
            }
            if (state.getValue().equals(servletRequest.getSession().getAttribute(STATE_KEY))) {
                LOGGER.debug("state parameter matches stored value");
                sessionUser = (AuthenticatedUserInfo) servletRequest.getSession()
                        .getAttribute(AUTHENTICATED_USER_INFO_KEY);
            } else {
                LOGGER.debug("state parameter {} differs from stored value {}", state.getValue(),
                        servletRequest.getSession().getAttribute(STATE_KEY));
                sessionUser = null;
            }
            servletRequest.getSession().removeAttribute(STATE_KEY);
        }
        var retrievedScoped = (String) servletRequest.getSession().getAttribute(SCOPES_KEY);
        if (null == retrievedScoped) {
            retrievedScoped = scopes;
        }
        String codeVerifier;
        synchronized (codeLock) {
            codeVerifier = (String) servletRequest.getSession().getAttribute(PKCE_CODE_KEY);
            servletRequest.getSession().removeAttribute(PKCE_CODE_KEY);
        }
        LOGGER.trace("handleTriggerAuthenticate codeVerifier: {}", codeVerifier);
        var oauthUser = oauth2ServiceImpl.createAuthenticatedUserInfo(servletRequest, code, state, retrievedScoped,
                codeVerifier);

        if (null != oauthUser) {
            LOGGER.debug("authenticated oauth user info was retrieved: {}", oauthUser);
            if (null == sessionUser || !sessionUser.isAuthenticated()) {
                LOGGER.debug(
                        "session user missing or not authenticated. invalidating session! (change session after login)");
                servletRequest.getSession().invalidate();
            }
            oauthUser = enrich(oauthUser);
            LOGGER.debug("adding oauth user to (new) session.");
            servletRequest.getSession().setAttribute(AUTHENTICATED_USER_INFO_KEY, oauthUser);
            return Optional.of(oauthUser);
        }
        LOGGER.debug("unable to retrieve authenticated user info");
        throw new OauthAuthenticationException("system.exception.oauth.login");
    }

    @Override
    public String retrieveOauth2RedirectUrl(final String scopes, final String idToken) {
        final var servletRequest = servletRequestProvider.get();
        requireNonNull(emptyToNull(scopes));
        synchronized (codeLock) {
            if (null == servletRequest.getSession().getAttribute(STATE_KEY)) {
                servletRequest.getSession().setAttribute(STATE_KEY, new BigInteger(130, random).toString(32));
            }
        }
        final var state = (String) servletRequest.getSession().getAttribute(STATE_KEY);
        final var nonce = new BigInteger(130, random).toString(32);
        servletRequest.getSession().setAttribute(NONCE_KEY, nonce);
        servletRequest.getSession().setAttribute(SCOPES_KEY, scopes);

        synchronized (codeLock) {
            if (null == servletRequest.getSession().getAttribute(PKCE_CODE_KEY)) {
                servletRequest.getSession().setAttribute(PKCE_CODE_KEY, new BigInteger(260, random).toString(32));
                LOGGER.trace("retrieveOauth2RedirectUrl ---- NEW CODE");
            }
        }

        var code = (String) servletRequest.getSession().getAttribute(PKCE_CODE_KEY);
        LOGGER.trace("retrieveOauth2RedirectUrl code: {}", code);
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e, "Cannot generate code_challenge");
            throw new IllegalStateException(e);
        }

        final var code_challenge = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(digest.digest(code.getBytes(StandardCharsets.US_ASCII)));
        LOGGER.trace("retrieveOauth2RedirectUrl code_challenge: {}", code_challenge);

        final var scopesParameter = encode(scopes, StandardCharsets.UTF_8);
        final var configuration = configurationProvider.get();

        final var url = configuration.getAuthorizeUri().trim() + UrlParameter.createParameterString(false,
                new UrlParameter("response_type", "code", false), new UrlParameter("scope", scopesParameter, false),
                new UrlParameter("client_id", configuration.getClientId().trim(), false),
                new UrlParameter("state", state, false), new UrlParameter("nonce", nonce, false),
                new UrlParameter("code_challenge", code_challenge, false),
                new UrlParameter("code_challenge_method", "S256", false),
                new UrlParameter("id_token_hint", idToken, false),
                new UrlParameter("redirect_uri",
                        oauth2ServiceImpl.calcEncodedRedirectUrl(servletRequest.getContextPath() + loginUrl.get()),
                        false));

        LOGGER.debug("redirect url = {}", url);
        return url;
    }

    @Override
    public String retrieveOauth2RenewUrl() {
        final var servletRequest = servletRequestProvider.get();
        final var currentUser = retrieveCurrentUserIfPresent(servletRequest);
        if (currentUser.isPresent()) {
            final var token = currentUser.get().getToken();
            var scopes = (String) servletRequest.getSession().getAttribute(SCOPES_KEY);
            if (MoreStrings.isEmpty(scopes)) {
                scopes = currentUser.get().getScopes();
            }
            if (MoreStrings.isEmpty(scopes)) {
                return null;
            }
            if (token != null && checkToken(token, currentUser.get().getTokenTimestamp())) {
                return retrieveOauth2RedirectUrl(scopes, token.getId_token()) + "&prompt=none&response_mode=cors";
            }
            return retrieveOauth2RedirectUrl(scopes, null);
        }
        return null;
    }

    @Override
    public String retrieveToken(final String scopes) {
        requireNonNull(emptyToNull(loginUrl.get()));
        requireNonNull(emptyToNull(scopes));
        LOGGER.trace("retrieveToken for scopes: {}", scopes);
        final var currentUser = retrieveCurrentUserIfPresent(servletRequestProvider.get());
        String idToken = null;

        if (currentUser.isPresent()) {
            LOGGER.debug("we have a user");

            final var accessToken = checkAndRetrieveToken(currentUser.get(), scopes);
            if (null != accessToken) {
                LOGGER.debug("accessToken present. returning accessToken.");
                return accessToken;
            }

            final var token = currentUser.get().getToken();
            if (token != null) {
                LOGGER.debug("CUI Token present. extracting idToken.");
                idToken = token.getId_token();
            } else {
                LOGGER.debug("No CUI Token available. Cannot set idToken.");
            }
        }

        LOGGER.debug("accessToken not present, redirecting to oauth server using idToken={}.", null != idToken);
        LOGGER.trace("using idToken: {}", idToken);
        sendRedirect(scopes, idToken);
        return null;
    }

    @Override
    public String retrieveClientToken(final String scopes) {
        return oauth2ServiceImpl.retrieveClientToken(scopes);
    }

    @Override
    public String retrieveToken(final AuthenticatedUserInfo currentUser, final String scopes) {
        requireNonNull(currentUser);
        requireNonNull(emptyToNull(scopes));
        final var oauthAuthenticatedUser = new OauthAuthenticatedUserInfo(currentUser);
        return checkAndRetrieveToken(oauthAuthenticatedUser, scopes);
    }

    @Override
    public Map<String, Object> retrieveIdToken(final AuthenticatedUserInfo currentUser) {
        final var token = new OauthAuthenticatedUserInfo(currentUser).getToken();
        if (null == token || MoreStrings.isEmpty(token.getId_token())) {
            return Collections.emptyMap();
        }
        final var tokenParts = token.getId_token().split("\\.");
        if (tokenParts.length != 3) {
            LOGGER.info("idToken can not be splitted: {}", token.getId_token());
            return Collections.emptyMap();
        }
        var objectReader = new ObjectMapper().reader().forType(new TypeReference<Map<String, Object>>() {

        });
        try {
            return objectReader.readValue(Base64.getDecoder().decode(tokenParts[1]));
        } catch (IOException e) {
            LOGGER.info(e, "idToken {} can not be parsed", tokenParts[1]);
            return Collections.emptyMap();
        }
    }

    private static Optional<OauthAuthenticatedUserInfo> retrieveCurrentUserIfPresent(
            final HttpServletRequest servletRequest) {
        final var session = servletRequest.getSession(false);
        if (null != session) {
            try {
                return Optional.ofNullable(OauthAuthenticatedUserInfo
                        .createOf((AuthenticatedUserInfo) session.getAttribute(AUTHENTICATED_USER_INFO_KEY)));
            } catch (final IllegalStateException e) {
                LOGGER.debug("getAttribute failed: ", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public String retrieveRenewInterval() {
        final var currentUser = retrieveCurrentUserIfPresent(servletRequestProvider.get());
        if (currentUser.isPresent()) {
            try {
                final var interval = currentUser.get().getTokenTimestamp() - (int) (System.currentTimeMillis() / 1000L)
                        + Integer.parseInt(currentUser.get().getToken().getExpires_in()) - 10;
                return String.valueOf(interval);
            } catch (final NumberFormatException e) {
                LOGGER.debug("token.expires_in not a valid number", e);
            }
        }
        return null;
    }

    @Override
    public AuthenticatedUserInfo refreshUserinfo() {
        final var currentUser = retrieveCurrentUserIfPresent(servletRequestProvider.get());
        if (currentUser.isPresent()) {
            final var token = currentUser.get().getToken();
            if (null != token) {
                return oauth2ServiceImpl.retrieveAuthenticatedUser(currentUser.get().getScopes(), token,
                        currentUser.get().getTokenTimestamp());
            }
        }
        return null;
    }

    @Override
    public String getLoginUrl() {
        return loginUrl.get();
    }

    @Override
    public String retrieveClientLogoutUrl(Set<UrlParameter> additionalUrlParams) {
        var config = configurationProvider.get();

        if (MoreStrings.isEmpty(config.getLogoutUri())) {
            var errMsg = """
                    Portal-160: Missing config for logout URI. Check \
                    the end_session_endpoint property from userinfo endpoint.\
                    """.formatted();
            LOGGER.warn(errMsg);
            throw new IllegalStateException(errMsg);
        }

        CollectionBuilder<UrlParameter> queryParams = CollectionBuilder.copyFrom(additionalUrlParams);

        if (config.isLogoutWithIdTokenHintEnabled() && queryParams.stream().map(UrlParameter::getName)
                .noneMatch(OidcRpInitiatedLogoutParams.ID_TOKEN_HINT::equals)) {

            LOGGER.debug("Adding id-token-hint as recommended by spec.");
            getIdTokenFromCurrentUser().map(OidcRpInitiatedLogoutParams::getIdTokenHintUrlParam)
                    .ifPresent(queryParams::add);
        }

        final var logoutUrl = config.getLogoutUri()
                + UrlParameter.createParameterString(queryParams.toArray(UrlParameter.class));
        LOGGER.trace("logoutUrl: {}", logoutUrl);

        return logoutUrl;
    }

    private Optional<String> getIdTokenFromCurrentUser() {
        var currentUser = retrieveCurrentUserIfPresent(servletRequestProvider.get());
        if (currentUser.isPresent()) {
            return currentUser.get().getIdToken();
        }
        LOGGER.warn("could not get id-token. no user context available.");
        return Optional.empty();
    }

    private String checkAndRetrieveToken(final OauthAuthenticatedUserInfo currentUser, final String scopes) {
        final var token = currentUser.getToken();
        if (checkToken(token, currentUser.getTokenTimestamp())) {
            LOGGER.debug("token is valid.");
            var allFound = true;
            final var existing = Splitter.on(' ').omitEmptyStrings().splitToList(currentUser.getScopes());
            for (final String requested : Splitter.on(' ').omitEmptyStrings().splitToList(scopes)) {
                if (!existing.contains(requested)) {
                    allFound = false;
                    LOGGER.debug("Missing scope: {}", requested);
                    break;
                }
            }
            if (allFound) {
                return token.getAccess_token();
            }
        } else if (!MoreStrings.isEmpty(token.getRefresh_token())) {
            LOGGER.debug("AccessToken expired, but RefreshToken present; trying to use it to get a new access token");
            return oauth2ServiceImpl.refreshToken(currentUser);
        }
        return null;
    }

    private static boolean checkToken(final Token token, final Integer timestamp) {
        if (null == token) {
            return false;
        }
        if (MoreStrings.isEmpty(token.getExpires_in())) {
            LOGGER.trace("token has no expiration. token is valid!");
            return true;
        }
        try {
            final var expires = timestamp + Integer.parseInt(token.getExpires_in()) - 10;
            final boolean valid = expires > (int) (System.currentTimeMillis() / 1000L);
            LOGGER.trace("checked expire time. token valid?: {}", valid);
            return valid;
        } catch (final NumberFormatException e) {
            LOGGER.warn("Portal-149: Oauth2 token.expires_in not a valid number", e);
            return false;
        }
    }

    @Override
    public AuthenticationSource getAuthenticationSource() {
        return AuthenticationSource.OPEN_ID_CONNECT;
    }
}
