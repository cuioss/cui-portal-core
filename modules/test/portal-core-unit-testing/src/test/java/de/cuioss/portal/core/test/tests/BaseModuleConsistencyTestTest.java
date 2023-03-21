package de.cuioss.portal.core.test.tests;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jboss.weld.exceptions.DeploymentException;
import org.junit.jupiter.api.Test;

class BaseModuleConsistencyTestTest extends BaseModuleConsistencyTest {

    @Override
    @Test
    protected void shouldStartUpContainer() {
        // This is not CDI module, therefore this test must fail
        assertThrows(DeploymentException.class, () -> {
            super.shouldStartUpContainer();
        });
    }

    @Override
    @Test
    protected void moduleShouldProvideBeansXml() {
        // This is not CDI module, therefore this test must fail
        assertThrows(AssertionError.class, () -> {
            super.moduleShouldProvideBeansXml();
        });
    }
}
