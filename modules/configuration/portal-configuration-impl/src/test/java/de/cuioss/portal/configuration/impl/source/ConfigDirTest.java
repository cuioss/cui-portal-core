package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_DIR;
import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CUSTOMIZATION_DIR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.smallrye.config.inject.ConfigProducer;

/**
 * @author Sven Haag
 */
@EnableAutoWeld
@AddBeanClasses(ConfigProducer.class)
class ConfigDirTest {

    @Inject
    @ConfigProperty(name = PORTAL_CONFIG_DIR)
    private Provider<Optional<String>> configDir;

    @Inject
    @ConfigProperty(name = PORTAL_CUSTOMIZATION_DIR)
    private Provider<Optional<String>> customizationDir;

    @AfterEach
    void clearSystemProperties() {
        System.clearProperty(PORTAL_CONFIG_DIR);
        System.clearProperty(PORTAL_CUSTOMIZATION_DIR);
    }

    @Test
    void defaultConfigDirPresent() {
        final var value = configDir.get();
        assertTrue(value.isPresent(), "portal default config dir should be present");

        System.setProperty(PORTAL_CONFIG_DIR, "elsewhere/");
        assertEquals("elsewhere/", configDir.get().get());
    }

    @Test
    void emptyConfigDirProperty() {
        System.setProperty(PORTAL_CONFIG_DIR, "");
        final var value = configDir.get();
        assertFalse(value.isPresent());
    }

}
