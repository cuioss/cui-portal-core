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
package de.cuioss.portal.core.test.tests.configuration;

import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.Joiner;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static de.cuioss.tools.string.MoreStrings.emptyToNull;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Base test class for verifying configuration key mappings to specific {@link ConfigSource}s.
 * Ensures configuration completeness and detects key naming changes by validating
 * declared configuration keys against the actual configuration source.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Configuration key validation</li>
 *   <li>ConfigSource mapping verification</li>
 *   <li>Key presence testing</li>
 *   <li>Configurable key filtering</li>
 *   <li>Detailed validation reporting</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 * <p>
 * Basic configuration test:
 * <pre>
 * public class MyConfigTest extends AbstractConfigurationKeyVerifierTest {
 *     &#64;Override
 *     protected Class<?> getKeyHolder() {
 *         return MyConfigurationKeys.class;
 *     }
 *
 *     &#64;Override
 *     protected String getConfigSourceName() {
 *         return "my-config-source";
 *     }
 * }
 * </pre>
 * <p>
 * Custom key filtering:
 * <pre>
 * public class FilteredConfigTest extends AbstractConfigurationKeyVerifierTest {
 *     &#64;Override
 *     protected Set<String> getKeysIgnoreList() {
 *         return Set.of("temporary.key", "debug.key");
 *     }
 *
 *     &#64;Override
 *     protected Set<String> getConfigurationKeysIgnoreList() {
 *         return Set.of("DEPRECATED_KEY", "FUTURE_KEY");
 *     }
 *
 *     // ... other required overrides
 * }
 * </pre>
 *
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Uses MicroProfile Config for configuration access</li>
 *   <li>Validates against public static final fields</li>
 *   <li>Supports key filtering and exclusions</li>
 *   <li>Provides detailed validation failure messages</li>
 *   <li>Handles null and empty values appropriately</li>
 * </ul>
 *
 * <h2>Best Practices</h2>
 * <ul>
 *   <li>Create one test class per configuration source</li>
 *   <li>Document ignored keys with reasons</li>
 *   <li>Keep key holder classes well-organized</li>
 *   <li>Regularly review ignored keys</li>
 *   <li>Use meaningful config source names</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @see ConfigSource
 * @see ConfigProvider
 * @since 1.0
 */
public abstract class AbstractConfigurationKeyVerifierTest {

    public static final String CONFIG_NAME = "config_name";
    private static final CuiLogger LOGGER = new CuiLogger(AbstractConfigurationKeyVerifierTest.class);

    /**
     * @return the class providing the keys as public static final variables. This
     * is the base for the checks. Must not be null;
     */
    public abstract Class<?> getKeyHolder();

    /**
     * @return the name of {@link org.eclipse.microprofile.config.spi.ConfigSource} that will be checked against the
     * derived keys.
     * The name is expected to be found under the key {@value #CONFIG_NAME}
     */
    public abstract String getConfigSourceName();

    /**
     * Defines the keys from the {@link #getKeyHolder()} that are not to be checked
     * against {@link #getConfigSourceName()} .
     * The filtering is done within {@link #resolveKeyNames()}
     *
     * @return a list of key names to be ignored. The default implementation returns
     * an empty {@link List}. Must not return {@code null}
     */
    public List<String> getKeysIgnoreList() {
        return Collections.emptyList();
    }

    /**
     * Defines the keys from the configuration that are not to be checked against
     * {@link #resolveKeyNames()}. The individual entries are checked using
     * {@link String#startsWith(String)}, saying they are implicitly used as
     * wildcards
     *
     * @return a list of key names to be ignored. The default implementation returns
     * an empty {@link List}. Must not return {@code null}
     */
    public List<String> getConfigurationKeysIgnoreList() {
        return Collections.emptyList();
    }

    /**
     * Tests whether each checked key, provided by {@link #resolveKeyNames()} is
     * backed by the given configuration {@link #getConfigSourceName()}
     */
    @Test
    void keyNamesShouldBeProvidedByConfiguration() {
        var configurationKeys = extractConfigurationKeys();
        var resolvedKeyNames = resolveKeyNames();

        LOGGER.info("Checking resolvedKeyNames:{}against configurationKeys:{}",
                System.lineSeparator() + "\t" + Joiner.on(System.lineSeparator() + "\t").join(resolvedKeyNames)
                        + System.lineSeparator(),
                System.lineSeparator() + "\t" + Joiner.on(System.lineSeparator() + "\t").join(configurationKeys));

        final List<String> notFoundKeys = resolvedKeyNames.stream().filter(key -> !configurationKeys.contains(key))
                .toList();
        if (!notFoundKeys.isEmpty()) {
            fail("Found Keys that are not backed by the provided configuration: " + notFoundKeys
                    + ", you can use #getKeysIgnoreList() to filter the keys to be checked");
        }
    }

