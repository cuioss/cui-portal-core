package de.cuioss.portal.authentication.oauth.impl;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.oauth.Oauth2Service;
import de.cuioss.portal.authentication.oauth.Token;
import de.cuioss.tools.net.UrlParameter;

@SuppressWarnings("javadoc")
public class Oauth2ServiceMock implements Oauth2Service, Serializable {

    private static final long serialVersionUID = -2635417375165112609L;

    @Override
    public AuthenticatedUserInfo createAuthenticatedUserInfo(final HttpServletRequest servletRequest,
            final UrlParameter code, final UrlParameter state, final String scopes, final String codeVerifier) {
        var token = new Token();
        token.setAccess_token("access");
        token.setId_token("id");
        token.setState("state");
        return retrieveAuthenticatedUser(scopes, token, (int) (System.currentTimeMillis() / 1000L));
    }

    @Override
    public String calcEncodedRedirectUrl(final String url) {
        return url;
    }

    @Override
    public AuthenticatedUserInfo retrieveAuthenticatedUser(String scopes, Token token, int tokenTimestamp) {
        return BaseAuthenticatedUserInfo.builder().authenticated(true).identifier("dummyIdentifier")
                .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_KEY, token)
                .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_TIMESTAMP_KEY,
                        (int) (System.currentTimeMillis() / 1000L))
                .contextMapElement(OauthAuthenticatedUserInfo.TOKEN_SCOPES_KEY, scopes).build();
    }

    @Override
    public String retrieveClientToken(String scopes) {
        return null;
    }

    @Override
    public String refreshToken(OauthAuthenticatedUserInfo currentUser) {
        return null;
    }
}
