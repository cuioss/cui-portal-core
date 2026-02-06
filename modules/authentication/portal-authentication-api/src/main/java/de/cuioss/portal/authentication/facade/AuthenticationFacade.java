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
package de.cuioss.portal.authentication.facade;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Basic interface for the cui-portals authentication mechanism.
 * This variant is used by sso logins
 *
 * @author Matthias Walliczek
 *
 */
public interface AuthenticationFacade {

    /**
     * Access the current {@link AuthenticatedUserInfo}.
     *
     * @param servletRequest The servlet request.
     * @return Returns <code>null</code> in case no authentication has taken place.
     *         In case of an existing authentication a populated
     *         {@link AuthenticatedUserInfo} instance is returned.
     */
    AuthenticatedUserInfo retrieveCurrentAuthenticationContext(HttpServletRequest servletRequest);

    /**
     * Logs out the currently authenticated user.
     *
     * @param servletRequest The current servlet request that triggered the logout.
     * @return Returns a boolean indicating whether the logout request was
     *         successful.
     */
    boolean logout(HttpServletRequest servletRequest);

    /**
     * @return The {@link AuthenticationSource} identifying the Source this
     *         {@link AuthenticationFacade} uses.
     */
    AuthenticationSource getAuthenticationSource();

}
