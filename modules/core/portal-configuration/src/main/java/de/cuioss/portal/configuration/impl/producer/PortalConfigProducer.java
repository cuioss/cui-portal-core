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
package de.cuioss.portal.configuration.impl.producer;

import static de.cuioss.portal.configuration.MetricsConfigKeys.PORTAL_METRICS_ENABLED;
import static de.cuioss.portal.configuration.cache.CacheConfig.*;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.*;
import static de.cuioss.portal.configuration.PortalConfigurationMessages.*;
import static de.cuioss.tools.base.BooleanOperations.isValidBoolean;
import static de.cuioss.tools.base.Preconditions.checkArgument;
import static de.cuioss.tools.collect.CollectionLiterals.immutableList;
import static de.cuioss.tools.collect.CollectionLiterals.immutableSet;
import static de.cuioss.tools.string.MoreStrings.*;

import de.cuioss.portal.configuration.cache.CacheConfig;
import de.cuioss.portal.configuration.types.*;
import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.FileLoaderUtility;
import de.cuioss.tools.lang.LocaleUtils;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.MoreStrings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Provides specific producer methods for elements not covered by the standard
 * configuration converter.
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
public class PortalConfigProducer {

    private static final CuiLogger LOGGER = new CuiLogger(PortalConfigProducer.class);

    static final String UNUSED = "unused";

    private static final String INVALID_CONTENT_FOR_LONG = ERROR.INVALID_CONTENT_FOR_LONG.getTemplate();
    private static final String INVALID_CONTENT_FOR_TIME_UNIT = ERROR.INVALID_CONTENT_FOR_TIME_UNIT.getTemplate();
    private static final String INVALID_CONTENT_FOR_BOOLEAN = ERROR.INVALID_CONTENT_FOR_BOOLEAN.getTemplate();

    @Inject
    @ConfigProperty(name = PORTAL_METRICS_ENABLED, defaultValue = "false")
    Provider<Boolean> portalMetricsEnabled;

    /**
     * @param injectionPoint providing the field-metadata
     * @return the resolved value or {@code null} if the key could not be found or
     * the resolved value is an empty {@link String}.
     */
    @Produces
    @Dependent
    @ConfigPropertyNullable(name = UNUSED)
    String produceNullableStringConfigProperty(final InjectionPoint injectionPoint) {
        final var metaData = resolveAnnotationOrThrow(injectionPoint, ConfigPropertyNullable.class);
        final var key = metaData.name();
        try {
            return resolveConfigProperty(key).orElse(emptyToNull(metaData.defaultValue()));
        } catch (final NoSuchElementException e) {
            LOGGER.debug(e, DEBUG.CONFIG_VALUE_NOT_FOUND.getTemplate(), key);
        }
        return null;
    }

    /**
     * @param injectionPoint providing the field-metadata
     * @return list with trimmed strings and removed null values
     */
    @Produces
    @Dependent
    @ConfigAsList(name = UNUSED)
    List<String> produceSplittedList(final InjectionPoint injectionPoint) {
        final var metaData = resolveAnnotationOrThrow(injectionPoint, ConfigAsList.class);
        return resolveConfigPropertyAsList(metaData.name(), metaData.defaultValue(), metaData.separator());
    }

    /**
     * @param injectionPoint providing the field-metadata
     * @return set with trimmed strings and removed null values
     */
    @Produces
    @Dependent
    @ConfigAsSet(name = UNUSED)
    Set<String> produceSplittedSet(final InjectionPoint injectionPoint) {
        final var metaData = resolveAnnotationOrThrow(injectionPoint, ConfigAsSet.class);
        return immutableSet(
                resolveConfigPropertyAsList(metaData.name(), metaData.defaultValue(), metaData.separator()));
    }

    @Produces
    @Dependent
    @ConfigAsFileLoader(name = UNUSED)
    FileLoader produceFileLoader(final InjectionPoint injectionPoint) {
        final var metaData = resolveAnnotationOrThrow(injectionPoint, ConfigAsFileLoader.class);
        final var configValue = resolveConfigProperty(metaData.name()).orElse(null);
        return checkFileLoader(configValue, metaData.failOnNotAccessible(), metaData.name());
    }

