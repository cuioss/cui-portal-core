package de.cuioss.portal.authentication.oauth.impl;

import java.util.Optional;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.oauth.Token;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class OauthAuthenticatedUserInfo implements AuthenticatedUserInfo {

    private static final long serialVersionUID = 1L;
    /**
     * The key of the accessToken to be stored in the {@link AuthenticatedUserInfo#getContextMap()}.
     */
    static final String TOKEN_KEY = "token";
    static final String TOKEN_SCOPES_KEY = "tokenScopes";
    static final String TOKEN_TIMESTAMP_KEY = "tokenTimestamp";

    @Delegate
    private final AuthenticatedUserInfo wrapped;

    public static OauthAuthenticatedUserInfo createOf(AuthenticatedUserInfo wrapped) {
        if (null == wrapped) {
            return null;
        }
        return new OauthAuthenticatedUserInfo(wrapped);
    }

    public Token getToken() {
        return (Token) wrapped.getContextMap().get(TOKEN_KEY);
    }

    public String getScopes() {
        return (String) wrapped.getContextMap().get(TOKEN_SCOPES_KEY);
    }

    public int getTokenTimestamp() {
        return (Integer) wrapped.getContextMap().get(TOKEN_TIMESTAMP_KEY);
    }

    /**
     * @return {@link Token#getId_token()}
     */
    public Optional<String> getIdToken() {
        var token = getToken();
        if (null != token) {
            return Optional.ofNullable(token.getId_token());
        }
        return Optional.empty();
    }
}
