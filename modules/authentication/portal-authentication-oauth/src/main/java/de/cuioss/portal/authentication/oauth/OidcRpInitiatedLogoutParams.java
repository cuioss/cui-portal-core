package de.cuioss.portal.authentication.oauth;

import java.util.Optional;

import de.cuioss.tools.net.UrlParameter;
import lombok.experimental.UtilityClass;

/**
 * Logout parameter for the OIDC RP-Initiated Logout 1.0 Draft 2.
 *
 */
@UtilityClass
public class OidcRpInitiatedLogoutParams {

    /** "id_token_hint" */
    public static final String ID_TOKEN_HINT = "id_token_hint";

    /** "logout_hint" */
    public static final String LOGOUT_HINT = "logout_hint";

    /** "client_id" */
    public static final String CLIENT_ID = "client_id";

    /** "post_logout_redirect_uri" */
    public static final String POST_LOGOUT_REDIRECT_URI = "post_logout_redirect_uri";

    /** "state" */
    public static final String STATE = "state";

    /** "ui_locales" */
    public static final String UI_LOCALES = "ui_locales";

    /**
     * A valid ID token attribute is required, in order for the IDP to check the given redirect URI
     * against the oauth clients post_logout_redirect_uris list.
     * 
     * @param idTokenHint to be created
     * @return The correctly prefixed parameter
     */
    public static Optional<UrlParameter> getIdTokenHintUrlParam(String idTokenHint) {
        return Optional.ofNullable(idTokenHint).map(value -> new UrlParameter(ID_TOKEN_HINT, value));
    }

    /**
     * @param logoutHint to be created
     * @return The correctly prefixed parameter
     */
    public static Optional<UrlParameter> getLogoutHintUrlParam(String logoutHint) {
        return Optional.ofNullable(logoutHint).map(value -> new UrlParameter(LOGOUT_HINT, value));
    }

    /**
     * @param clientId to be created
     * @return The correctly prefixed parameter
     */
    public static Optional<UrlParameter> getClientIdUrlParam(String clientId) {
        return Optional.ofNullable(clientId).map(value -> new UrlParameter(CLIENT_ID, value));
    }

    /**
     * @param redirectUri to be created
     * @return The correctly prefixed parameter
     */
    public static Optional<UrlParameter> getPostLogoutRedirectUriUrlParam(String redirectUri) {
        return Optional.ofNullable(redirectUri).map(value -> new UrlParameter(POST_LOGOUT_REDIRECT_URI, value));
    }

    /**
     * @param state to be created
     * @return The correctly prefixed parameter
     */
    public static Optional<UrlParameter> getStateUrlParam(String state) {
        return Optional.ofNullable(state).map(value -> new UrlParameter(STATE, value));
    }

    /**
     * @param locales to be created
     * @return The correctly prefixed parameter
     */
    public static Optional<UrlParameter> getUiLocalesUrlParam(String locales) {
        return Optional.ofNullable(locales).map(value -> new UrlParameter(UI_LOCALES, value));
    }
}
