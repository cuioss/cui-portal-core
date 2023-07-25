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
 * <h3>Extending</h3>
 * <p>
 * in case you want to add beans or modify the dev-mode you just need to
 * implement the callback method:
 * </p>
 * 
 * <pre>
 * protected Weld modifyWeldContainer(Weld weld) {
 *     return weld.enableDevMode().addBeanClass(ServletObjectsFromJSFContextProducers.class);
 * }
 * </pre>
 *
 * 
 * @author Oliver Wolff
 *
 */
public class BaseModuleConsistencyTest {

    @Test
    protected void shouldStartUpContainer() {
        try (var weld = modifyWeldContainer(new Weld()).initialize()) {
            assertNotNull(weld.select(BeanManager.class),
                    "Unable to acquire an instance of javax.enterprise.inject.spi.BeanManager");
        }
    }

    @Test
    protected void moduleShouldProvideBeansXml() {
        assertTrue(Paths.get("src", "main", "resources", "META-INF", "beans.xml").toFile().exists(),
                "It is best-practice that each module provides a beans.xml");
    }

    /**
     * Use the callback to modify the Weld-testcontainer, like setting dev-mode or
     * adding beans.
     * 
     * @param weld to be modified
     * @return the modified weld
     */
    protected Weld modifyWeldContainer(Weld weld) {
        return weld;
    }

}
