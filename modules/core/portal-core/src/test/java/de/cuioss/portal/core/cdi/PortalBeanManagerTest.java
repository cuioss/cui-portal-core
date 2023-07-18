package de.cuioss.portal.core.cdi;

import static de.cuioss.portal.core.cdi.PortalBeanManager.resolveBean;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.core.cdi.support.DependentTestBeanWithoutQualifier;
import de.cuioss.portal.core.cdi.support.TestAnnotation;
import de.cuioss.portal.core.cdi.support.TestBeanWithQualifier;
import de.cuioss.portal.core.cdi.support.TestBeanWithQualifierAndPriority10;
import de.cuioss.portal.core.cdi.support.TestBeanWithQualifierAndPriority20;
import de.cuioss.portal.core.cdi.support.TestBeanWithoutQualifier;
import de.cuioss.portal.core.cdi.support.TestInterface;

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

}
