/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.core.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContextEvent;

import org.apache.myfaces.test.mock.MockServletContext;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.core.servlet.CuiContextPath;
import de.cuioss.test.jsf.mocks.CuiMockServletContext;
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

    private static final String CONTEXT_PATH = "mock-context";
    private static final MockServletContext SERVLET_CONTEXT = new CuiMockServletContext();

    @Inject
    @CuiContextPath
    private Provider<String> contextPathProvider;

    @BeforeEach
    void before() {
        mockInitializer = new MockInitializer();
    }

    @Test
    void shoudinitialize() {
        assertFalse(mockInitializer.isInitializeCalled());
        assertFalse(mockInitializer.isDestroyCalled());

        underTest.contextInitialized(new ServletContextEvent(SERVLET_CONTEXT));

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
        assertEquals("portal", contextPathProvider.get());

        underTest.contextInitialized(new ServletContextEvent(SERVLET_CONTEXT));
        assertEquals(CONTEXT_PATH, contextPathProvider.get());
    }

    @Test
    void shouldHandleContextPathNotSet() {
        assertEquals("portal", contextPathProvider.get());
    }

}
