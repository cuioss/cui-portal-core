package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_DIR;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;

import de.cuioss.portal.configuration.ConfigurationSourceChangeEvent;
import de.cuioss.tools.logging.CuiLogger;

/**
 * This is a simple observer observing changes of the portal config dir configuration. If there is a
 * change it re-initialized all AbstractInstallationConfigSources via
 * {@link AbstractInstallationConfigSource#initPath()}.
 *
 * This cannot be done in the config source itself as they cannot be CDI enabled due to the nature of an actual MP
 * config source (init via SPI).
 *
 * This must only apply to junit tests, because per default surefire creates 1 JVM per module, not per test class!
 *
 * @author Sven Haag
 */
@ApplicationScoped
public class InstallationConfigSourcePathInitializer {

    private static final CuiLogger LOGGER = new CuiLogger(InstallationConfigSourcePathInitializer.class);

    void configSourceChangeEventListener(@Observes @ConfigurationSourceChangeEvent final Map<String, String> eventMap) {
        if (eventMap.containsKey(PORTAL_CONFIG_DIR)) {
            LOGGER.debug("portal config dir changed");
            for (final ConfigSource configSource : ConfigProvider.getConfig().getConfigSources()) {
                if (configSource instanceof AbstractInstallationConfigSource) {
                    LOGGER.debug("re-init: {}", configSource.getName());
                    ((AbstractInstallationConfigSource) configSource).initPath();
                }
            }
        }
    }
}
