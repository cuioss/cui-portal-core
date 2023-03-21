package de.cuioss.portal.configuration.impl.initializer;

import static de.cuioss.tools.string.MoreStrings.isEmpty;
import static de.cuioss.tools.string.MoreStrings.nullToEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.Dependent;

import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.Joiner;

/**
 * Helper class that will log the final configuration derived by all sources. It will only run if it
 * is set on info-level.
 *
 * @author Oliver Wolff
 * @see "io.smallrye.config.LoggingConfigSourceInterceptor"
 */
@Dependent
@PortalInitializer
public class PortalConfigurationLogger implements ApplicationInitializer {

    private static final CuiLogger log = new CuiLogger(PortalConfigurationLogger.class);

    @SuppressWarnings("squid:S2068") // False positive: This is no password
    private static final String FILTERED_PASSWORD = "FILTERED_PASSWORD";

    @Override
    public void initialize() {
        if (!log.isInfoEnabled()) {
            return;
        }

        log.info("JVM Configuration:\n" + Joiner.on('\n').join(extractSystemProperties()));
        log.info("Environment Configuration:\n" + Joiner.on('\n').join(extractProperties(System.getenv())));
        log.info("Portal Configuration:\n" + Joiner.on('\n').join(extractProperties(
            ConfigurationHelper.resolveConfigProperties())));
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
     * @param key must not be null
     * @param value value
     * @return the string literal "FILTERED_PASSWORD" in case the given key contains the token
     *         "password" or "Password", otherwise the given value
     */
    public static String filterPassword(final String key, final String value) {
        if (!isEmpty(value)
            && (key.contains("password") || key.contains("Password"))) {
            return FILTERED_PASSWORD;
        }
        return value;
    }

    @Override
    public Integer getOrder() {
        return 5;
    }
}
