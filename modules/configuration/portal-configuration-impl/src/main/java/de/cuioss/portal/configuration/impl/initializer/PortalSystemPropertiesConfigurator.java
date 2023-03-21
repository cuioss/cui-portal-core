package de.cuioss.portal.configuration.impl.initializer;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_SYSTEM_PROPERTY_PREFIX;
import static de.cuioss.tools.string.MoreStrings.nullToEmpty;

import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Provider;

import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.configuration.types.ConfigAsFilteredMap;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Helper class that translates custom properties to system properties, see documentation of
 * {@link PortalConfigurationKeys#PORTAL_SYSTEM_PROPERTY_PREFIX}
 *
 * {@link #getOrder()} is not overridden because the default,
 * {@link ApplicationInitializer#ORDER_INTERMEDIATE} is perfect
 *
 * @author Oliver Wolff
 *
 */
@ApplicationScoped
@PortalInitializer
public class PortalSystemPropertiesConfigurator implements ApplicationInitializer {

    private static final CuiLogger log = new CuiLogger(PortalSystemPropertiesConfigurator.class);

    @Inject
    @ConfigAsFilteredMap(startsWith = PORTAL_SYSTEM_PROPERTY_PREFIX, stripPrefix = true)
    private Provider<Map<String, String>> systemProperties;

    @Override
    public void initialize() {
        setSystemProperties(systemProperties.get());
    }

    private static void setSystemProperties(final Map<String, String> map) {
        if (map.isEmpty()) {
            return;
        }
        final var actualProperties = System.getProperties();
        for (final Entry<String, String> entry : map.entrySet()) {
            final var key = nullToEmpty(entry.getKey());
            if (!key.trim().isEmpty()) {
                final var value = nullToEmpty(entry.getValue());
                final var readSystemProperty = nullToEmpty(actualProperties.getProperty(key));
                if (readSystemProperty.equals(value)) {
                    log.debug("Property value for '{}' is the same like the exisiting one '{}'", key,
                            PortalConfigurationLogger.filterPassword(key, readSystemProperty));
                } else {
                    log.info("Property value for '{}' changed, replacing '{}' with {}", key,
                            PortalConfigurationLogger.filterPassword(key, readSystemProperty),
                            PortalConfigurationLogger.filterPassword(key, value));
                    System.setProperty(key, value);
                }
            }
        }
    }

    /**
     * Listener for {@link PortalConfigurationChangeEvent}s. Reconfigures the locale-configuration
     *
     * @param deltaMap
     */
    void configurationChangeEventListener(
            @Observes @PortalConfigurationChangeEvent final Map<String, String> deltaMap) {
        final var filteredMap = ConfigurationHelper.getFilteredPropertyMap(deltaMap,
                PORTAL_SYSTEM_PROPERTY_PREFIX, true);
        if (!filteredMap.isEmpty()) {
            log.debug("Update system properties");
            setSystemProperties(filteredMap);
        }
    }
}
