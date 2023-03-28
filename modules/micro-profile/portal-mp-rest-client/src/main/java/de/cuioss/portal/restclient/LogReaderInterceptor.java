package de.cuioss.portal.restclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import de.cuioss.tools.io.IOStreams;
import de.cuioss.tools.logging.CuiLogger;

/**
 * <p>
 * A {@link ReaderInterceptor} to log the response headers and body received from the server using
 * {@link CuiLogger#trace}.
 * </p>
 * <p>
 * To enable logging set package <code>de.cuioss.portal.core.restclient</code> to
 * <code>TRACE</code> in your logger configuration.
 * </p>
 * <p>
 * This is a {@link ReaderInterceptor} instead of a {@link ClientResponseFilter}, because
 * ReaderInterceptors are
 * executed
 * after ClientResponseFilters. For logging we are interested in the data that is actually coming
 * from the server
 * before hitting any subsequent processing. Furthermore, this class is annotated with
 * {@link Priority} with value
 * {@link Integer#MIN_VALUE} to ensure it is the very first reader interceptor that is called.
 * <p>
 * <p>
 * This interceptor is executed when the client runs {@link Response#readEntity}.
 * </p>
 */
@Priority(Integer.MIN_VALUE)
class LogReaderInterceptor implements ReaderInterceptor {

    private static final String LINE_BREAK = "\n";

    private final CuiLogger log;

    public LogReaderInterceptor(final CuiLogger logger) {
        this.log = logger;
    }

    @Override
    public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
        try {
            final var logMsg = new StringBuilder();

            logMsg.append("-- Client response info --").append(LINE_BREAK);
            logMsg.append("MediaType: ").append(context.getMediaType()).append(LINE_BREAK);
            logMsg.append("GenericType: ").append(context.getGenericType()).append(LINE_BREAK);
            logProperties(logMsg, context);
            logHeaders(logMsg, context);
            logBody(logMsg, context);

            log.info(logMsg.toString());
        } catch (final Exception e) {
            log.error("Portal-529: Could not trace-log response data", e);
        }
        return context.proceed();
    }

    private void logBody(StringBuilder logMsg, final ReaderInterceptorContext context) throws IOException {
        final String body;
        try (final var inputStream = context.getInputStream()) {
            body = IOStreams.toString(inputStream, StandardCharsets.UTF_8);
        }

        logMsg.append("Body:")
            .append(LINE_BREAK)
            .append(body);

        // put the read body back into the response
        context.setInputStream(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
    }

    private void logHeaders(StringBuilder logMsg, final ReaderInterceptorContext context) {
        logMsg.append("Headers:").append(LINE_BREAK);
        context.getHeaders().forEach((key, value) -> logHeader(logMsg, key, value));
    }

    private void logHeader(StringBuilder logMsg, final String key, final List<String> value) {
        logMsg.append(key).append(": ").append(value).append(LINE_BREAK);
    }

    private void logProperties(StringBuilder logMsg, final ReaderInterceptorContext context) {
        logMsg.append("Properties:").append(LINE_BREAK);
        for (final String name : context.getPropertyNames()) {
            logMsg.append(name)
                .append(": ")
                .append(context.getProperty(name))
                .append(LINE_BREAK);
        }
    }
}