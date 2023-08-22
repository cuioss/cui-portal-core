package de.cuioss.portal.configuration.util;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.CONTEXT_PARAM_SEPARATOR;
import static de.cuioss.tools.base.Preconditions.checkArgument;
import static de.cuioss.tools.collect.CollectionLiterals.immutableList;
import static de.cuioss.tools.string.MoreStrings.emptyToNull;
import static de.cuioss.tools.string.MoreStrings.isEmpty;
import static de.cuioss.tools.string.MoreStrings.nullToEmpty;
import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.inject.spi.InjectionPoint;

import org.eclipse.microprofile.config.ConfigProvider;

import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.tools.collect.CollectionLiterals;
import de.cuioss.tools.collect.MapBuilder;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.MoreStrings;
import de.cuioss.tools.string.Splitter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Provides some utilities for interacting with configuration elements
 *
 * @author Oliver Wolff
 */
@UtilityClass
public final class ConfigurationHelper {

    private static final CuiLogger log = new CuiLogger(ConfigurationHelper.class);

    private static final Pattern NON_ALPHANUMERIC_PATTERN = Pattern.compile("[^0-9a-zA-Z]");

    private static final String RESOLVED_ENV_VAR_MSG = "resolved environment property {}={}";

    /**
     * The default property-separator
     */
    public static final String PROPERTY_SEPARATOR = ".";

    /**
     * Helper method that filters a given map of properties according to the given
     * parameter
     *
     * @param properties  to be filtered, must no be null
     * @param prefix      The name to be filtered using
     *                    {@link String#startsWith(String)}
     * @param stripPrefix boolean indicating whether to strip the prefix from the
     *                    keys.
     * @return the filtered map
     */
    public static Map<String, String> getFilteredPropertyMap(final Map<String, String> properties, final String prefix,
            final boolean stripPrefix) {
        final var startsWith = nullToEmpty(prefix);
        final var builder = new MapBuilder<String, String>();

        properties.entrySet().stream().filter(e -> e.getKey().startsWith(startsWith)).forEach(builder::put);

        if (stripPrefix) {
            return stripPrefix(builder.toImmutableMap(), prefix);
        }

        return builder.toImmutableMap();
    }

    /**
     * Removes the given prefix from the corresponding properties of the given map.
     * Ignores properties that don't start with that prefix.
     *
     * @param properties to be sanitized
     * @param prefix     to be stripped from the properties keys
     *
     * @return the given properties map but with keys not containing the given
     *         prefix.
     */
    private Map<String, String> stripPrefix(final Map<String, String> properties, final String prefix) {
        final var prefixSanitized = nullToEmpty(prefix);
        final var strippedBuilder = new MapBuilder<String, String>();

        for (final Map.Entry<String, String> entry : properties.entrySet()) {
            final var key = entry.getKey();
            if (key.startsWith(prefixSanitized)) {
                strippedBuilder.put(key.substring(prefixSanitized.length()), entry.getValue());
            } else {
                strippedBuilder.put(key, entry.getValue());
            }
        }
        return strippedBuilder.toImmutableMap();
    }

    /**
     * @return a map representation of all currently active
     *         configuration-properties. <b>Configuration properties with an empty
     *         string value will NOT be contained in the returned map!</b>
     */
    public static Map<String, String> resolveConfigProperties() {
        final var config = ConfigProvider.getConfig();
        final Map<String, String> resolved = new HashMap<>();
        for (final String key : config.getPropertyNames()) {
            try {
                final Optional<String> value = config.getOptionalValue(key, String.class);
                if (value.isPresent()) {
                    resolved.put(key, value.get());
                } else {
                    log.trace("No value found for key '{}'", key);
                }
            } catch (final NoSuchElementException e) {
                log.trace(e, "Could not resolve config key: {}", key);
            }
        }
        return resolved;
    }

    /**
     * @return an {@link Collection} of all contained names
     */
    public static Collection<String> resolveConfigPropertyNames() {
        return CollectionLiterals.immutableList(ConfigProvider.getConfig().getPropertyNames());
    }

