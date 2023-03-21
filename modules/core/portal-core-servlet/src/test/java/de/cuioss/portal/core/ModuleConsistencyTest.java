package de.cuioss.portal.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.Weld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.core.test.support.PortalAuthenticationFacadeMock;

class ModuleConsistencyTest {

    @Test
    void shouldStartUpContainer() {
        try (var weld = new Weld().enableDiscovery().addBeanClass(PortalAuthenticationFacadeMock.class).initialize()) {
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
