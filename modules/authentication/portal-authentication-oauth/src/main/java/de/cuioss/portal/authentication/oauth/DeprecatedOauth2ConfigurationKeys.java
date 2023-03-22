package de.cuioss.portal.authentication.oauth;

import java.io.Serializable;

/**
 * Provide the keys for the application.properties to configure oauth2.
 * <p>
 * See also https://wiki.icw.int/display/CUI/Howto+enable+Oauth2+Authentication
 *
 * @author Matthias Walliczek
 */
@Deprecated // Use Oauth2ConfigurationKeys instead
public class DeprecatedOauth2ConfigurationKeys implements Serializable {

    private static final long serialVersionUID = 7710072530801495876L;

    /**
     * The client id the application is registered with at the oauth2 server. Will be used for basic
     * authentication at {@link #OAUTH2_SERVER_REST_URI} and via redirect to
     * {@link #OAUTH2_SERVER_AUTHENTICATION_URI}.
     */
    public static final String CLIENT_ID = "oauth2clientId";

    /**
     * The client secret the application is registered with at the oauth2 server. Will be used for
     * basic authentication at {@link #OAUTH2_SERVER_REST_URI}.
     */
    public static final String CLIENT_SECRET = "oauth2clientSecret";

    /**
     * The uri of the oauth2 server to redirect the browser to for login.
     * E.g. https://xyz/c2id-facade/oauth/authorize
     */
    public static final String OAUTH2_SERVER_AUTHENTICATION_URI = "oauth2authorize";

    /**
     * The uri of the oauth2 server rest interface used to retrieve the token and userInfo.
     * E.g. https://xyz/c2id-facade/c2id
     */
    public static final String OAUTH2_SERVER_REST_URI = "oauth2uri";

    /**
     * The current external deployment path used to calculate the redirect uri. E.g.
     * https://ri-ux-inbound-01.ci.dev.icw.int:8447
     */
    public static final String CURRENT_DEPLOYMENT_EXTERNAL_CONTEXT_PATH = "externalContextPath";

    /**
     * The initial set of scopes to request at login page.
     */
    public static final String OAUTH2INITIAL_SCOPES = "oauth2initialScopes";

    public static final String OAUTH2LOGOUT_URI = "oauth2logout";

    public static final String OAUTH2LOGOUT_URI_REDIRECT_PARAMETER = "oauth2logoutRedirectParameter";
}
