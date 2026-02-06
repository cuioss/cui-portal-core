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

import de.cuioss.tools.logging.CuiLogger;
import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;

import java.io.IOException;

/**
 * Client filter that logs incoming REST client responses.
 * Provides detailed logging of response details including:
 * <ul>
 *   <li>Response status and status info</li>
 *   <li>Headers</li>
 * </ul>
 *
 * <p>The filter is automatically configured by {@link CuiRestClientBuilder}
 * and can be controlled through the Portal logging configuration.
 *
 * @see LogClientRequestFilter
 * @see LogReaderInterceptor
 * @see CuiRestClientBuilder
 */
@Priority(Integer.MIN_VALUE)
abstract class LogClientResponseFilter implements ClientResponseFilter {

    private static final CuiLogger LOGGER;
    private final String name;

    protected LogClientResponseFilter(final CuiLogger givenLogger) {
        this(givenLogger, "");
    }

    protected LogClientResponseFilter(final CuiLogger givenLogger, final String name) {
        this.givenLogger = givenLogger;
        this.name = "[" + name + "] ";
    }

    @Override
    public void filter(final ClientRequestContext clientRequestContext,
            final ClientResponseContext clientResponseContext) throws IOException {
        try {
            LOGGER.info(RestClientLogMessages.INFO.RESPONSE_INFO, name, clientResponseContext.getStatus(),
                    clientResponseContext.getStatusInfo(), clientResponseContext.getAllowedMethods(),
                    clientResponseContext.getEntityTag(), clientResponseContext.getCookies(),
                    clientResponseContext.getDate(), clientResponseContext.getHeaders(),
                    clientResponseContext.getLanguage(), clientResponseContext.getLastModified(),
                    clientResponseContext.getLinks(), clientResponseContext.getLocation(),
                    clientResponseContext.getMediaType());
        } /*~~(TODO: Catch specific not Exception. Suppress: // cui-rewrite:disable InvalidExceptionUsageRecipe)~~>*/catch (final Exception e) {
            LOGGER.error(e, RestClientLogMessages.ERROR.TRACE_LOG_ERROR);
        }
    }
}
