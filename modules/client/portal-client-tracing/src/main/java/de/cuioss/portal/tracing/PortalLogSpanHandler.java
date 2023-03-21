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
            Arrays.asList(LoggingStrategies.values())
                .stream()
                .anyMatch(handler -> handler.doLogHandled(span));
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
                if (requestPathContains(span, "/status")
                    || requestPathContains(span, "/health")
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



