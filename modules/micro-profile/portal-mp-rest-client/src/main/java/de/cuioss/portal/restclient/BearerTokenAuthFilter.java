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
package de.cuioss.portal.restclient;

import static de.cuioss.tools.string.MoreStrings.isEmpty;

import de.cuioss.tools.logging.CuiLogger;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;

/**
 * Client filter that will do token authentication. You must allocate it and
 * then register it with the Client or WebTarget.
 */
@RequiredArgsConstructor
public class BearerTokenAuthFilter implements ClientRequestFilter {

    private static final CuiLogger LOGGER = new CuiLogger(BearerTokenAuthFilter.class);
    private static final String AUTH_HEADER_PREFIX = "Bearer ";

    private final String token;

    @Override
    public void filter(ClientRequestContext requestContext) {
        LOGGER.trace("token: {}", token);
        if (!isEmpty(token)) {
            requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, AUTH_HEADER_PREFIX + token);
        }
    }
}
