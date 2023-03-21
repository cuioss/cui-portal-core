package de.cuioss.portal.authentication.oauth;

/**
 * @author Matthias Walliczek
 */
public class OauthAuthenticationException extends RuntimeException {

    private static final long serialVersionUID = -2351542706407596661L;

    public OauthAuthenticationException(final String string) {
        super(string);
    }

}