    @Produces
    @Dependent
    @ConfigAsPath(name = UNUSED)
    Path producePath(final InjectionPoint injectionPoint) {
        final var metaData = resolveAnnotationOrThrow(injectionPoint, ConfigAsPath.class);
        final var configValue = resolveConfigProperty(metaData.name()).orElse(null);
        final var pathAsString = nullToEmpty(configValue).trim();
        if (MoreStrings.isEmpty(pathAsString)) {
            throw new IllegalArgumentException("Path must not be null nor empty, property is " + metaData.name());
        }

        final var path = Path.of(pathAsString).normalize();
        if (metaData.failOnNotAccessible()) {
            checkArgument(path.toFile().exists(),
                    "Path " + pathAsString + " does not denote an existing file/directory");
        }
        return path;
    }

    @Produces
    @Dependent
    @ConfigAsPath(name = UNUSED)
    File produceFile(final InjectionPoint injectionPoint) {
        return producePath(injectionPoint).toFile();
    }

    @Produces
    @Dependent
    @ConfigAsLocale(name = UNUSED)
    Locale produceLocale(final InjectionPoint injectionPoint) {
        final var metaData = resolveAnnotationOrThrow(injectionPoint, ConfigAsLocale.class);
        final var configValue = resolveConfigProperty(metaData.name()).orElse(null);
        final var localeAsString = nullToEmpty(configValue).trim();
        if (MoreStrings.isEmpty(localeAsString)) {
            if (metaData.defaultToSystem()) {
                return Locale.getDefault();
            }
            throw new IllegalArgumentException("Locale must not be null nor empty, property is " + metaData.name());
        }
        return resolveLocale(localeAsString, metaData.defaultToSystem());
    }

    @Produces
    @Dependent
    @ConfigAsLocaleList(name = UNUSED)
    List<Locale> produceLocaleAsList(final InjectionPoint injectionPoint) {
        final var metaData = resolveAnnotationOrThrow(injectionPoint, ConfigAsLocaleList.class);
        final var resultBuilder = new CollectionBuilder<Locale>();
        final var locales = resolveConfigPropertyAsList(metaData.name(), null, metaData.separator());
        for (final String localeString : locales) {
            final var locale = resolveLocale(localeString, metaData.defaultToSystem());
            if (!resultBuilder.contains(locale)) {
                resultBuilder.add(locale);
            }
        }
        return immutableList(resultBuilder);
    }

    @Produces
    @Dependent
    @ConfigAsFileLoaderList(name = UNUSED)
    List<FileLoader> produceFileLoaderList(final InjectionPoint injectionPoint) {
        final var metaData = resolveAnnotationOrThrow(injectionPoint, ConfigAsFileLoaderList.class);
        final var builder = new CollectionBuilder<FileLoader>();
        final var paths = resolveConfigPropertyAsList(metaData.name(), null, metaData.separator());
        for (final String path : paths) {
            builder.add(checkFileLoader(path, metaData.failOnNotAccessible(), metaData.name()));
        }
        return builder.toImmutableList();
    }

    /**
     * Producer for filtered Maps, see {@link ConfigAsFilteredMap}
     *
     * @param injectionPoint providing the field-metadata
     * @return a filtered map of properties filtered corresponding to
     * {@link ConfigAsFilteredMap#startsWith()}
     */
    @Produces
    @Dependent
    @ConfigAsFilteredMap(startsWith = PortalConfigProducer.UNUSED)
    Map<String, String> produceFilteredMap(final InjectionPoint injectionPoint) {
        final var meta = resolveAnnotationOrThrow(injectionPoint, ConfigAsFilteredMap.class);
        return resolveFilteredConfigProperties(meta.startsWith(), meta.stripPrefix());
    }

