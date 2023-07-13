package de.cuioss.portal.core.test.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;

/**
 * Base class for testing the consistency / structure of an assembly module by
 * starting up a {@link WeldContainer} and checking whether the module provides
 * a beans.xml-file. This is a variant of {@link BaseModuleConsistencyTest}
 *
 * @author Oliver Wolff
 *
 */
public class BaseAssemblyConsistencyTest {

    @Test
    protected void shouldStartUpContainer() {
        try (var weld = new Weld().initialize()) {
            assertNotNull(weld.select(BeanManager.class),
                    "Unable to acquire an instance of javax.enterprise.inject.spi.BeanManager");
        }
    }

    @Test
    protected void assemblyShouldProvideBeansXml() {
        assertTrue(Paths.get("src", "main", "webapp", "WEB-INF", "beans.xml").toFile().exists(),
                "It is best-practice that each module provides a beans.xml");
    }

}
