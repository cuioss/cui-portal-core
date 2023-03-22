package de.cuioss.portal.restclient;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import de.cuioss.tools.logging.CuiLogger;

/**
 * A {@linkplain ClientResponseFilter} to log the response metadata received by the rest-client.
 * <p>
 * This class is annotated with {@link Priority} with value {@link Integer#MIN_VALUE} to ensure it
 * is the very last filter that is called.
 * <p>
 * This is an abstract class to allow multi-registering via anonymous class.
 */
@Priority(Integer.MIN_VALUE)
abstract class LogClientResponseFilter implements ClientResponseFilter {

    private static final String PATTERN = "-- Client response filter {}--\n" +
            "Status: {}\n" +
            "StatusInfo: {}\n" +
            "Allowed Methods: {}\n" +
            "EntityTag: {}\n" +
            "Cookies: {}\n" +
            "Date: {}\n" +
            "Headers: {}\n" +
            "Language: {}\n" +
            "LastModified: {}\n" +
            "Links: {}\n" +
            "Location: {}\n" +
            "MediaType: {}\n";

    private final CuiLogger log;

    private final String name;

    protected LogClientResponseFilter(final CuiLogger logger) {
        this(logger, "");
    }

    protected LogClientResponseFilter(final CuiLogger logger, final String name) {
        log = logger;
        this.name = "[" + name + "] ";
    }

    @Override
    public void filter(final ClientRequestContext clientRequestContext,
            final ClientResponseContext clientResponseContext)
        throws IOException {
        try {
            log.info(PATTERN, name,
                    clientResponseContext.getStatus(),
                    clientResponseContext.getStatusInfo(),
                    clientResponseContext.getAllowedMethods(),
                    clientResponseContext.getEntityTag(),
                    clientResponseContext.getCookies(),
                    clientResponseContext.getDate(),
                    clientResponseContext.getHeaders(),
                    clientResponseContext.getLanguage(),
                    clientResponseContext.getLastModified(),
                    clientResponseContext.getLinks(),
                    clientResponseContext.getLocation(),
                    clientResponseContext.getMediaType());
        } catch (final Exception e) {
            log.error("Portal-529: Could not trace-log response data", e);
        }
    }
}
