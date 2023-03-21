package de.cuioss.portal.restclient;

import javax.annotation.Priority;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import de.cuioss.tools.logging.CuiLogger;

/**
 * Runs before {@link LogReaderInterceptor} because it has a lower {@link Priority}.
 */
@Priority(42)
public class LogClientRequestFilter42 implements ClientRequestFilter {

    private static final CuiLogger LOGGER = new CuiLogger(LogClientRequestFilter42.class);

    @Override
    public void filter(final ClientRequestContext reqContext) {
        LOGGER.trace("-- LogClientRequestFilter42 --");
    }
}
