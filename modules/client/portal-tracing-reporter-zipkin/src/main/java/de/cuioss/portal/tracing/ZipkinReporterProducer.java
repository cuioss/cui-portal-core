package de.cuioss.portal.tracing;

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_REPORTER_URL;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.cuioss.tools.logging.CuiLogger;
import zipkin2.Span;
import zipkin2.codec.Encoding;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.urlconnection.URLConnectionSender;

/**
 * Produces a span reporter that sends the span data to zipkin.
 *
 * @author Sven Haag
 */
public class ZipkinReporterProducer {

    private static final CuiLogger LOGGER = new CuiLogger(ZipkinReporterProducer.class);

    /**
     * Spans are reported to java.util.logging per default
     * (https://github.com/openzipkin/brave/tree/master/brave#setup)
     * <p>
     * Reports spans to a Zipkin server, e.g.: http://zipkin:9411/api/v2/spans
     *
     * @return AsyncReporter
     */
    @Produces
    @Dependent
    @Alternative
    static Reporter<Span> zipkinReporter(
            @ConfigProperty(name = PORTAL_TRACING_REPORTER_URL) final Optional<String> url) {

        if (!url.isPresent()) {
            LOGGER.debug("Zipkin reporting disabled due to missing value for configuration key: "
                    + PORTAL_TRACING_REPORTER_URL);
            return null;
        }

        LOGGER.info("Span reporting enabled. URL: {}", url.get());
        final Sender sender = URLConnectionSender.newBuilder()
                .endpoint(url.get())
                .encoding(Encoding.JSON)
                .build();

        return AsyncReporter.builder(sender)
                // TODO .metrics()
                .build();
    }
}