    /**
     * Tests whether each checked key, provided by
     * {@link #resolveConfigurationKeyNames()} is backed by the given keyName
     * {@link #resolveKeyNames()}
     */
    @Test
    void configurationKeysShouldReverseMapToKeyNames() {
        var configurationKeys = resolveConfigurationKeyNames();
        var resolvedKeysFromType = resolveKeyNames();
        LOGGER.info("Checking configurationKeys='{}' against resolvedKeysFromType='{}'", resolvedKeysFromType, configurationKeys);
        List<String> notFoundKeys = configurationKeys.stream().filter(key -> !resolvedKeysFromType.contains(key))
                .toList();
        if (!notFoundKeys.isEmpty()) {
            fail("Found Keys that are not backed by the provided configuration: " + notFoundKeys
                    + ", you can use #getConfigurationKeysIgnoreList() to filter the keys to be checked");
        }
    }

    /**
     * @return the keys derived by reading all public static final fields from type
     * {@link String} from the class read from {@link #getKeyHolder()} that
     * are not contained within {@link #getKeysIgnoreList()} and do not end
     * with a '.'
     * Reason: These keys are interpreted to be a prefix for other keys
     */
    public SortedSet<String> resolveKeyNames() {
        Class<?> keyHolder = requireNonNull(getKeyHolder(), "getKeyHolder() must not return null");
        var blacklist = getKeysIgnoreList();
        List<String> keys = new ArrayList<>();
        for (Field field : keyHolder.getFields()) {
            String value;
            try {
                var read = field.get(null);
                if (read instanceof String) {
                    value = (String) field.get(null);
                    assertNotNull(emptyToNull(value));
                    if (!blacklist.contains(value) && !value.endsWith(".")) {
                        keys.add(value);
                    }
                } else {
                    LOGGER.debug("Field '{}' is not a String, ignoring", field.getName());
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                var message = "Unable to read field " + field.getName() + " due to " + e.getMessage();
                LOGGER.error(message, e);
                fail(message);
            }
        }
        return new TreeSet<>(keys);
    }

    /**
     * @return the Keys derived by reading them from {@link #getConfigSourceName()} and
     * removing the keys from {@link #getConfigurationKeysIgnoreList()}
     */
    public SortedSet<String> resolveConfigurationKeyNames() {
        var configurationKeys = extractConfigurationKeys();
        var blacklist = getConfigurationKeysIgnoreList();
        SortedSet<String> keys = new TreeSet<>();
        for (String key : configurationKeys) {
            var startsWith = false;
            for (String blackKey : blacklist) {
                if (key.startsWith(blackKey)) {
                    startsWith = true;
                    break;
                }
            }
            if (!startsWith) {
                keys.add(key);
            }
        }
        return keys;
    }

    /**
     * Extracts non-empty keys, as empty keys are handled as non-existing in
     * MicroProfile config.
     * It checks
     * for a {@link ConfigSource} containing the key {@link #CONFIG_NAME} with the value provided by {@link #getConfigSourceName()}
     *
     * @return the {@link SortedSet} of non-empty configuration keys for the given {@link ConfigSource}.
     * The key {@link #CONFIG_NAME} will implicitly be filtered.
     */
    protected SortedSet<String> extractConfigurationKeys() {
        final var name = getConfigSourceName();
        assertNotNull(emptyToNull(name), "You must provide a configuration source name");

        Optional<ConfigSource> found = Optional.empty();
        for (var source : ConfigProvider.getConfig().getConfigSources()) {
            if (name.equals(source.getValue(CONFIG_NAME))) {
                found = Optional.of(source);
                break;
            }
        }
        if (found.isPresent()) {
            TreeSet<String> strings = new TreeSet<>(found.get().getPropertyNames());
            strings.remove(CONFIG_NAME);
            return strings;
        }
        var foundNames = StreamSupport.stream(ConfigProvider.getConfig().getConfigSources().spliterator(), false).map(ConfigSource::getName).collect(Collectors.toSet());
        throw new AssertionError("Unable to find any configuration source named '%s', available sources: '%s'".formatted(name, foundNames));
    }
}
