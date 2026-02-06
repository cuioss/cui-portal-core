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
 * Provides the core OAuth2 authentication framework for portal applications.
 * 
 * <h2>Core Concepts</h2>
 * <ul>
 *   <li><em>Authentication Flow</em>: Implements the OAuth2 authorization code flow
 *       with PKCE support for enhanced security</li>
 *   <li><em>Token Management</em>: Handles access tokens, refresh tokens, and ID tokens
 *       (when using OpenID Connect)</li>
 *   <li><em>Role Mapping</em>: Maps OAuth2 claims to portal roles for authorization</li>
 *   <li><em>Session Integration</em>: Manages user sessions and token lifecycle</li>
 * </ul>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.authentication.oauth.Oauth2Service} - Core service
 *       interface for OAuth2 operations</li>
 *   <li>{@link de.cuioss.portal.authentication.oauth.Oauth2Configuration} - Configuration
 *       contract for OAuth2 settings</li>
 *   <li>{@link de.cuioss.portal.authentication.oauth.Token} - Represents OAuth2
 *       token responses</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * Applications integrate with this framework by:
 * <ol>
 *   <li>Implementing or injecting {@code Oauth2Configuration}</li>
 *   <li>Using {@code Oauth2Service} for authentication flows</li>
 *   <li>Configuring role mappings for authorization</li>
 * </ol>
 * 
 * @see de.cuioss.portal.authentication.oauth.impl
 */
package de.cuioss.portal.authentication.oauth;
