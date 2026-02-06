/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.configuration.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Data Transfer Object (DTO) representing a configuration placeholder in the Portal's
 * configuration system. Handles parsing and representation of placeholders with
 * optional default values.
 * 
 * <h2>Placeholder Format</h2>
 * <ul>
 *   <li>Basic: {@code ${config.key}}</li>
 *   <li>With Default: {@code ${config.key:defaultValue}}</li>
 * </ul>
 * 
 * <h2>Examples</h2>
 * <pre>
 * // Simple placeholder
 * ${app.home}
 * 
 * // Placeholder with default
 * ${app.port:8080}
 * 
 * // Placeholder with empty default
 * ${app.theme:}
 * 
 * // Environment variable reference
 * ${ENV_VAR:defaultValue}
 * </pre>
 * 
 * <h2>Usage Notes</h2>
 * <ul>
 *   <li>Default values are optional</li>
 *   <li>Empty default values are supported</li>
 *   <li>Whitespace in keys is not recommended</li>
 *   <li>Case-sensitive keys</li>
 * </ul>
 * 
 * @author Oliver Wolff
 */
@ToString
@EqualsAndHashCode
class ConfigPlaceholder {

    /**
     * Regex pattern matching a valid config placeholder. It is not perfect! E.g. a
     * key with leading or trailing whitespace/s is matched too, as well as a
     * placeholder with non-matching leading and trailing curly braces - e.g.
     * "${key1:${key2}crap}".
     */
    static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{" + // find start of a config placeholder.
            // escaped, because these are regexp special
            // chars.
            "[^:]" + // ignore placeholder if it starts with a double-colon and therefore has no
            // config-key.
            ".*?" + // allow any character between curly braces. however,
            // we need to stop at the first sight of a placeholders suffix.
            // therefore, we use a lazy mode quantifier - the question mark.
            "}+" // find at least 1 suffix character, but as many as possible to account for
    // nested placeholders.
    );

    @SuppressWarnings("el-syntax")
    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";
    private static final String PLACEHOLDER_DEFAULT_VALUE_SPLITTER = ":";

    /**
     * the placeholder key
     */
    @Getter
    private final @NonNull String configKey;

    /**
     * the placeholders default value (optional)
     */
    private final String defaultValue;

    /**
     * Constructor for placeholders with a key only. Sets {@link #defaultValue} to
     * {@code null}.
     */
    public ConfigPlaceholder(@NonNull final String configKey) {
        this(configKey, null);
    }

    /**
     * Constructor for placeholders with a key and default value.
     */
    public ConfigPlaceholder(@NonNull final String configKey, final String defaultValue) {
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    /**
     * Parse the given string.
     * <p>
     * Spring Framework will use an empty string if there is nothing behind the
     * double colon, see:
     * <a href="https://github.com/spring-projects/spring-framework/blob/dad0b1e98cacf417cc5c68b5884e7a1e47b85000/spring-core/src/main/java/org/springframework/util/PropertyPlaceholderHelper.java#L156">...</a>
     *
     * @param placeholder must not be null
     * @throws IllegalArgumentException if the given string is not a placeholder
     */
    public static ConfigPlaceholder split(@NonNull final String placeholder) {
        if (!isPlaceholder(placeholder)) {
            throw new IllegalArgumentException("Not a valid config placeholder: " + placeholder);
        }

        final var placeholderContent = placeholder.substring(PLACEHOLDER_PREFIX.length(),
                placeholder.length() - PLACEHOLDER_SUFFIX.length());

        if (placeholderContent.contains(PLACEHOLDER_DEFAULT_VALUE_SPLITTER)) {
            final var start = placeholderContent.indexOf(PLACEHOLDER_DEFAULT_VALUE_SPLITTER);
            return new ConfigPlaceholder(placeholderContent.substring(0, start),
                    placeholderContent.substring(start + PLACEHOLDER_DEFAULT_VALUE_SPLITTER.length()));
        }
        return new ConfigPlaceholder(placeholderContent);
    }

    /**
     * @return true, if the given string represents a placeholder. false, otherwise,
     *         or the given string is null.
     */
    public static boolean isPlaceholder(final String placeholder) {
        if (null == placeholder) {
            return false;
        }

        return PLACEHOLDER_PATTERN.matcher(placeholder).matches();
    }
}
