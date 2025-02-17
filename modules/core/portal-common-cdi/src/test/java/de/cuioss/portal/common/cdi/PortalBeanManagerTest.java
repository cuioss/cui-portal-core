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
package de.cuioss.portal.common.cdi;

import static de.cuioss.portal.common.cdi.PortalBeanManager.resolveBean;
import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.common.cdi.support.*;
import jakarta.enterprise.context.RequestScoped;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@EnableAutoWeld
@AddBeanClasses({TestBeanWithQualifier.class, TestBeanWithoutQualifier.class, DependentTestBeanWithoutQualifier.class,
        TestBeanWithQualifierAndPriority10.class, TestBeanWithQualifierAndPriority20.class})
@ActivateScopes(RequestScoped.class)
@DisplayName("Tests the PortalBeanManager functionality")
class PortalBeanManagerTest {

    @Nested
    @DisplayName("Bean Resolution with Qualifier Tests")
    class QualifierTests {

        @Test
        @DisplayName("Should resolve normal scoped bean with qualifier")
        void shouldReturnNormalScopedBeanWithQualifier() {
            final Optional<TestBeanWithQualifier> resolved =
                    resolveBean(TestBeanWithQualifier.class, TestAnnotation.class);

            assertAll("Bean resolution verification",
                    () -> assertTrue(resolved.isPresent(), "Bean should be resolved"),
                    () -> assertEquals(TestBeanWithQualifier.MESSAGE, resolved.get().getInitMessage(),
                            "Should have correct initialization message")
            );
        }

        @Test
        @DisplayName("Should resolve bean with qualifier and highest priority via interface")
        void shouldReturnNormalScopedBeanWithQualifierAndHighestPriorityViaInterface() {
            final Optional<TestInterface> resolved = resolveBean(TestInterface.class, TestAnnotation.class);

            assertAll("Priority bean resolution verification",
                    () -> assertTrue(resolved.isPresent(), "Bean should be resolved"),
                    () -> assertInstanceOf(TestBeanWithQualifierAndPriority20.class, resolved.get(),
                            "Should resolve to highest priority implementation"),
                    () -> assertEquals(TestBeanWithQualifierAndPriority20.MESSAGE, resolved.get().getInitMessage(),
                            "Should have correct initialization message")
            );
        }

        @Test
        @DisplayName("Should resolve normal scoped bean with qualifier without BeanManager")
        void shouldReturnNormalScopedBeanWithQualifierWOBeanManager() {
            final Optional<TestBeanWithQualifier> resolved =
                    resolveBean(TestBeanWithQualifier.class, TestAnnotation.class);

            assertAll("Bean resolution without BeanManager verification",
                    () -> assertTrue(resolved.isPresent(), "Bean should be resolved"),
                    () -> assertEquals(TestBeanWithQualifier.MESSAGE, resolved.get().getInitMessage(),
                            "Should have correct initialization message")
            );
        }
    }

    @Nested
    @DisplayName("Bean Resolution without Qualifier Tests")
    class NoQualifierTests {

        @Test
        @DisplayName("Should resolve normal scoped bean without qualifier")
        void shouldReturnNormalScopedBeanWithoutQualifier() {
            final Optional<TestBeanWithoutQualifier> resolved =
                    resolveBean(TestBeanWithoutQualifier.class, null);

            assertAll("Bean without qualifier verification",
                    () -> assertTrue(resolved.isPresent(), "Bean should be resolved"),
                    () -> assertEquals(TestBeanWithoutQualifier.MESSAGE, resolved.get().getInitMessage(),
                            "Should have correct initialization message")
            );
        }

        @Test
        @DisplayName("Should resolve required bean without qualifier")
        void shouldReturnSimpleRequiredWithoutQualifier() {
            final var resolved = PortalBeanManager.resolveRequiredBean(TestBeanWithoutQualifier.class);

            assertAll("Required bean resolution verification",
                    () -> assertNotNull(resolved, "Required bean should not be null"),
                    () -> assertEquals(TestBeanWithoutQualifier.MESSAGE, resolved.getInitMessage(),
                            "Should have correct initialization message")
            );
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should throw exception when no beans found")
        void shouldThrowExceptionOnZeroBeansFound() {
            var thrown = assertThrows(IllegalArgumentException.class,
                    () -> resolveBean(TestBeanWithoutQualifier.class, TestAnnotation.class),
                    "Should throw IllegalArgumentException when no beans found");

            assertTrue(thrown.getMessage().contains("No bean of type"),
                    "Exception message should indicate no bean was found");
        }

        @Test
        @DisplayName("Should throw exception for null bean class")
        void shouldThrowExceptionOnNullBeanClass() {
            assertThrows(NullPointerException.class,
                    () -> resolveBean(null, TestAnnotation.class),
                    "Should throw NullPointerException for null bean class");
        }
    }
}
