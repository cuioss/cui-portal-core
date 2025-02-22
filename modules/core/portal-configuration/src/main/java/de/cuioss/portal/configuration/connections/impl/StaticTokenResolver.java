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
package de.cuioss.portal.configuration.connections.impl;

import de.cuioss.portal.configuration.connections.TokenResolver;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serial;

/**
 * Implementation of {@link TokenResolver} for static, application-wide tokens.
 * This resolver is used when the token value remains constant and is not
 * user-specific or session-bound.
 * <p>
 * Use cases include:
 * <ul>
 *   <li>API keys for service-to-service communication</li>
 *   <li>Static bearer tokens for application authentication</li>
 *   <li>Shared secrets for internal system communication</li>
 * </ul>
 * <p>
 * Note: This implementation is immutable and thread-safe.
 *
 * @author Oliver Wolff
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString(of = "key") // Only key, because Token may be considered as sensitive data
public class StaticTokenResolver implements TokenResolver {

    @Serial
    private static final long serialVersionUID = 7523596484663692845L;

    /**
     * The key or name identifying this token.
     * This is typically used in headers or query parameters to identify
     * the token type or purpose.
     */
    @Getter
    private final String key;

    /**
     * The actual token value.
     * Not included in toString() for security reasons.
     */
    private final String token;

    /**
     * Resolves and returns the static token value.
     * Since this is a static implementation, it always returns the same token
     *  provided during construction.
     *
     * @return the static token value, never null
     */
    @Override
    public String resolve() {
        return token;
    }

}
