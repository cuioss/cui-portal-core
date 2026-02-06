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
package de.cuioss.portal.restclient;

import de.cuioss.portal.configuration.connections.TokenResolver;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import lombok.RequiredArgsConstructor;

/**
 * Client filter that implements token-based authentication for REST client requests.
 * The actual token is provided through a {@link TokenResolver} implementation.
 *
 * <p>The filter is automatically configured by {@link CuiRestClientBuilder}
 * when token authentication is specified in the configuration.
 *
 * @see CuiRestClientBuilder
 */
@RequiredArgsConstructor
public class TokenFilter implements ClientRequestFilter {

    private final TokenResolver tokenResolver;

    @Override
    public void filter(ClientRequestContext requestContext) {
        if (null != tokenResolver) {
            requestContext.getHeaders().putSingle(tokenResolver.getKey(), tokenResolver.resolve());
        }
    }
}
