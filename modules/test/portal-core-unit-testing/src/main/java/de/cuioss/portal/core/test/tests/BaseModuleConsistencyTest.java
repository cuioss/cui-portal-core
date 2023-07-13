package de.cuioss.portal.core.test.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;

/**
 * Base class for testing the consistency / structure of a certain module by
 * starting up a {@link WeldContainer} and checking whether the module provides
 * a beans.xml-file
 *
 * @author Oliver Wolff
 *
 */
public class BaseModuleConsistencyTest {

    @Test
    protected void shouldStartUpContainer() {
        try (var weld = new Weld().initialize()) {
            assertNotNull(weld.select(BeanManager.class),
                    "Unable to acquire an instance of javax.enterprise.inject.spi.BeanManager");
        }
    }

    @Test
    protected void moduleShouldProvideBeansXml() {
        assertTrue(Paths.get("src", "main", "resources", "META-INF", "beans.xml").toFile().exists(),
                "It is best-practice that each module provides a beans.xml");
    }

}