    /**
     * Helper method that filters a given map of properties according to the given
     * parameter
     *
     * @param prefix      The name to be filtered using String.startsWith(String),
     *                    see
     *                    {@link ConfigurationHelper#getFilteredPropertyMap(Map, String, boolean)}
     * @param stripPrefix boolean indicating whether to strip the prefix from the
     *                    keys.
     * @return filtered map
     */
    public static Map<String, String> resolveFilteredConfigProperties(final String prefix, final boolean stripPrefix) {
        final var builder = new MapBuilder<String, String>();

        // first, get all property names with the prefix.
        // this should be faster than resolving all properties with their values.
        final Set<String> keys = resolveConfigPropertyNames().stream().filter(key -> key.startsWith(prefix))
                .collect(Collectors.toSet());

        // now that we have all relevant keys, also resolve their values
        for (final String key : keys) {
            resolveConfigProperty(key).ifPresent(value -> builder.put(key, value));
        }

        if (stripPrefix) {
            return stripPrefix(builder.toImmutableMap(), prefix);
        }

        return builder.toImmutableMap();
    }

    /**
     * Shorthand for accessing
     * {@link #resolveFilteredConfigProperties(String, boolean)} with stripPrefix
     * being set to {@code false}
     *
     * @param prefix The name to be filtered using String.startsWith(String), see
     *               {@link ConfigurationHelper#getFilteredPropertyMap(Map, String, boolean)}
     *
     * @return filtered map
     */
    public static Map<String, String> resolveFilteredConfigProperties(final String prefix) {
        return resolveFilteredConfigProperties(prefix, false);
    }

    /**
     * Resolves a property from the underlying system.
     *
     * @param name of the property to be resolved
     * @return the resolved property if available
     */
    public static Optional<String> resolveConfigProperty(final String name) {
        return resolveConfigProperty(name, String.class);
    }

    /**
     * Resolves a property from the underlying system.
     *
     * @param <T>  The property type
     * @param name of the property to be resolved
     * @param type of the property to be resolved
     * @return the resolved property if available
     */
    public static <T> Optional<T> resolveConfigProperty(final String name, final Class<T> type) {
        if (isEmpty(name)) {
            return Optional.empty();
        }

        try {
            return ConfigProvider.getConfig().getOptionalValue(name, type);
        } catch (final NoSuchElementException e) {
            log.trace(e, "Could not resolve config key: {}", name);
        }
        return Optional.empty();
    }

    /**
     * Resolves a property from the underlying system. This call is useful if you
     * are confident that the property actually exists.
     *
     * @param <T>  The property type
     * @param name of the property to be resolved
     * @param type of the property to be resolved
     * @return the resolved property if available, otherwise it will throw an
     *         {@link IllegalStateException}
     */
    public static <T> T resolveConfigPropertyOrThrow(final String name, final Class<T> type) {
        return resolveConfigProperty(name, type)
                .orElseThrow(() -> new IllegalStateException("No property found for key:" + name));
    }

    /**
     * Resolves a property from the underlying system. This call is useful if you
     * are confident that the property actually exists.
     *
     * @param name of the property to be resolved
     * @return the resolved property if available, otherwise it will throw an
     *         {@link IllegalStateException}
     */
    public static String resolveConfigPropertyOrThrow(final String name) {
        return resolveConfigPropertyOrThrow(name, String.class);
    }

    /**
     * @param injectionPoint identifying the current injection point must not be
     *                       null and be at least an instance of
     *                       {@link AnnotatedElement}
     * @param annotationType must not be null
     * @param <T>            type to be looked up
     * @return annotation instance extracted from the given injection point
     */
    public static <T extends Annotation> Optional<T> resolveAnnotation(final InjectionPoint injectionPoint,
            final Class<T> annotationType) {
        requireNonNull(annotationType, "annotationType must not be null");
        requireNonNull(injectionPoint, "injectionPoint must not be null");
        final var annotatedElement = requireNonNull(injectionPoint.getAnnotated(), "injectionPoint must be annotated");
        // initially taken from
        // org.apache.deltaspike.core.util.BeanUtils.extractAnnotation(Annotated,
        // Class<T>)
        var result = annotatedElement.getAnnotation(annotationType);

        if (null == result) {
            for (final Annotation annotation : annotatedElement.getAnnotations()) {
                result = annotation.annotationType().getAnnotation(annotationType);
                if (null != result) {
                    break;
                }
            }
        }
        return Optional.ofNullable(result);
    }

