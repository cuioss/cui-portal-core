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
/**
 * Mock implementation of the Portal Authentication API for testing and development purposes.
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.authentication.mock.MockAuthenticationFacade} - Configurable mock implementation
 *       of the authentication facade</li>
 *   <li>{@link de.cuioss.portal.authentication.mock.MockAuthenticationLogMessages} - Logging support for mock
 *       authentication events</li>
 * </ul>
 * 
 * <h2>Configuration</h2>
 * <p>The mock facade is highly configurable through the following properties:</p>
 * <ul>
 *   <li>{@code portal.MockAuthenticationFacade.authenticated} - Controls initial authentication state</li>
 *   <li>{@code portal.MockAuthenticationFacade.username} - Sets the default username</li>
 *   <li>{@code portal.MockAuthenticationFacade.system} - Defines the authentication system</li>
 *   <li>{@code portal.MockAuthenticationFacade.groups} - Configures user groups</li>
 *   <li>{@code portal.MockAuthenticationFacade.roles} - Sets user roles</li>
 *   <li>{@code portal.MockAuthenticationFacade.contextMap} - Defines additional context attributes</li>
 * </ul>
 * 
 * <p><strong>Note:</strong> This module is intended for testing and development only.
 * It must not be used in production environments.</p>
 * 
 * @author Oliver Wolff
 */
package de.cuioss.portal.authentication.mock;
