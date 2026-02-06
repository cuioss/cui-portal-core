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
 * Provides the core authentication facade and related services for Portal applications.
 * 
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.authentication.facade.AuthenticationFacade} - Primary interface for authentication operations</li>
 *   <li>{@link de.cuioss.portal.authentication.facade.BaseAuthenticationFacade} - Base implementation with common functionality</li>
 *   <li>{@link de.cuioss.portal.authentication.facade.AuthenticationResults} - Constants and utility results</li>
 * </ul>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Authentication context management</li>
 *   <li>User session handling</li>
 *   <li>Logout functionality</li>
 *   <li>Authentication source tracking</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * The {@link de.cuioss.portal.authentication.facade.AuthenticationFacade} is the main entry point
 * for authentication operations. Implementations should extend
 * {@link de.cuioss.portal.authentication.facade.BaseAuthenticationFacade} to leverage common functionality.
 * 
 * <h2>Thread Safety</h2>
 * All components in this package are designed to be thread-safe and suitable for use in concurrent
 * web applications. They rely on CDI-managed components and immutable state.
 * 
 * @author Oliver Wolff
 */
package de.cuioss.portal.authentication.facade;
