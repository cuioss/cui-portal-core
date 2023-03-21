package de.cuioss.portal.configuration.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.Weld;
import org.junit.jupiter.api.Test;

/**
 * Tests the complete cdi environment / wiring
 *
 * @author Oliver Wolff
 */
class ModuleConsistencyTest {

    @Test
    void shouldStartUpContainer() {
        try (final var weld = new Weld().initialize()) {
            assertNotNull(weld.select(BeanManager.class),
                    "Unable to acquire an instance of javax.enterprise.inject.spi.BeanManager");

            /*
             * final Set<Bean<?>> fileConfigSourceInstances = CDI.current().getBeanManager()
             * .getBeans(FileConfigurationSource.class, PortalConfigurationSource.Literal.INSTANCE);
             * assertFalse(fileConfigSourceInstances.isEmpty(),
             * "FileConfigurationSource instances not registered in CDI");
             */
        }
    }

    @Test
    void moduleShouldProvideBeansXml() {
        assertTrue(Paths.get("src", "main", "resources", "META-INF", "beans.xml").toFile().exists(),
                "It is best-practice that each module provides a beans.xml");
    }

}