    @Produces
    @Dependent
    @ConfigAsCacheConfig(name = PortalConfigProducer.UNUSED)
    CacheConfig produceCacheConfig(final InjectionPoint injectionPoint) {

        final var meta = resolveAnnotationOrThrow(injectionPoint, ConfigAsCacheConfig.class);
        final var configKeyPrefix = appendPropertySeparator(requireNotEmptyTrimmed(meta.name(), "name"));
        final var configProperties = resolveFilteredConfigProperties(configKeyPrefix, true);

        var expiration = meta.defaultExpiration();
        var size = meta.defaultSize();
        var timeUnit = meta.defaultTimeUnit();
        var recordStats = meta.recordStatistics();

        LOGGER.trace(TRACE.CONFIG_PROPERTIES.getTemplate(), configKeyPrefix, configProperties);

        if (configProperties.containsKey(EXPIRATION_KEY)) {
            try {
                expiration = Long.parseLong(configProperties.get(EXPIRATION_KEY).trim());
            } catch (final NumberFormatException e) {
                LOGGER.error(e, INVALID_CONTENT_FOR_LONG, configKeyPrefix, EXPIRATION_KEY,
                        configProperties.get(EXPIRATION_KEY));
            }
        }

        if (configProperties.containsKey(SIZE_KEY)) {
            try {
                size = Long.parseLong(configProperties.get(SIZE_KEY).trim());
            } catch (final NumberFormatException e) {
                LOGGER.error(e, INVALID_CONTENT_FOR_LONG, configKeyPrefix, SIZE_KEY, configProperties.get(SIZE_KEY));
            }
        }

        if (configProperties.containsKey(EXPIRATION_UNIT_KEY)) {
            try {
                timeUnit = TimeUnit.valueOf(configProperties.get(EXPIRATION_UNIT_KEY).trim().toUpperCase());
            } catch (final IllegalArgumentException e) {
                LOGGER.error(e, INVALID_CONTENT_FOR_TIME_UNIT, configKeyPrefix, EXPIRATION_UNIT_KEY, TimeUnit.values(),
                        configProperties.get(EXPIRATION_UNIT_KEY));
            }
        }

        if (configProperties.containsKey(CacheConfig.RECORD_STATISTICS_KEY)) {
            final var configValue = configProperties.get(CacheConfig.RECORD_STATISTICS_KEY).trim();
            if (!isValidBoolean(configValue)) {
                LOGGER.error(INVALID_CONTENT_FOR_BOOLEAN, configKeyPrefix, CacheConfig.RECORD_STATISTICS_KEY,
                        configProperties.get(CacheConfig.RECORD_STATISTICS_KEY));
                recordStats = false;
            } else {
                recordStats = Boolean.parseBoolean(configValue);
            }
            LOGGER.trace(TRACE.RECORD_STATS.getTemplate(), recordStats);
        }

        final var cacheConfig = new CacheConfig(expiration, timeUnit, size, recordStats && portalMetricsEnabled.get());
        LOGGER.trace(TRACE.CACHE_CONFIG.getTemplate(), cacheConfig);
        return cacheConfig;
    }

    private static Locale resolveLocale(final String localeAsString, final boolean defaultToSystem) {
        Locale locale = null;
        try {
            locale = LocaleUtils.toLocale(localeAsString);
        } catch (final IllegalArgumentException e) {
            LOGGER.warn(WARN.INVALID_LOCALE.getTemplate(), localeAsString, e);
        }
        if (null != locale) {
            return locale;
        }
        if (defaultToSystem) {
            return Locale.getDefault();
        }
        throw new IllegalArgumentException("Unable to determine locale for " + localeAsString);
    }

    private static FileLoader checkFileLoader(final String pathProperty, final boolean failOnNotAccessible,
            final String propertyName) {

        final var path = nullToEmpty(pathProperty).trim();
        if (MoreStrings.isEmpty(path)) {
            throw new IllegalArgumentException("Path must not be null nor empty, property is " + propertyName);
        }

        final var fileLoader = FileLoaderUtility.getLoaderForPath(path);
        if (failOnNotAccessible && !fileLoader.isReadable()) {
            throw new IllegalArgumentException("Unable to access file " + path);
        }
        return fileLoader;
    }
}
