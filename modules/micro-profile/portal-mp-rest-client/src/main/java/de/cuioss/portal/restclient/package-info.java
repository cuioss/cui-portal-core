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
 * Provides Portal-specific REST client implementation based on MicroProfile REST Client.
 * 
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.restclient.CuiRestClientBuilder} - Portal-specific REST client builder</li>
 *   <li>{@link de.cuioss.portal.restclient.RestClientProducer} - CDI producer for REST clients</li>
 *   <li>{@link de.cuioss.portal.restclient.PortalRestClient} - Qualifier for REST client injection</li>
 * </ul>
 * 
 * <h2>Authentication</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.restclient.BasicAuthenticationFilter} - HTTP Basic Authentication</li>
 *   <li>{@link de.cuioss.portal.restclient.BearerTokenAuthFilter} - Bearer Token Authentication</li>
 *   <li>{@link de.cuioss.portal.restclient.TokenFilter} - Token-based authentication</li>
 * </ul>
 * 
 * <h2>Logging Support</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.restclient.LogClientRequestFilter} - Request logging</li>
 *   <li>{@link de.cuioss.portal.restclient.LogClientResponseFilter} - Response logging</li>
 *   <li>{@link de.cuioss.portal.restclient.LogReaderInterceptor} - Message body logging</li>
 * </ul>
 * 
 * <p>Configuration is provided through {@link de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata}
 * 
 * @see de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata
 */
package de.cuioss.portal.restclient;
