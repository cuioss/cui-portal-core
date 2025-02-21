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

/**
 * Provides implementations of the OAuth2 authentication framework interfaces.
 * 
 * <h2>Implementation Strategy</h2>
 * <ul>
 *   <li><em>CDI Integration</em>: Components use CDI for configuration injection
 *       and lifecycle management</li>
 *   <li><em>Thread Safety</em>: All implementations are thread-safe for use in
 *       multi-user environments</li>
 *   <li><em>Error Handling</em>: OAuth2-specific exceptions with clear error messages
 *       and proper logging</li>
 * </ul>
 * 
 * <h2>Key Implementations</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.authentication.oauth.impl.Oauth2ServiceImpl} -
 *       Default OAuth2 service implementation</li>
 *   <li>{@link de.cuioss.portal.authentication.oauth.impl.Oauth2ConfigurationImpl} -
 *       Configuration implementation using MicroProfile Config</li>
 *   <li>{@link de.cuioss.portal.authentication.oauth.impl.OauthAuthenticatedUserInfo} -
 *       OAuth2-specific user information</li>
 * </ul>
 * 
 * <h2>Extension Points</h2>
 * The implementation provides several extension points:
 * <ul>
 *   <li>Custom role mapping strategies</li>
 *   <li>Token storage customization</li>
 *   <li>Authentication flow modifications</li>
 * </ul>
 * 
 * @see de.cuioss.portal.authentication.oauth
 */
package de.cuioss.portal.authentication.oauth.impl;
