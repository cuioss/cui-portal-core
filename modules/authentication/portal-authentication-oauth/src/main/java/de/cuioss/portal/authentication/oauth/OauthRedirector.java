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
package de.cuioss.portal.authentication.oauth;

/**
 * Provides redirection capabilities for OAuth2 authentication flows.
 * This interface abstracts the redirection mechanism to support both JSF and non-JSF
 * environments, ensuring consistent redirection behavior across different module types.
 *
 * <p>The redirector is responsible for:
 * <ul>
 *   <li>Handling external URL redirections</li>
 *   <li>Managing context-independent URL processing</li>
 *   <li>Providing consistent error handling</li>
 * </ul>
 */
public interface OauthRedirector {

    /**
     * Performs a redirect to the specified URL.
     * The implementation must handle the redirection in a way appropriate for the current environment (JSF/non-JSF).
     *
     * @param url The complete, context-independent URL to redirect to
     * @throws IllegalStateException if the redirection cannot be performed due to
     *                               technical issues or invalid state
     */
    void sendRedirect(String url) throws IllegalStateException;

}
