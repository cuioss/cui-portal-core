package de.cuioss.portal.configuration.yaml;

import static de.cuioss.tools.base.Preconditions.checkArgument;
import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static de.cuioss.tools.string.MoreStrings.isEmpty;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.reader.UnicodeReader;

import de.cuioss.portal.configuration.ConfigurationSource;
import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.tools.collect.MapBuilder;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.FileLoaderUtility;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.MoreStrings;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * Properties provider for YAML files.
 * <p>
 * Some code is copied from:
 * https://github.com/spring-projects/spring-framework/blob/v5.1.8.RELEASE
 * /spring-beans/src/main/java/org/springframework/beans/factory/config/YamlPropertiesFactoryBean.java
 * /spring-beans/src/main/java/org/springframework/beans/factory/config/YamlProcessor.java
 *
 * @author Sven Haag
 */
@EqualsAndHashCode
@ToString
public class YamlConfigurationProvider implements ConfigurationSource {

    private static final String MSG_LOAD_ERROR = "Portal-519: Unable to load configuration file: {}, due to: {}";

    private static final String MSG_READ_ERROR = "The referenced file can not be loaded: ";

    private static final CuiLogger log = new CuiLogger(YamlConfigurationProvider.class);

    private final FileLoader fileLoader;

    /**
     * @param fileLoader must not be null and the file must be readable:
     *                   {@link FileLoader#isReadable()}
     */
    public YamlConfigurationProvider(final FileLoader fileLoader) {
        requireNonNull(fileLoader, "fileLoader");
        checkArgument(fileLoader.isReadable(), MSG_READ_ERROR + fileLoader);
        this.fileLoader = fileLoader;
        log.debug("Loading properties from: {}", fileLoader.getURL());
    }

    /**
     * @param pathName must not be null and the file must be readable:
     *                 {@link FileLoader#isReadable()}
     */
    public YamlConfigurationProvider(final String pathName) {
        requireNonNull(pathName, "pathName");
        fileLoader = FileLoaderUtility.getLoaderForPath(pathName);
        checkArgument(fileLoader.isReadable(), MSG_READ_ERROR + fileLoader);
    }

