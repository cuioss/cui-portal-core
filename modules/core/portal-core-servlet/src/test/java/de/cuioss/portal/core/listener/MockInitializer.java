package de.cuioss.portal.core.listener;

import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.test.generator.Generators;
import lombok.Getter;
import lombok.Setter;

@PortalInitializer
class MockInitializer implements ApplicationInitializer {

    @Getter
    boolean initializeCalled;

    @Getter
    boolean destroyCalled;

    @Getter
    @Setter
    boolean explodeOnDestroy = false;

    @Override
    public void initialize() {
        initializeCalled = true;
    }

    @Override
    public void destroy() {
        destroyCalled = true;
        if (explodeOnDestroy) {
            throw Generators.runtimeExceptions().next();
        }
    }
}
