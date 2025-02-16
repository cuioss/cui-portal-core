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
package de.cuioss.portal.metrics.utils;

import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigProperty;
import static de.cuioss.tools.collect.CollectionLiterals.immutableList;

import de.cuioss.portal.configuration.MetricsConfigKeys;
import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.portal.metrics.PortalMetricsLogMessages;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.ws.rs.WebApplicationException;
import lombok.experimental.UtilityClass;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.Tag;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

/**
 * Provides some convenience methods for metric related data
 *
 */
@UtilityClass
public class MetricsUtils {

    private static final CuiLogger LOGGER = new CuiLogger(MetricsUtils.class);

    private static final Function<Throwable, Tag> CLASSNAME_EXCEPTION_TAG_MAPPER = cause -> null != cause
            ? new Tag("cause", cause.getClass().getName())
            : null;

    private static final Function<Throwable, Tag> WEB_APPLICATION_EXCEPTION_TAG_MAPPER = cause -> cause instanceof WebApplicationException wae
            ? createHttpStatusCodeTag(wae.getResponse())
            : null;

    private static String metricsAppName;
    private static Tag metricsAppTag;

    /**
     * @return this applications name for the metrics Tag {@code _app}.
     */
    public static String getAppName() {
        if (null == metricsAppName) {
            LOGGER.debug("Resolving metrics application name");
            metricsAppName = resolveConfigProperty(MetricsConfigKeys.MP_METRICS_APP_NAME)
                    .orElseGet(() -> resolveConfigProperty(MetricsConfigKeys.PORTAL_METRICS_APP_NAME)
                            .orElseGet(() -> resolveConfigProperty(PortalConfigurationKeys.APPLICATION_CONTEXT_NAME)
                                    .orElseThrow(() -> new NoSuchElementException(
                                            "Invalid config. Missing 'mp.metrics.appName' or 'portal.metrics.appName'"))));
            LOGGER.info(() -> PortalMetricsLogMessages.METRICS_APP_NAME.format(metricsAppName));
        }
        return metricsAppName;
    }

    /**
     * @return Tag with key {@code _app} and value {@link #getAppName()}.
     */
    public static Tag getAppTag() {
        if (null == metricsAppTag) {
            LOGGER.debug("Creating application tag");
            metricsAppTag = new Tag("_app", getAppName());
            LOGGER.debug("Created application tag: %s=%s", metricsAppTag.getTagName(), metricsAppTag.getTagValue());
        }
        return metricsAppTag;
    }

    /**
     * @param name metric name
     * @param tags to be added
     * @return MetricID with {@code _app}-Tag and tags given in {@code tags}.
     */
    public static MetricID createMetricId(final String name, final Tag... tags) {
        LOGGER.debug("Creating metric ID for name '%s' with %s tags", name, tags != null ? tags.length : 0);
        return createMetricId(name, null, tags);
    }

    /**
     * @param name      of the metric
     * @param exception to be converted into Tag
     * @param tags      additional Tags
     * @return MetricID with {@code _app}-Tag and tags given in {@code tags}, plus a
     *         Tag for the exception class.
     */
    public static MetricID createMetricId(final String name, final Throwable exception, final Tag... tags) {
        LOGGER.debug("Creating metric ID for name '%s' with exception and %s tags", name, tags != null ? tags.length : 0);
        return createMetricIdBuilder(name, exception, immutableList(tags), null).build();
    }

    /**
     * @param name                of the metric
     * @param exception           to be processed into Tags
     * @param tags                additional Tags
     * @param exceptionTagMappers additional exception-to-Tag mapper functions
     * @return MetricID with additional Tags depending on the given exception.
     */
    public static MetricIdBuilder createMetricIdBuilder(final String name, final Throwable exception,
            final Collection<Tag> tags, final Collection<Function<Throwable, Tag>> exceptionTagMappers) {

        LOGGER.debug("Building metric ID for name '%s'", name);
        final var idBuilder = new MetricIdBuilder().name(name).tag(getAppTag()).exception(exception)
                .exceptionTagMapper(CLASSNAME_EXCEPTION_TAG_MAPPER)
                .exceptionTagMapper(WEB_APPLICATION_EXCEPTION_TAG_MAPPER);

        Optional.ofNullable(tags).ifPresent(t -> {
            LOGGER.debug("Adding %s tags to metric ID", t.size());
            t.forEach(idBuilder::tag);
        });

        Optional.ofNullable(exceptionTagMappers).ifPresent(mappers -> {
            LOGGER.debug("Adding %s exception tag mappers to metric ID", mappers.size());
            mappers.forEach(idBuilder::exceptionTagMapper);
        });

        return idBuilder;
    }

    /**
     * @param response providing the Status Code
     * @return the resulting {@link Tag}
     */
    public static Tag createHttpStatusCodeTag(final jakarta.ws.rs.core.Response response) {
        return Optional.ofNullable(response)
                .map(jakarta.ws.rs.core.Response::getStatus)
                .map(status -> new Tag("http_status", String.valueOf(status)))
                .orElse(null);
    }
}