    @Override
    public Map<String, String> getConfigurationMap() {
        try {
            final var properties = createProperties();
            final var sanitizedMap = new MapBuilder<String, String>();
            for (final Entry<Object, Object> entry : properties.entrySet()) {
                sanitizedMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
            return sanitizedMap.toImmutableMap();
        } catch (final Exception e) {
            log.error(MSG_LOAD_ERROR, fileLoader.getURL(), e.getMessage());
            log.debug("Portal-100: Exception stack trace follows", e);
        }

        return immutableMap();
    }

    /**
     * Template method that subclasses may override to construct the object returned
     * by this factory. The default implementation returns a properties with the
     * content of all resources.
     *
     * @return the object returned by this factory
     */
    Properties createProperties() throws IOException {
        final var result = createStringAdaptingProperties();
        process((properties, map) -> result.putAll(properties));
        return result;
    }

    /**
     * Create a variant of {@code java.util.Properties} that automatically adapts
     * non-String values to String representations on
     * {@link Properties#getProperty}.
     *
     * @return a new {@code Properties} instance
     */
    @SuppressWarnings("serial")
    private static Properties createStringAdaptingProperties() {
        return new Properties() {

            private static final long serialVersionUID = 1L;

            @Override
            public String getProperty(final String key) {
                final var value = get(key);
                return null != value ? value.toString() : null;
            }
        };
    }

    /**
     *
     * @param source identifying the possible yml file.
     * @return an {@link Optional} on a {@link YamlConfigurationProvider} in case
     *         the given sources references a readable files ending with "yml" or
     *         "yaml", empty Optional otherwise.
     */
    public static Optional<YamlConfigurationProvider> createFromFile(FileLoader source) {
        if (null == source || isEmpty(source.getFileName().getOriginalName())) {
            log.debug("Nothing to load found");
            return Optional.empty();
        }
        final var suffix = source.getFileName().getSuffix().toLowerCase(Locale.ROOT);
        if ("yml".equals(suffix) || "yaml".equals(suffix)) {
            log.debug("Found yml file {}", source);
            if (!source.isReadable()) {
                log.error(MSG_LOAD_ERROR, source, "not readable");
                return Optional.empty();
            }
            return Optional.of(new YamlConfigurationProvider(source));
        }
        log.debug("Given file seems not to represent a yml file: {}", source);
        return Optional.empty();

    }

    /**
     *
     * @param source identifying the possible yml file.
     * @return an {@link Optional} on a {@link YamlConfigurationProvider} in case
     *         the given sources references a readable files ending with "yml" or
     *         "yaml", empty Optional otherwise.
     */
    public static Optional<YamlConfigurationProvider> createFromFile(FileConfigurationSource source) {
        if (null == source || isEmpty(source.getPath())) {
            log.debug("Nothing to load found");
            return Optional.empty();
        }
        return createFromFile(FileLoaderUtility.getLoaderForPath(source.getPath()));
    }

    /**
     * Create the {@link Yaml} instance to use.
     */
    private static Yaml createYaml() {
        return new Yaml(new StrictMapAppenderConstructor());
    }

    private void process(final MatchCallback callback) throws IOException {
        log.debug(() -> "Loading YAML from: " + fileLoader);
        try (final Reader reader = new UnicodeReader(fileLoader.inputStream())) {
            for (final Object document : createYaml().loadAll(reader)) {
                if (null != document) {
                    process(asMap(document), callback);
                }
            }
        }
    }

    private static void process(final Map<String, Object> map, final MatchCallback callback) {
        log.debug("Processing YAML map...");
        final var properties = createStringAdaptingProperties();
        properties.putAll(getFlattenedMap(map));
        log.debug("Merging document (no matchers set): {}", map);
        callback.process(properties, map);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(final Object object) {
        // YAML can have numbers as keys
        final Map<String, Object> result = new LinkedHashMap<>();
        if (!(object instanceof Map)) {
            // A document can be a text literal
            result.put("document", object);
            return result;
        }

        final var map = (Map<Object, Object>) object;
        for (final Map.Entry<Object, Object> entry : map.entrySet()) {
            var value = entry.getValue();
            if (value instanceof Map) {
                value = asMap(value);
            }
            final var key = entry.getKey();
            if (key instanceof CharSequence) {
                result.put(key.toString(), value);
            } else {
                // It has to be a map key in this case
                result.put("[" + key.toString() + "]", value);
            }
        }
        return result;
    }

    /**
     * Return a flattened version of the given map, recursively following any nested
     * Map or Collection values. Entries from the resulting map retain the same
     * order as the source. When called with the Map from a {@link MatchCallback}
     * the result will contain the same values as the {@link MatchCallback}
     * Properties.
     *
     * @param source the source map
     *
     * @return a flattened map
     */
    private static Map<String, Object> getFlattenedMap(final Map<String, Object> source) {
        final Map<String, Object> result = new LinkedHashMap<>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    private static void buildFlattenedMap(final Map<String, Object> result, final Map<String, Object> source,
            final String path) {

        for (final Map.Entry<String, Object> entry : source.entrySet()) {
            final var key = buildFlattenedKey(path, entry.getKey());
            final var value = entry.getValue();
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof Map) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                final var map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            } else if (value instanceof Collection) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                final var collection = (Collection<Object>) value;
                var count = 0;
                for (final Object object : collection) {
                    buildFlattenedMap(result, Collections.singletonMap("[" + count + "]", object), key);
                    count++;
                }
            } else {
                result.put(key, null != value ? value : "");
            }
        }
    }

    private static String buildFlattenedKey(final String path, final String key) {
        if (MoreStrings.hasNonWhitespaceChar(path)) {
            if (key.startsWith("[")) {
                return path + key;
            }
            return path + '.' + key;
        }
        return key;
    }

    /**
     * Callback interface used to process the YAML parsing results.
     */
    interface MatchCallback {

        /**
         * Process the given representation of the parsing results.
         *
         * @param properties the properties to process (as a flattened representation
         *                   with indexed keys in case of a collection or map)
         * @param map        the result map (preserving the original value structure in
         *                   the YAML document)
         */
        void process(Properties properties, Map<String, Object> map);
    }

    /**
     * A specialized {@link Constructor} that checks for duplicate keys.
     */
    static class StrictMapAppenderConstructor extends Constructor {

        // Declared as public for use in subclasses
        StrictMapAppenderConstructor() {
            super(new LoaderOptions());
        }

        @Override
        protected Map<Object, Object> constructMapping(final MappingNode node) {
            try {
                return super.constructMapping(node);
            } catch (final IllegalStateException ex) {
                throw new ParserException("while parsing MappingNode", node.getStartMark(), ex.getMessage(),
                        node.getEndMark());
            }
        }

        @Override
        protected Map<Object, Object> createDefaultMap(final int initSize) {
            final var delegate = super.createDefaultMap(initSize);
            return new AbstractMap<>() {

                @Override
                public Object put(final Object key, final Object value) {
                    if (delegate.containsKey(key)) {
                        throw new IllegalStateException("Duplicate key: " + key);
                    }
                    return delegate.put(key, value);
                }

                @Override
                public @NonNull Set<Entry<Object, Object>> entrySet() {
                    return delegate.entrySet();
                }
            };
        }
    }
}
