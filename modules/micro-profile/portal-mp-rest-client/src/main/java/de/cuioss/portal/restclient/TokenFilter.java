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

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import de.cuioss.portal.configuration.connections.TokenResolver;
import lombok.RequiredArgsConstructor;

/**
 * Client filter that will do token authentication. You must allocate it and
 * then register it with the Client or WebTarget.
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
