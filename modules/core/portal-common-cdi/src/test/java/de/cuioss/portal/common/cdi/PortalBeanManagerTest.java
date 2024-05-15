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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import jakarta.enterprise.context.RequestScoped;

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.common.cdi.support.DependentTestBeanWithoutQualifier;
import de.cuioss.portal.common.cdi.support.TestAnnotation;
import de.cuioss.portal.common.cdi.support.TestBeanWithQualifier;
import de.cuioss.portal.common.cdi.support.TestBeanWithQualifierAndPriority10;
import de.cuioss.portal.common.cdi.support.TestBeanWithQualifierAndPriority20;
import de.cuioss.portal.common.cdi.support.TestBeanWithoutQualifier;
import de.cuioss.portal.common.cdi.support.TestInterface;

@EnableAutoWeld
@AddBeanClasses({ TestBeanWithQualifier.class, TestBeanWithoutQualifier.class, DependentTestBeanWithoutQualifier.class,
        TestBeanWithQualifierAndPriority10.class, TestBeanWithQualifierAndPriority20.class })
@ActivateScopes(RequestScoped.class)
class PortalBeanManagerTest {

    @Test
    void shouldReturnNormalScopedBeanWithQualifier() {
        final Optional<TestBeanWithQualifier> check = resolveBean(TestBeanWithQualifier.class, TestAnnotation.class);
        assertTrue(check.isPresent());
        assertEquals(TestBeanWithQualifier.MESSAGE, check.get().getInitMessage());
    }

    @Test
    void shouldReturnNormalScopedBeanWithQualifierAndHighestPriorityViaInterface() {
        final Optional<TestInterface> check = resolveBean(TestInterface.class, TestAnnotation.class);
        assertTrue(check.isPresent());
        assertTrue(check.get() instanceof TestBeanWithQualifierAndPriority20);
    }

    @Test
    void shouldReturnNormalScopedBeanWithQualifierWOBeanManager() {
        final Optional<TestBeanWithQualifier> check = resolveBean(TestBeanWithQualifier.class, TestAnnotation.class);
        assertTrue(check.isPresent());
        assertEquals(TestBeanWithQualifier.MESSAGE, check.get().getInitMessage());
    }

    @Test
    void shouldReturnNormalScopedBeanWithoutQualifier() {
        final Optional<TestBeanWithoutQualifier> check = resolveBean(TestBeanWithoutQualifier.class, null);
        assertTrue(check.isPresent());
        assertEquals(TestBeanWithQualifier.MESSAGE, check.get().getInitMessage());
    }

    @Test
    void shouldThrowExceptionOnZeroBeansFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            resolveBean(TestBeanWithoutQualifier.class, TestAnnotation.class);
        });
    }

    @Test
    void shouldReturnSimpleRequiredWithoutQualifier() {
        final var check = PortalBeanManager.resolveRequiredBean(TestBeanWithoutQualifier.class);
        assertNotNull(check);
        assertEquals(TestBeanWithQualifier.MESSAGE, check.getInitMessage());
    }

}
