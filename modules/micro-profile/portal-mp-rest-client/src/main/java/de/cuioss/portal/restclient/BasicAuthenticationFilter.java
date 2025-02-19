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

import de.cuioss.tools.string.MoreStrings;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * A filter for adding Basic Authentication to REST client requests.
 * Adds an Authorization header with the Basic authentication scheme.
 */
public class BasicAuthenticationFilter implements ClientRequestFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC_PREFIX = "Basic ";

    private final String authHeader;

    /**
     * Constructs a new BasicAuthenticationFilter with the given credentials.
     *
     * @param username the username for authentication, must not be null
     * @param password the password for authentication, must not be null
     * @throws IllegalArgumentException if username or password is null
     */
    public BasicAuthenticationFilter(String username, String password) {
        MoreStrings.requireNotEmpty(username, "username");
        MoreStrings.requireNotEmpty(password, "password");
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        this.authHeader = BASIC_PREFIX + new String(encodedAuth, StandardCharsets.UTF_8);
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(AUTHORIZATION, authHeader);
    }
}
