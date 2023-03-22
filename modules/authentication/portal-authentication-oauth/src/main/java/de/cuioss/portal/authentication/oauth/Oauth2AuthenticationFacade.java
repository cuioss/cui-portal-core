package de.cuioss.portal.authentication.oauth;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.facade.AuthenticationFacade;
import de.cuioss.tools.net.UrlParameter;

/**
 * An extension of the {@link AuthenticationFacade} with oauth2 specific functions.
 *
 * @author Matthias Walliczek
 */
public interface Oauth2AuthenticationFacade extends AuthenticationFacade {

    /**
     * The key of the email to be stored in the {@link AuthenticatedUserInfo#getContextMap()}.
     */
    String EMAIL_KEY = "email";

    /**
     * Prefix to store additional key/value entries of userinfo response in
     * {@link AuthenticatedUserInfo#getContextMap()}.
     */
    String USERINFO_PREFIX_KEY = "userinfo_";

    /**
     * Start the Oauth login by initializing the parameters and redirect to the oauth2 server.
     * Retrieve the current scope set.
     */
    void sendRedirect();

    /**
     * Start the Oauth login by initializing the parameters and redirect to the oauth2 server.
     *
     * @param scopes the scopes as space separated list
     */
    void sendRedirect(final String scopes);

    /**
     * Check if this request was caused by the oauth2 server after successful authentication and the
     * parameters contains valid authentication data. If so, retrieve the username and subject and
     * create a authenticated {@link AuthenticatedUserInfo} to be stored in the session and
     * returned. Otherwise an unauthenticated {@link AuthenticatedUserInfo} will be returned.
     *
     * @param parameters
     * @param scopes the scopes as space separated list
     * @return an {@link AuthenticatedUserInfo} that can be authenticated or unauthenticated.
     */
    AuthenticatedUserInfo testLogin(List<UrlParameter> parameters, final String scopes);

    /**
     * Invalidates the existing access token. The next call to {@link #retrieveToken(String)} will trigger a redirect
     * to the oauth2 server.
     */
    void invalidateToken();

    /**
     * Retrieve an accessToken for the defined scope(s). Either a token with this scope is already
     * present, than this function will return this token. Or a new authentication at the oauth2
     * server needs to be triggered, than a redirect will be executed an null will be returned.
     * After successful authentication this page will be accessed again, and the function will
     * return the token.
     *
     * @param scopes the scopes as space separated list
     * @return the accessToken if present, otherwise null.
     */
    String retrieveToken(String scopes);

    /**
     * Retrieve an accessToken for the defined scope(s). Either a token with this scope is already
     * present, than this function will return this token. Otherwise null is returned.
     *
     * @param currentUser
     * @param scopes the scopes as space separated list
     * @return the accessToken if present, otherwise null.
     */
    String retrieveToken(AuthenticatedUserInfo currentUser, String scopes);

    /**
     * Retrieve the parsed id token for the current user session.
     *
     * @param currentUser
     * @return the parsed id token.
     */
    Map<String, Object> retrieveIdToken(AuthenticatedUserInfo currentUser);

    /**
     * Retrieve an accessToken for the client with defined scope(s).
     *
     * @param scopes the scopes as space separated list, can be null (= all registered scopes)
     * @return the accessToken
     */
    String retrieveClientToken(String scopes);

    /**
     * Creates a URL to redirect an unauthenticated user to the oauth2 server with correct client id
     * / secret / scopes containing the url of the current request as redirect target.
     *
     * @param scopes the scopes as space separated list
     * @param idToken the id token if present
     * @return an url to redirect to.
     */
    String retrieveOauth2RedirectUrl(final String scopes, final String idToken);

    /**
     * Create a url to be called via ajax from the browser to renew the token.
     *
     * @return an url.
     */
    String retrieveOauth2RenewUrl();

    /**
     * Retrieve the remaining duration to trigger renewing of the token before it expires.
     *
     * @return the remaining duration in seconds.
     */
    String retrieveRenewInterval();

    /**
     * Refresh the user info properties from the oauth server
     */
    AuthenticatedUserInfo refreshUserinfo();

    /**
     * the configured login url
     */
    String getLoginUrl();

    /**
     * Logout URL to trigger OpenID-Connect RP-Initiated logout (RP=Relying Party).
     *
     * @param additionalUrlParams additional URL parameters. Can be null.
     * @return IDP logout URL including {@code id_token_hint} and {@code post_logout_redirect_uri} parameters as well
     *         as any {@code additionalUrlParams} URL parameter
     * @throws IllegalStateException in case a mandatory configuration is missing
     * @see <a href="https://openid.net/specs/openid-connect-rpinitiated-1_0.html">OpenID Connect RP-Initiated Logout 1.0</a>
     */
    String retrieveClientLogoutUrl(Set<UrlParameter> additionalUrlParams);
}
