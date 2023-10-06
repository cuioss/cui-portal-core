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

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.oauth.Oauth2Service;
import de.cuioss.portal.authentication.oauth.Token;
import de.cuioss.tools.net.UrlParameter;
import lombok.Getter;
import lombok.Setter;

public class Oauth2ServiceMock implements Oauth2Service, Serializable {

    private static final long serialVersionUID = -2635417375165112609L;

    @Getter
    @Setter
    private String clientToken;

    @Getter
    @Setter
    private String refreshToken;

    public void reset() {
        clientToken = null;
        refreshToken = null;
    }

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
        return clientToken;
    }

    @Override
    public String refreshToken(OauthAuthenticatedUserInfo currentUser) {
        return refreshToken;
    }
}
