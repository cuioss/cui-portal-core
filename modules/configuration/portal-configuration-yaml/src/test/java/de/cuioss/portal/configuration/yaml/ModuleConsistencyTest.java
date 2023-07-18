package de.cuioss.portal.configuration.yaml;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.Weld;
import org.junit.jupiter.api.Test;

/**
 * Tests the complete cdi environment / wiring.
 * <p>
 * Does not utilize BaseModuleConsistencyTest from portal-unit-testing-junit5
 * due to a circular dependency via portal-configuration-impl.
 *
 * @author Sven Haag
 */
class ModuleConsistencyTest {

    @Test
    void shouldStartUpContainer() {
        try (var weld = new Weld().initialize()) {
            assertNotNull(weld.select(BeanManager.class),
                    "Unable to acquire an instance of javax.enterprise.inject.spi.BeanManager");
        }
    }

    @Test
    void moduleShouldProvideBeansXml() {
        assertTrue(Paths.get("src", "main", "resources", "META-INF", "beans.xml").toFile().exists(),
                "It is best-practice that each module provides a beans.xml");
    }
}
