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

import de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.MoreStrings;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Client filter that adds HTTP Basic Authentication to outgoing requests.
 * Credentials are provided through the Portal configuration system using
 * {@link ConfigAsConnectionMetadata}.
 *
 * <p>The filter is automatically configured by {@link CuiRestClientBuilder}
 * when Basic Authentication is specified in the configuration.
 *
 * @see CuiRestClientBuilder
 * @see ConfigAsConnectionMetadata
 */
public class BasicAuthenticationFilter implements ClientRequestFilter {

    private static final CuiLogger LOGGER = new CuiLogger(BasicAuthenticationFilter.class);
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
        LOGGER.trace("Using auth header %s", this.authHeader);
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(AUTHORIZATION, authHeader);
    }
}
