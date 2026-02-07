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
package de.cuioss.portal.authentication;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Interface representing user-specific information in the session context.
 * Implementations must be thread-safe and immutable to ensure consistent state across
 * multiple threads.
 * 
 * <h2>Usage Example</h2>
 * <pre>
 * AuthenticatedUserInfo userInfo = getCurrentUser();
 * if (userInfo.isAuthenticated() &amp;&amp; userInfo.isUserInRole("ADMIN")) {
 *     // Perform admin operation
 * }
 * </pre>
 * 
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>All implementations must be thread-safe</li>
 *   <li>Return values must never be null - use empty collections instead</li>
 *   <li>Changes to the user state should create new instances</li>
 * </ul>
 *
 * @author Stephan Babkin
 * @see de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo
 * @see de.cuioss.portal.authentication.PortalUserEnricher
 * @since 1.0
 */
@SuppressWarnings("squid:S1214") // We allow constants in interfaces if they belong together
// (coherence).
public interface AuthenticatedUserInfo extends Serializable {

    /**
     * Indicates whether the user is currently authenticated.
     *
     * @return {@code true} if the user is authenticated, {@code false} otherwise
     */
    boolean isAuthenticated();

    /**
     * Returns the list of roles assigned to the user. The list is never null but may be empty.
     *
     * @return an unmodifiable list of role names, never {@code null}
     */
    List<String> getRoles();

    /**
     * Checks if the user has a specific role.
     *
     * @param roleName the role name to check, must not be {@code null}
     * @return {@code true} if the user has the role, {@code false} otherwise
     * @throws NullPointerException if roleName is {@code null}
     */
    default boolean isUserInRole(String roleName) {
        return getRoles().contains(roleName);
    }

    /**
     * Returns the list of groups the user belongs to. The list is never null but may be empty.
     *
     * @return an unmodifiable list of group names, never {@code null}
     */
    List<String> getGroups();

    /**
     * Returns the display name of the authenticated user.
     *
     * @return the user's display name, never {@code null}
     */
    String getDisplayName();

    /**
     * Returns the technical identifier for the authenticated user.
     * This identifier should be used in conjunction with {@link #getSystem()}.
     *
     * @return the user's identifier, never {@code null}
     */
    String getIdentifier();

    /**
     * Returns the qualified identifier for the authenticated user.
     * This identifier can be used independently of {@link #getSystem()}.
     *
     * @return the user's qualified identifier, never {@code null}
     */
    String getQualifiedIdentifier();

    /**
     * Returns the technical authority that assigned the {@link #getIdentifier()}.
     *
     * @return the system identifier, never {@code null}
     */
    String getSystem();

    /**
     * Returns a map containing additional user context information.
     * The map and its contents must be immutable to ensure thread-safety.
     *
     * @return an unmodifiable map of context information, never {@code null}
     */
    Map<Serializable, Serializable> getContextMap();
}
