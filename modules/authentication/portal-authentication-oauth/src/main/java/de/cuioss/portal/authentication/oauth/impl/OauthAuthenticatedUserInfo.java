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

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.oauth.Token;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.io.Serial;
import java.util.Optional;

@RequiredArgsConstructor
public class OauthAuthenticatedUserInfo implements AuthenticatedUserInfo {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * The key of the accessToken to be stored in the
     * {@link AuthenticatedUserInfo#getContextMap()}.
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
     * @return The id-token
     */
    public Optional<String> getIdToken() {
        var token = getToken();
        if (null != token) {
            return Optional.ofNullable(token.getId_token());
        }
        return Optional.empty();
    }
}
