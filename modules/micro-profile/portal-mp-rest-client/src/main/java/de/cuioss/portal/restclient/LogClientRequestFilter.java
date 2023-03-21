package de.cuioss.portal.restclient;

import static de.cuioss.tools.string.MoreStrings.nullToEmpty;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Form;

import de.cuioss.tools.logging.CuiLogger;

/**
 * A {@linkplain ClientRequestFilter} to log the request uri, headers and body sent by the
 * rest-client.
 * It will be the last filter that is called in the filter chain to make sure the logged data is the
 * actual request that
 * is hitting the server.
 * <p>
 * This class is annotated with {@link Priority} with value {@link Integer#MAX_VALUE} to ensure it
 * is the very last
 * filter that is called.
 */
@Priority(Integer.MAX_VALUE)
class LogClientRequestFilter implements ClientRequestFilter {

    private static final String PATTERN = "-- Client request info --\n" +
        "Request URI: {}\n" +
        "Method: {}\n" +
        "Headers: {}\n" +
        "Body: {}\n";

    private final CuiLogger log;

    public LogClientRequestFilter(final CuiLogger logger) {
        this.log = logger;
    }

    @Override
    public void filter(final ClientRequestContext reqContext) throws IOException {
        try {
            final var headers = new StringBuilder();
            reqContext.getStringHeaders()
                .forEach((key, value) -> headers.append(key).append(": ").append(value).append("\n"));

            var body = "";
            if (reqContext.hasEntity()) {
                if (reqContext.getEntity() instanceof Form) {
                    body = ((Form) reqContext.getEntity()).asMap().toString();
                } else {
                    body = reqContext.getEntity().toString();
                }
            }

            log.info(PATTERN,
                reqContext.getUri(),
                nullToEmpty(reqContext.getMethod()),
                headers.toString(),
                body);
        } catch (final Exception e) {
            log.error("Portal-529: Could not trace-log request data", e);
        }
    }
}
