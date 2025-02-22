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

import de.cuioss.tools.logging.CuiLogger;
import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Form;

import java.io.IOException;

import static de.cuioss.tools.string.MoreStrings.nullToEmpty;

/**
 * Client filter that logs outgoing REST client requests.
 * Provides detailed logging of request details including:
 * <ul>
 *   <li>Request URI and method</li>
 *   <li>Headers (with sensitive information)</li>
 *   <li>Request body (if enabled)</li>
 * </ul>
 *
 * <p>The filter is automatically configured by {@link CuiRestClientBuilder}
 * and can be controlled through the Portal logging configuration.
 *
 * @see LogClientResponseFilter
 * @see LogReaderInterceptor
 * @see CuiRestClientBuilder
 */
@Priority(Integer.MAX_VALUE)
class LogClientRequestFilter implements ClientRequestFilter {

    private static final String PATTERN = """
            -- Client request info --
            Request URI: %s
            Method: %s
            Headers: %s
            Body: %s
            """;

    private final CuiLogger givenLogger;

    public LogClientRequestFilter(final CuiLogger givenLogger) {
        this.givenLogger = givenLogger;
    }

    @Override
    public void filter(final ClientRequestContext reqContext) throws IOException {
        try {
            final var headers = new StringBuilder();
            reqContext.getStringHeaders()
                    .forEach((key, value) -> headers.append(key).append(": ").append(value).append("\n"));

            var body = "";
            if (reqContext.hasEntity()) {
                if (reqContext.getEntity() instanceof Form form) {
                    body = form.asMap().toString();
                } else {
                    body = reqContext.getEntity().toString();
                }
            }

            givenLogger.info(PATTERN, reqContext.getUri(), nullToEmpty(reqContext.getMethod()), headers.toString(), body);
        } catch (final Exception e) {
            givenLogger.error(e, RestClientLogMessages.ERROR.TRACE_LOG_ERROR.format(), e);
        }
    }
}
