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

import de.cuioss.tools.io.IOStreams;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Reader interceptor that logs message bodies in REST client communication.
 * Provides detailed logging of request and response bodies while preserving
 * the original content.
 *
 * <p>The interceptor is automatically configured by {@link CuiRestClientBuilder}
 * and can be controlled through the Portal logging configuration.
 *
 * @see LogClientRequestFilter
 * @see LogClientResponseFilter
 * @see CuiRestClientBuilder
 */
@Priority(Integer.MIN_VALUE)
// cui-rewrite:disable CuiLoggerStandardsRecipe
class LogReaderInterceptor implements ReaderInterceptor {

    private static final String LINE_BREAK = "\n";

    // cui-rewrite:disable CuiLoggerStandardsRecipe
    private final CuiLogger givenLogger;

    public LogReaderInterceptor(final CuiLogger givenLogger) {
        this.givenLogger = givenLogger;
    }

    @Override
    public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
        try {
            final var logMsg = new StringBuilder();

            logMsg.append("-- Client response info --").append(LINE_BREAK);
            logMsg.append("MediaType: ").append(context.getMediaType()).append(LINE_BREAK);
            logMsg.append("GenericType: ").append(context.getGenericType()).append(LINE_BREAK);
            appendProperties(logMsg, context);
            appendHeaders(logMsg, context);
            logBody(logMsg, context);

            givenLogger.info(logMsg.toString()); // cui-rewrite:disable CuiLogRecordPatternRecipe
        } catch (final RuntimeException e) { // cui-rewrite:disable InvalidExceptionUsageRecipe
            givenLogger.error(e, RestClientLogMessages.ERROR.TRACE_LOG_ERROR);
        }
        return context.proceed();
    }

    private void logBody(StringBuilder logMsg, final ReaderInterceptorContext context) throws IOException {
        final String body;
        try (final var inputStream = context.getInputStream()) {
            body = IOStreams.toString(inputStream, StandardCharsets.UTF_8);
        }

        logMsg.append("Body:").append(LINE_BREAK).append(body);

        // put the read body back into the response
        context.setInputStream(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
    }

    private void appendHeaders(StringBuilder logMsg, final ReaderInterceptorContext context) {
        logMsg.append("Headers:").append(LINE_BREAK);
        context.getHeaders().forEach((key, value) -> appendHeader(logMsg, key, value));
    }

    private void appendHeader(StringBuilder logMsg, final String key, final List<String> value) {
        logMsg.append(key).append(": ").append(value).append(LINE_BREAK);
    }

    private void appendProperties(StringBuilder logMsg, final ReaderInterceptorContext context) {
        logMsg.append("Properties:").append(LINE_BREAK);
        for (final String name : context.getPropertyNames()) {
            logMsg.append(name).append(": ").append(context.getProperty(name)).append(LINE_BREAK);
        }
    }
}
