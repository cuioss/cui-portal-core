package de.cuioss.portal.authentication.oauth;

import java.io.Serializable;
import java.util.List;

/**
 * Contains all configuration properties for the oauth2 client
 */
public interface Oauth2Configuration extends Serializable {

    /**
     * @return
     */
    String getAuthorizeUri();

    /**
     * @return The client-id of the portal-application. This is provided /
     *         maintained by the corresponding SSO-Server. Must be set by the
     *         installation.
     */
    String getClientId();

    /**
     * @return The client-secret of the portal-application. This is provided /
     *         maintained by the corresponding SSO-Server. Must be set by the
     *         installation.
     */
    String getClientSecret();

    String getTokenUri();

    String getUserInfoUri();

    List<String> getRoleMapperClaims();

    /**
     * @return The current external host name used to calculate the redirect uri for
     *         the browser of the external user.
     */
    String getExternalContextPath();

    /**
     * @return The default scopes to be requested by the client. The individual
     *         scopes are separated by whitespaces.
     */
    String getInitialScopes();

    String getLogoutUri();

    /**
     * @return The parameter name to transport the url to redirect after logout.
     */
    String getLogoutRedirectParamName();

    /**
     * @return full URI to be used for the {@code post_logout_redirect_uri}
     *         parameter.
     */
    String getPostLogoutRedirectUri();

    boolean isLogoutWithIdTokenHintEnabled();
}
