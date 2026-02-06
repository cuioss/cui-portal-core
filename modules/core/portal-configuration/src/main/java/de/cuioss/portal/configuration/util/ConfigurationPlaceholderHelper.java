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

import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.Joiner;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigProperty;
import static java.util.regex.Matcher.quoteReplacement;

/**
 * Helper class for processing configuration placeholders in property values.
 * Handles resolution of placeholders, including nested placeholders and default values,
 * with protection against excessive nesting depth.
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Resolves placeholders in configuration values</li>
 *   <li>Supports default values using {@code ${key:default}} syntax</li>
 *   <li>Handles nested placeholders up to 5 levels deep</li>
 *   <li>Protects against circular dependencies</li>
 * </ul>
 * 
 * <h2>Examples</h2>
 * <pre>
 * // Simple placeholder
 * value = "${app.home}"
 * result = "/opt/app"
 * 
 * // Placeholder with default
 * value = "${app.port:8080}"
 * result = "8080" (if app.port is not defined)
 * 
 * // Nested placeholders
 * value = "${app.${env.name}.config}"
 * result = "production-settings" (if env.name="prod" and app.prod.config="production-settings")
 * </pre>
 * 
 * <h2>Error Handling</h2>
 * <ul>
 *   <li>Missing keys: Logged as warnings, throws exception if configured</li>
 *   <li>Excessive nesting: Throws {@link ConfigKeyNestingException}</li>
 *   <li>Invalid syntax: Throws {@link IllegalArgumentException}</li>
 * </ul>
 * 
 * @author Sven Haag
 */
class ConfigurationPlaceholderHelper {

    private static final CuiLogger LOGGER = new CuiLogger(ConfigurationPlaceholderHelper.class);

    /**
     * Our representation of a valid config placeholder. It is not perfect! E.g. a
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

    private ConfigurationPlaceholderHelper() {
    }

    /**
     * Replace all config placeholders in the given string. Also nested placeholders
     * are processed up to a depth of 5. Default values are supported, using the
     * syntax {@code ${key:default}}.
     *
     * @param value                 to be processed
     * @param exceptionOnMissingKey if true, throws an exception if a config key is
     *                              missing
     * @return the given {@code value} with replaced placeholders, where possible.
     * @throws NoSuchElementException    if {@code exceptionOnMissingKey} is true
     *                                   and a required config key is missing
     * @throws ConfigKeyNestingException if a config keys default value is used, and
     *                                   its value is a placeholder again, for more
     *                                   than 5-times.
     */
    static String replacePlaceholders(final String value, final boolean exceptionOnMissingKey) {
        final Set<String> missingConfigKeys = new HashSet<>();
        var result = replacePlaceholders(value, missingConfigKeys, 1);

        if (!missingConfigKeys.isEmpty()) {
            final var errMsg = "Portal-161: Missing config key/s: " + Joiner.on(", ").join(missingConfigKeys);
            /*~~(TODO: WARN needs LogRecord. Suppress: // cui-rewrite:disable CuiLogRecordPatternRecipe)~~>*//*~~(TODO: WARN needs LogRecord. Suppress: // cui-rewrite:disable CuiLogRecordPatternRecipe)~~>*//*~~(TODO: WARN needs LogRecord. Suppress: // cui-rewrite:disable CuiLogRecordPatternRecipe)~~>*/LOGGER.warn(errMsg);
            if (exceptionOnMissingKey) {
                throw new NoSuchElementException(errMsg);
            }
        }

        return result;
    }

    private static String replacePlaceholders(final String value, final Set<String> missingConfigKeys,
            final int count) {
        if (null == value) {
            return null;
        }

        return PLACEHOLDER_PATTERN.matcher(value).replaceAll(matchResult -> {
            final var placeholderEntry = matchResult.group();
            final var configPlaceholder = ConfigPlaceholder.split(placeholderEntry);
            final String configKey = configPlaceholder.getConfigKey();
            final var defaultValue = configPlaceholder.getDefaultValue();

            var resolvedValue = resolveConfigProperty(configKey);
            if (resolvedValue.isPresent()) {
                return quoteReplacement(resolvedValue.get());
            }

            if (defaultValue.isPresent()) {
                if (5 == count) {
                    // stop resolving placeholders in defaultValue
                    throw new ConfigKeyNestingException(configKey);
                }

                try {
                    // resolve placeholders in defaultValue
                    return quoteReplacement(replacePlaceholders(defaultValue.get(), missingConfigKeys, count + 1));
                } catch (ConfigKeyNestingException e) {
                    throw new ConfigKeyNestingException(configKey, e);
                }
            }

            // config key and default value missing
            missingConfigKeys.add(configKey);
            return quoteReplacement(placeholderEntry);
        });
    }
}
