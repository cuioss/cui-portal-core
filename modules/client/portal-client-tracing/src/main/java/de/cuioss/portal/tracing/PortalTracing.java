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

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_ENABLED;
import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_NAME;
import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_SAMPLER_PROBABILITY;
import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_SAMPLER_RATE;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigProperty;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigPropertyOrThrow;
import static de.cuioss.tools.string.MoreStrings.isEmpty;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import brave.Tracer;
import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggageFields;
import brave.baggage.CorrelationScopeConfig.SingleCorrelationField;
import brave.context.log4j2.ThreadContextScopeDecorator;
import brave.handler.SpanHandler;
import brave.http.HttpTracing;
import brave.internal.Nullable;
import brave.internal.baggage.BaggageContext;
import brave.propagation.B3Propagation;
import brave.propagation.CurrentTraceContext;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import brave.sampler.RateLimitingSampler;
import brave.sampler.Sampler;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.tools.logging.CuiLogger;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.brave.ZipkinSpanHandler;

/**
 * Provides producers for tracing aspects.
 * <p>
 * Spans are reported to {@link PortalLogSpanHandler} and all other span
 * handlers discovered via {@link Instance}. Furthermore, {@link B3Propagation}
 * is used. All tracing features are also provided to log4j2 by using
 * {@link ThreadContextScopeDecorator}.
 *
 * <h2>Wrapping multiple spans</h2>
 *
 * <pre>
 * &#x40;Inject
 * Tracing tracing;
 *
 * void method() {
 *     ScopedSpan span = tracing.tracer().startScopedSpan("do-magic");
 *     try {
 *         call1();
 *         call2();
 *         call3();
 *     } catch (Exception e) {
 *         span.error(e);
 *     } finally {
 *         span.finish();
 *     }
 * }
 * </pre>
 *
 * @author Sven Haag
 */
@ApplicationScoped
public class PortalTracing {

    private static final CuiLogger log = new CuiLogger(PortalTracing.class);

    @Inject
    Instance<SpanHandler> spanHandlers;

    /**
     * @return true, if distributed tracing is enabled. defaults to
     *         <code>true</code>.
     * @see de.cuioss.portal.configuration.TracingConfigKeys#PORTAL_TRACING_ENABLED
     */
    public static boolean isEnabled() {
        return resolveConfigPropertyOrThrow(PORTAL_TRACING_ENABLED, Boolean.class);
    }

    /**
     * Creates a new Tracing, e.g. for a client builder.
     *
     * @return new brave tracing instance
     */
    public static Tracing createTracing() {
        return tracingBean();
    }

    /**
     * @param spanReporter produces span reporter. Can be {@code null}.
     * @param sampler      produced sampler or e.g. {@link Sampler#ALWAYS_SAMPLE}.
     *                     Must not be {@code null}!
     *
     * @return newly build brave tracing instance
     */
    @Produces
    @Dependent
    Tracing produceTracing(final Reporter<zipkin2.Span> spanReporter, final Sampler sampler) {
        if (null != Tracing.current()) {
            log.debug("producing Tracing via current tracing: {}", Tracing.current());
            return Tracing.current();
        }

        final var localServiceName = getLocalServiceName();
        final var builder = Tracing.newBuilder();

        if (!isEmpty(localServiceName)) {
            log.debug("Setting localServiceName to: {}", localServiceName);
            builder.localServiceName(localServiceName);
        }

        spanHandlers.forEach(spanHandler -> {
            log.debug("Adding dynamic span handler: {}", spanHandler);
            builder.addSpanHandler(spanHandler);
        });

        if (null != spanReporter) {
            log.debug("Setting span reporter to: {}", spanReporter);
            builder.addSpanHandler(ZipkinSpanHandler.create(spanReporter));
        }

        final CurrentTraceContext ctx = ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(
                // the scope baggage items "spanId" and "traceId" are already added by
                // ThreadContextScopeDecorator per
                // default, but we also need "parentId" and "sampled"
                ThreadContextScopeDecorator.newBuilder() // puts tracing data into MDC aka ThreadContext
                        .add(SingleCorrelationField.create(BaggageFields.PARENT_ID))
                        .add(SingleCorrelationField.create(BaggageFields.SAMPLED)).build())
                .build();

        return builder.sampler(sampler).trackOrphans().propagationFactory(B3Propagation.FACTORY)
                .currentTraceContext(ctx).addSpanHandler(new PortalLogSpanHandler(ctx)).build();
    }

