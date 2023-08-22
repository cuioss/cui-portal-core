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
package de.cuioss.portal.tracing;

import java.util.Arrays;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import de.cuioss.tools.logging.CuiLogger;
import lombok.RequiredArgsConstructor;

/**
 * Log spans with error on error-log, everything else on info-log.
 *
 * @author Sven Haag
 */
@RequiredArgsConstructor
final class PortalLogSpanHandler extends SpanHandler {

    private static final CuiLogger log = new CuiLogger(PortalLogSpanHandler.class);

    final CurrentTraceContext currentTraceContext;

    @Override
    public boolean end(final TraceContext context, final MutableSpan span, final Cause cause) {
        // doing maybeScope ensures that the MDC contains the correct tracing data
        try (final var scope = currentTraceContext.maybeScope(context)) {
            var result = Arrays.asList(LoggingStrategies.values()).stream()
                    .anyMatch(handler -> handler.doLogHandled(span));
            if (!result) {
                log.debug("Scope '%s' could not be handled", scope);
            }
        }

        // keep the span alive for other span handlers
        return true;
    }

    @Override
    public String toString() {
        return PortalLogSpanHandler.class.getSimpleName() + "{name=" + log.getName() + "}";
    }

    interface LoggingStrategy {

        boolean doLogHandled(MutableSpan span);
    }

    enum LoggingStrategies implements LoggingStrategy {

        HANDLE_ERRORS {

            @Override
            public boolean doLogHandled(MutableSpan span) {
                if (LoggingStrategies.containsError(span)) {
                    log.error(span.toString(), span.error());
                    return true;
                }
                return false;
            }
        },
        HANDLE_PAGES {

            @Override
            public boolean doLogHandled(MutableSpan span) {
                if (requestPathContains(span, "/faces/")) {
                    // report each page request
                    log.debug(span.toString());
                    return true;
                }
                return false;
            }
        },
        HANDLE_RESOURCES {

            @Override
            public boolean doLogHandled(MutableSpan span) {
                // nothing to do
                return requestPathContains(span, "/javax.faces.resource/");
            }
        },
        HADNLE_METRIC_ENDPOINTS {

            @Override
            public boolean doLogHandled(MutableSpan span) {
                if (requestPathContains(span, "/status") || requestPathContains(span, "/health")
                        || requestPathContains(span, "/metrics")) {
                    log.trace(span.toString());
                    return true;
                }
                return false;
            }
        },
        DEFAULT_HANDLING {

            @Override
            public boolean doLogHandled(MutableSpan span) {
                log.info(span.toString());
                return true;
            }
        };

        private static boolean containsError(final MutableSpan span) {
            return null != span.error() || span.tags().containsKey("error");
        }

        private static boolean requestPathContains(final MutableSpan span, final String value) {
            final var requestPath = span.tag("http.path");
            return null != requestPath && requestPath.contains(value);
        }
    }
}