    /**
     * @param injectionPoint identifying the current injection point must not be
     *                       null and be at least an instance of
     *                       {@link AnnotatedElement}
     * @param annotationType to be resolved. must not be null.
     * @param <T>            annotation type to be looked up
     *
     * @return annotation instance extracted from the given injection point
     * @throws IllegalStateException if the given injection point does not contain
     *                               the annotationType. Its message can be given
     *                               via {@code errorMessage}. errorMessage defaults
     *                               to
     *                               {@code Could not resolve annotation for type: annotationType#getName()}
     */
    public static <T extends Annotation> T resolveAnnotationOrThrow(final InjectionPoint injectionPoint,
            final Class<T> annotationType) {

        return resolveAnnotation(injectionPoint, annotationType).orElseThrow(() -> new IllegalStateException(
                "Portal-530: Could not resolve annotation for type: " + annotationType.getName()));
    }

    /**
     * First it tries to get the config value from system properties. If that fails,
     * it tries against the environment properties. If that fails, it sanitizes the
     * config key by replacing all non-alphanumeric characters with underscore and
     * tries against environment properties. If that fails too, it tries the
     * sanitized upper-case key against environment properties.
     *
     * @param name config key
     *
     * @return the raw config value, if any. <em>May contain unresolved
     *         variables!</em>
     */
    public static Optional<String> resolveConfigPropertyFromSysOrEnv(final String name) {
        final var systemValue = Optional.ofNullable(System.getProperty(name));
        if (systemValue.isPresent()) {
            log.trace("resolved system property {}={}", name, systemValue.get());
            return systemValue;
        }

        final var envProperties = System.getenv();

        if (envProperties.containsKey(name)) {
            final var envValue = envProperties.get(name);
            log.trace(RESOLVED_ENV_VAR_MSG, name, envValue);
            return Optional.of(envValue);
        }

        final var sanitizedName = NON_ALPHANUMERIC_PATTERN.matcher(name).replaceAll("_");

        if (envProperties.containsKey(sanitizedName)) {
            final var envValue = envProperties.get(sanitizedName);
            log.trace(RESOLVED_ENV_VAR_MSG, sanitizedName, envValue);
            return Optional.of(envValue);
        }

        final var sanitizedUppercaseName = sanitizedName.toUpperCase(Locale.ROOT);
        final var envValue = envProperties.get(sanitizedUppercaseName);
        log.trace(RESOLVED_ENV_VAR_MSG, sanitizedUppercaseName, envValue);
        return Optional.ofNullable(envValue);
    }

    /**
     * Convert inputValue to enum of type <code>enumClass</code>.
     *
     * @param inputValue to be converted. Can be <code>null</code>.
     * @param enumClass  target enum type. Must not be <code>null</code>.
     * @return corresponding enum value of type <code>enumClass</code> or
     *         {@link IllegalArgumentException} if the <code>inputValue</code>
     *         cannot be converted.
     */
    <T extends Enum<T>> T convertToEnum(final String inputValue, final Class<T> enumClass) {
        return convertToEnum(inputValue, enumClass, true, null);
    }

    /**
     * Convert inputValue to enum of type <code>enumClass</code>.
     *
     * @param inputValue   to be converted. Can be <code>null</code>.
     * @param enumClass    target enum type. Must not be <code>null</code>.
     * @param defaultValue default enum value to be used, if the
     *                     <code>inputValue</code> cannot be converted.
     * @return corresponding enum value of type <code>enumClass</code> or
     *         <code>defaultValue</code> if the <code>inputValue</code> cannot be
     *         converted.
     */
    <T extends Enum<T>> T convertToEnum(final String inputValue, final Class<T> enumClass, final T defaultValue) {
        return convertToEnum(inputValue, enumClass, false, defaultValue);
    }

