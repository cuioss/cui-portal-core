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
 * Provides MicroProfile REST Client integration for the Portal Core.
 * 
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.restclient.CuiRestClientBuilder} - The central builder for creating REST clients</li>
 *   <li>{@link de.cuioss.portal.restclient.RestClientProducer} - CDI producer for REST clients</li>
 *   <li>{@link de.cuioss.portal.restclient.PortalRestClient} - Qualifier for injecting configured REST clients</li>
 * </ul>
 * 
 * <h2>Authentication</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.restclient.BasicAuthenticationFilter} - HTTP Basic Authentication</li>
 *   <li>{@link de.cuioss.portal.restclient.BearerTokenAuthFilter} - Bearer Token Authentication</li>
 *   <li>{@link de.cuioss.portal.restclient.TokenFilter} - Base class for token-based authentication</li>
 * </ul>
 * 
 * <h2>Logging</h2>
 * Provides comprehensive request/response logging support:
 * <ul>
 *   <li>{@link de.cuioss.portal.restclient.LogClientRequestFilter} - Logs outgoing requests</li>
 *   <li>{@link de.cuioss.portal.restclient.LogClientResponseFilter} - Logs incoming responses</li>
 *   <li>{@link de.cuioss.portal.restclient.LogReaderInterceptor} - Intercepts and logs message bodies</li>
 * </ul>
 * 
 * @see de.cuioss.portal.restclient.CuiRestClientBuilder
 * @see de.cuioss.portal.restclient.PortalRestClient
 */
package de.cuioss.portal.restclient;
