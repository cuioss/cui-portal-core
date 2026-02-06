/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.configuration.connections;

import java.io.Serializable;

/**
 * Defines the contract for resolving authentication tokens in the portal's connection system.
 * A token resolver is responsible for providing both the token's identifier (key) and its value,
 * handling any necessary token processing like encoding, formatting, or transformation.
 * <p>
 * Implementations of this interface should handle various token types and sources:
 * <ul>
 *   <li>Static application tokens</li>
 *   <li>User session tokens</li>
 *   <li>OAuth/OIDC tokens</li>
 *   <li>API keys</li>
 * </ul>
 * <p>
 * The interface extends {@link Serializable} to support token persistence and
 * distribution in clustered environments.
 *
 * @author Oliver Wolff
 */
public interface TokenResolver extends Serializable {

    /**
     * Returns the key or identifier for this token.
     * <p>
     * The key is typically used:
     * <ul>
     *   <li>As an HTTP header name (e.g., "Authorization", "X-API-Key")</li>
     *   <li>As a query parameter name</li>
     *   <li>To identify the token type in configuration</li>
     * </ul>
     *
     * @return the token key/identifier, never null
     */
    String getKey();

    /**
     * Resolves and returns the actual token value.
     * <p>
     * The implementation should handle all necessary token processing, including:
     * <ul>
     *   <li>Token retrieval from the source (static config, user session, etc.)</li>
     *   <li>Token formatting (e.g., adding "Bearer" prefix)</li>
     *   <li>Token encoding (e.g., Base64 encoding)</li>
     *   <li>Token transformation or combination</li>
     * </ul>
     *
     * @return the processed token value, never null
     */
    String resolve();
}
