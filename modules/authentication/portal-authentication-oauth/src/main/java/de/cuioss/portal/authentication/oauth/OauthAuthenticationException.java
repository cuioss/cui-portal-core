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
package de.cuioss.portal.authentication.oauth;

import java.io.Serial;

/**
 * Thrown to indicate that an OAuth2 authentication operation has failed.
 * This exception is used to wrap OAuth2-specific authentication failures such as:
 * <ul>
 *   <li>Invalid state parameter in the OAuth2 callback</li>
 *   <li>Missing or invalid tokens</li>
 *   <li>Authorization server errors</li>
 * </ul>
 *
 * <p>The exception message typically contains a message key that can be
 * resolved to a user-friendly error message.
 *
 * @author Matthias Walliczek
 */
public class OauthAuthenticationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2351542706407596661L;

    /**
     * Constructs a new OAuth authentication exception with the specified message key.
     *
     * @param messageKey The key identifying the specific authentication failure
     */
    public OauthAuthenticationException(final String messageKey) {
        super(messageKey);
    }
}
