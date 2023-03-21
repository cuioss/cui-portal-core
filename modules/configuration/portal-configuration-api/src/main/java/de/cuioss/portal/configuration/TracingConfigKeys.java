package de.cuioss.portal.configuration;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.ENABLED;
import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_BASE;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TracingConfigKeys {

    private static final String PORTAL_TRACING_BASE = PORTAL_BASE + "tracing.";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.tracing.enabled'
     * <p>
     * Used to enable or disable the distributed tracing system for any clients, e.g. DSML, REST, etc.
     * Valid values are: true|false. Default is true.
     * </p>
     */
    public static final String PORTAL_TRACING_ENABLED = PORTAL_TRACING_BASE + ENABLED;

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.tracing.name'
     * <p>
     * Used to set the local service name. Defaults to ${application.context.name}.
     * </p>
     */
    public static final String PORTAL_TRACING_NAME = PORTAL_TRACING_BASE + "name";

    /**
     * URL of the zipkin query server instance.
     * 'portal.tracing.reporter.url'
     */
    public static final String PORTAL_TRACING_REPORTER_URL = PORTAL_TRACING_BASE + "reporter.url";

    /**
     * Context parameter within configuration-subsystem with the name 'portal.tracing.servlet.enabled'.
     * Used to enable or disable the servlet filter for Brave tracing.
     */
    public static final String PORTAL_TRACING_SERVLET_ENABLED = PORTAL_TRACING_BASE + "servlet.enabled";

    /**
     * Probability of requests that should be sampled, ranging from {@code 0.0} to {@code 1.0}.
     * E.g. 1.0 = 100% requests should be sampled.
     * 'portal.tracing.probability'
     */
    public static final String PORTAL_TRACING_SAMPLER_PROBABILITY = PORTAL_TRACING_BASE + "probability";

    /**
     * Sampling rate per second (integer). E.g. for surge protection.
     * 'portal.tracing.rate'
     */
    public static final String PORTAL_TRACING_SAMPLER_RATE = PORTAL_TRACING_BASE + "rate";
}
