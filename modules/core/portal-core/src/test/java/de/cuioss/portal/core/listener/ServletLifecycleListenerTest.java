package de.cuioss.portal.core.listener;

import static de.cuioss.portal.core.PortalCoreLogMessages.LIFECYCLE;
import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.core.servlet.CuiContextPath;
import de.cuioss.test.jsf.mocks.CuiMockServletContext;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.servlet.ServletContextEvent;
import lombok.Getter;
import org.apache.myfaces.test.mock.MockServletContext;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ServletLifecycleListener} which manages the lifecycle of portal
 * components and initializers.
 */
@EnableAutoWeld
@EnableTestLogger
@DisplayName("ServletLifecycleListener Tests")
class ServletLifecycleListenerTest implements ShouldBeNotNull<ServletLifecycleListener> {

    @Inject
    @Getter
    private ServletLifecycleListener underTest;

    @Produces
    @PortalInitializer
    @Dependent
    private MockInitializer mockInitializer;

    private static final String CONTEXT_PATH = "mock-context";
    private static final MockServletContext SERVLET_CONTEXT = new CuiMockServletContext();

    @Inject
    @CuiContextPath
    private Provider<String> contextPathProvider;

    @BeforeEach
    void before() {
        mockInitializer = new MockInitializer();
    }

    @Nested
    @DisplayName("Lifecycle Management Tests")
    class LifecycleManagementTests {
        
        @Test
        @DisplayName("Should properly initialize and destroy components")
        void shouldInitializeAndDestroy() {
            assertFalse(mockInitializer.isInitializeCalled(), 
                "Initialize should not be called initially");
            assertFalse(mockInitializer.isDestroyCalled(), 
                "Destroy should not be called initially");

            underTest.contextInitialized(new ServletContextEvent(SERVLET_CONTEXT));

            assertTrue(mockInitializer.isInitializeCalled(), 
                "Initialize should be called after context initialization");
            assertFalse(mockInitializer.isDestroyCalled(), 
                "Destroy should not be called after initialization");

            underTest.applicationDestroyListener();
            assertTrue(mockInitializer.isInitializeCalled(), 
                "Initialize should remain called after destroy");
            assertTrue(mockInitializer.isDestroyCalled(), 
                "Destroy should be called after destroy listener");
        }

        @Test
        @DisplayName("Should handle exceptions during destroy gracefully")
        void shouldHandleExceptionOnDestroy() {
            mockInitializer.setExplodeOnDestroy(true);

            underTest.applicationDestroyListener();
            assertTrue(mockInitializer.isDestroyCalled(), 
                "Destroy should be called even if exception occurs");

            LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN, 
                LIFECYCLE.WARN.DESTROY_ERROR.resolveIdentifierString());
        }
    }

    @Nested
    @DisplayName("Context Path Tests")
    class ContextPathTests {
        
        @Test
        @DisplayName("Should handle default context path correctly")
        void shouldHandleDefaultContextPath() {
            assertEquals("portal", contextPathProvider.get(), 
                "Should start with default context path");

            underTest.contextInitialized(new ServletContextEvent(SERVLET_CONTEXT));
            assertEquals(CONTEXT_PATH, contextPathProvider.get(), 
                "Should update to mock context path after initialization");
        }

        @Test
        @DisplayName("Should handle missing context path gracefully")
        void shouldHandleContextPathNotSet() {
            assertEquals("portal", contextPathProvider.get(), 
                "Should use default context path when not set");
        }
    }
}