    @Produces
    @Dependent
    Sampler produceSampler() {
        float samplingProbability = resolveConfigPropertyOrThrow(PORTAL_TRACING_SAMPLER_PROBABILITY, Float.class);
        if (samplingProbability > 0.0f) {
            log.debug("Creating sampler with probability: {}", samplingProbability);
            return Sampler.create(samplingProbability);
        }

        final int samplingRate = resolveConfigPropertyOrThrow(PORTAL_TRACING_SAMPLER_RATE, Integer.class);
        log.debug("Creating sampler with sampling rate: {}", samplingRate);
        return RateLimitingSampler.create(samplingRate);
    }

    static final class Sampled extends BaggageContext.ReadOnly {
        @Override
        public String getValue(BaggageField field, TraceContextOrSamplingFlags extracted) {
            return getValue(extracted.sampled());
        }

        @Override
        public String getValue(BaggageField field, TraceContext context) {
            return getValue(context.sampled());
        }

        @Nullable
        static String getValue(@Nullable Boolean sampled) {
            return sampled != null ? sampled.toString() : null;
        }
    }

    @Produces
    @Dependent
    Tracer produceTracer(final Reporter<zipkin2.Span> spanReporter, final Sampler sampler) {
        return produceTracing(spanReporter, sampler).tracer();
    }

    @Produces
    @Dependent
    Reporter<zipkin2.Span> zipkinReporter() {
        return null;
    }

    @Produces
    @Dependent
    HttpTracing produceHttpTracing() {
        return HttpTracing.current();
    }

    /**
     * @param tracing the instance to be closed
     */
    void close(@Disposes final Tracing tracing) {
        tracing.close();
    }

    /**
     * @param connectionMetadata may be null
     *
     * @return connection id or {@link #getServiceName(String)}
     */
    public static String getServiceName(final ConnectionMetadata connectionMetadata) {
        if (null == connectionMetadata) {
            return null;
        }

        if (!isEmpty(connectionMetadata.getConnectionId())) {
            log.debug("using connection id as tracing service name: {}", connectionMetadata.getConnectionId());
            return connectionMetadata.getConnectionId();
        }
        return getServiceName(connectionMetadata.getServiceUrl());
    }

    /**
     * @param url service endpoint
     *
     * @return first part of uri path, host name or <code>null</code>
     */
    public static String getServiceName(final String url) {
        if (isEmpty(url)) {
            return null;
        }

        String serviceName = null;

        try {
            final var uri = new URI(url);
            serviceName = uri.getHost();

            final var path = uri.getPath();
            if ((path.length() > 0) && ('/' == path.charAt(0))) {
                // the path contains a leading slash, but maybe that's it?!
                final var endpointCandidate = path.substring(1).split("/")[0];
                if (!isEmpty(endpointCandidate)) {
                    serviceName = endpointCandidate;
                }
            }
        } catch (final Exception e) {
            log.error(e, "Could not extract hostname or endpoint from URL: {}", url);
        }

        log.debug("using tracing service name: {}", serviceName);
        return serviceName;
    }

    private static String getLocalServiceName() {
        return resolveConfigProperty(PORTAL_TRACING_NAME).orElse(null);
    }

    private static Tracing tracingBean() {
        return CDI.current().select(Tracing.class).get();
    }
}
