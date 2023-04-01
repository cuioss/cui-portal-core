package de.cuioss.portal.core.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.core.servlet.CuiContextPath;
import de.cuioss.test.generator.Generators;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
@EnableTestLogger
class ServletLifecycleListenerTest implements ShouldBeNotNull<ServletLifecycleListener> {

    @Inject
    @Getter
    private ServletLifecycleListener underTest;

    @Produces
    @PortalInitializer
    @Dependent
    private MockInitializer mockInitializer;

    private boolean shouldExplodeCallingContextPath = false;

    private static final String CONTEXT_PATH = "testContextPath";

    @BeforeEach
    void before() {
        mockInitializer = new MockInitializer();
        shouldExplodeCallingContextPath = false;
    }

    @Test
    void shoudinitialize() {
        assertFalse(mockInitializer.isInitializeCalled());
        assertFalse(mockInitializer.isDestroyCalled());

        underTest.applicationInitializerListener(null);
        assertTrue(mockInitializer.isInitializeCalled());
        assertFalse(mockInitializer.isDestroyCalled());

        underTest.applicationDestroyListener();
        assertTrue(mockInitializer.isInitializeCalled());
        assertTrue(mockInitializer.isDestroyCalled());
    }

    @Test
    void shouldHandleExeptionOnDestroy() {
        mockInitializer.setExplodeOnDestroy(true);

        underTest.applicationDestroyListener();
        assertTrue(mockInitializer.isDestroyCalled());

        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN, "Runtime Exception occurred while");
    }

    @Test
    void shouldHandleDefaultContextPath() {
        assertEquals(CONTEXT_PATH, underTest.getContextPath());
        // Should be cached
        assertEquals(CONTEXT_PATH, underTest.getContextPath());
    }

    @Test
    void shouldHandleFailingContextPath() {
        shouldExplodeCallingContextPath = true;
        assertEquals("portal", underTest.getContextPath());
    }

    @Produces
    @CuiContextPath
    String produceContextPath() {
        if (shouldExplodeCallingContextPath) {
            throw Generators.runtimeExceptions().next();
        }
        return CONTEXT_PATH;
    }
}
