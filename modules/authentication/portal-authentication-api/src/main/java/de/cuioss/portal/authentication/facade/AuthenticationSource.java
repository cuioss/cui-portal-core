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

/**
 * Identifies the currently active Identification Source
 *
 * @author Oliver Wolff
 */
public enum AuthenticationSource {

    /**
     * Any application-specific implementation.
     */
    CUSTOM,

    /**
     * The authentication is done by an external Key-Cloak Server.
     */
    KEYCLOAK,

    /**
     * Special variant for testing purpose.
     */
    MOCK,

    /**
     * Special variant always returning a non-authenticated user.
     */
    DUMMY,

    /**
     * Open ID Connect / Oauth2
     */
    OPEN_ID_CONNECT,

    /**
     * The application local tomcat-users.xml
     */
    TOMCAT_USER,

    /**
     * Default / {@code null} value.
     */
    UNDEFINED
}
