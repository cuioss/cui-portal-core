package de.cuioss.portal.authentication.oauth;

import javax.servlet.http.HttpServletRequest;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.oauth.impl.OauthAuthenticatedUserInfo;
import de.cuioss.tools.net.UrlParameter;

/**
 * Accessing the oauth2 services to handle login, token request, and accessing user information.
 *
 * @author Matthias Walliczek
 */
public interface Oauth2Service {

    /**
     * Creates an authenticated {@link AuthenticatedUserInfo}
     * by requesting a token and retrieving user data from the /userinfo endpoint.
     * The returned {@link AuthenticatedUserInfo} contains the {@code preferred_username} as
     * {@link AuthenticatedUserInfo#getDisplayName()} and {@code sub} as {@link AuthenticatedUserInfo#getIdentifier()}.
     * Other userinfo data is stored in the {@link AuthenticatedUserInfo#getContextMap()}.
     *
     * @param servletRequest
     * @param code
     * @param state
     * @param scopes the scopes to request
     * @return an authenticated {@link AuthenticatedUserInfo}.
     */
    AuthenticatedUserInfo createAuthenticatedUserInfo(HttpServletRequest servletRequest, UrlParameter code,
            UrlParameter state, String scopes, final String codeVerifier);

    /**
     * Calculate the redirect url that gets send to the oauth2 server as urlencoded parameter value.
     *
     * @param url
     * @return the url.
     */
    String calcEncodedRedirectUrl(String url);

    /**
     * Retrieve an {@link AuthenticatedUserInfo} with the user info properties from the oauth
     * server.
     *
     * @param scopes
     * @param token
     * @param tokenTimestamp
     * @return
     */
    AuthenticatedUserInfo retrieveAuthenticatedUser(String scopes, Token token, int tokenTimestamp);

    /**
     * Retrieve an accessToken for the client with defined scope(s).
     *
     * @param scopes the scopes as space separated list, can be null (= all registered scopes)
     * @return the accessToken
     */
    String retrieveClientToken(String scopes);

    /**
     * refresh the current access token
     *
     * @param currentUser
     * @return
     */
    String refreshToken(OauthAuthenticatedUserInfo currentUser);
}