    /**
     * Convert inputValue to enum of type <code>enumClass</code>. Using default
     * value on failure, if <code>explode</code> is set to <code>false</code>.
     *
     * @param inputValue            to be converted. Can be <code>null</code>.
     * @param enumClass             target enum type. Must not be <code>null</code>.
     * @param explodeOnInvalidInput indicating if an
     *                              {@link IllegalArgumentException} should be
     *                              thrown if the <code>inputValue</code> cannot be
     *                              converted.
     * @param defaultValue          default enum value to be used, if the
     *                              <code>inputValue</code> cannot be converted.
     *                              Only necessary if <code>explode</code> is set to
     *                              <code>false</code>.
     * @return corresponding enum value of type <code>enumClass</code>.
     */
    <T extends Enum<T>> T convertToEnum(final String inputValue, final Class<T> enumClass,
            final boolean explodeOnInvalidInput, final T defaultValue) {
        requireNonNull(enumClass);
        checkArgument(explodeOnInvalidInput || (null != defaultValue),
                "defaultValue must be present if explodeOnInvalidInput is set to false!");

        T result = null;

        try {
            if (explodeOnInvalidInput && (null == inputValue)) {
                throw new IllegalArgumentException("null value");
            }

            final var value = nullToEmpty(inputValue).trim().toUpperCase();
            if (value.isEmpty()) {
                throw new IllegalArgumentException("empty value");
            }
            result = Enum.valueOf(enumClass, value);
        } catch (final IllegalArgumentException ex) {
            log.error("Portal-512: Could not convert input value '{}' to enum of type: {}. Reason: {}", inputValue,
                    enumClass, ex.getMessage());
            if (explodeOnInvalidInput) {
                throw ex;
            }
        }

        if (null == result) {
            return defaultValue;
        }

        return result;
    }

    /**
     * split values via {@link PortalConfigurationKeys#CONTEXT_PARAM_SEPARATOR}.
     * Does not throw an exception, if the config key is not present!
     *
     * @param name config key
     * @return the list of configProperties
     */
    public List<String> resolveConfigPropertyAsList(@NonNull final String name) {
        return resolveConfigPropertyAsList(name, null);
    }

    /**
     * split values via {@link PortalConfigurationKeys#CONTEXT_PARAM_SEPARATOR}.
     *
     * @param name         config key
     * @param defaultValue string representing default config value. can be null.
     * @return the list of configProperties
     */
    public List<String> resolveConfigPropertyAsList(@NonNull final String name, final String defaultValue) {
        return resolveConfigPropertyAsList(name, defaultValue, CONTEXT_PARAM_SEPARATOR);
    }

    /**
     * /**
     *
     * @param name         config key
     * @param defaultValue string representing default config value. can be null.
     * @param separator    separator between list values
     * @return list with configured values, separated via
     *         {@link PortalConfigurationKeys#CONTEXT_PARAM_SEPARATOR}
     */
    public List<String> resolveConfigPropertyAsList(@NonNull final String name, final String defaultValue,
            final char separator) {
        final var configuredValue = resolveConfigProperty(name).orElse(emptyToNull(defaultValue));
        return immutableList(resolveListFromString(configuredValue, separator));
    }

    /**
     * split and trim values. omit empty strings.
     */
    private static List<String> resolveListFromString(final String configuredValue, final char separator) {

        if (MoreStrings.isEmpty(configuredValue)) {
            return Collections.emptyList();
        }
        return Splitter.on(separator).trimResults().omitEmptyStrings().splitToList(configuredValue);
    }

    /**
     * @param value config key
     * @return config key with separator as last character
     */
    public static String appendPropertySeparator(@NonNull final String value) {
        if (value.endsWith(PROPERTY_SEPARATOR)) {
            return value;
        }
        return value + PROPERTY_SEPARATOR;
    }

    /**
     * Replace all placeholders will their resolved value, if possible. All config
     * properties from the configuration system are used, not only SYS or ENV
     * properties. If a key does not exist, the placeholders default value is used
     * ({@code ${key:default-value}}). If the default value is left empty
     * ({@code ${missing.key:}}), an empty string is used. If there is no default
     * value, either, the placeholder is not replaced at all
     * ({@code exceptionOnMissingKey=false}) or a {@link NoSuchElementException} is
     * thrown ({@code exceptionOnMissingKey=true}).
     *
     * @param source                config map to process, containing placeholder/s
     * @param exceptionOnMissingKey if {@code true} an exception is throws, if the
     *                              given source map contains a placeholder with no
     *                              corresponding key in the ENV or SYS properties.
     * @return the resulting String
     * @throws NoSuchElementException    if {@code exceptionOnMissingKey} is
     *                                   {@code true} and the source map contains a
     *                                   placeholder with no corresponding key in
     *                                   the ENV or SYS properties.
     * @throws ConfigKeyNestingException if a placeholder contains a placeholder as
     *                                   its default value for more than 5 times.
     */
    public static String replacePlaceholders(final String source, boolean exceptionOnMissingKey) {
        return ConfigurationPlaceholderHelper.replacePlaceholders(source, exceptionOnMissingKey);
    }
}
