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
package de.cuioss.portal.configuration.impl.initializer;

import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.Joiner;
import jakarta.enterprise.context.Dependent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static de.cuioss.portal.configuration.PortalConfigurationMessages.INFO;
import static de.cuioss.tools.string.MoreStrings.isEmpty;
import static de.cuioss.tools.string.MoreStrings.nullToEmpty;

/**
 * Helper class that will log the final configuration derived by all sources. It
 * will only run if it is set on info-level.
 *
 * @author Oliver Wolff
 * @see "io.smallrye.config.LoggingConfigSourceInterceptor"
 */
@Dependent
@PortalInitializer
public class PortalConfigurationLogger implements ApplicationInitializer {

    private static final CuiLogger LOGGER = new CuiLogger(PortalConfigurationLogger.class);

    @SuppressWarnings("squid:S2068") // False positive: This is no password
    private static final String FILTERED_PASSWORD = "FILTERED_PASSWORD";

    @Override
    public void initialize() {
        if (!LOGGER.isInfoEnabled()) {
            return;
        }

        LOGGER.info(INFO.SYSTEM_CONFIG, Joiner.on('\n').join(extractSystemProperties()));
        LOGGER.info(INFO.ENV_CONFIG, Joiner.on('\n').join(extractProperties(System.getenv())));
        LOGGER.info(INFO.PORTAL_CONFIG, Joiner.on('\n').join(extractProperties(ConfigurationHelper.resolveConfigProperties())));
    }

    private List<String> extractProperties(final Map<String, String> input) {
        final List<String> properties = new ArrayList<>();
        for (final Entry<String, String> entry : input.entrySet()) {
            properties.add(createPropertyString(nullToEmpty(entry.getKey()), nullToEmpty(entry.getValue())));
        }
        Collections.sort(properties);
        return properties;
    }

    private static String createPropertyString(final String key, final String value) {
        return key + "=" + filterPassword(key, value);
    }

    private static List<String> extractSystemProperties() {
        final List<String> properties = new ArrayList<>();
        for (final Entry<Object, Object> entry : System.getProperties().entrySet()) {
            var value = "";
            if (null != entry.getValue()) {
                value = String.valueOf(entry.getValue());
            }
            properties.add(createPropertyString(String.valueOf(entry.getKey()), value));
        }
        Collections.sort(properties);
        return properties;
    }

    /**
     * Simple helper method that filters password keys
     *
     * @param key   must not be null
     * @param value value
     * @return the string literal "FILTERED_PASSWORD" in case the given key contains
     *         the token "password" or "Password", otherwise the given value
     */
    public static String filterPassword(final String key, final String value) {
        if (!isEmpty(value) && (key.contains("password") || key.contains("Password"))) {
            return FILTERED_PASSWORD;
        }
        return value;
    }

    @Override
    public Integer getOrder() {
        return 5;
    }
}
