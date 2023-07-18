package de.cuioss.portal.core.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import de.cuioss.tools.collect.CollectionLiterals;

class AnnotationInstanceProviderTest {

    @SuppressWarnings({ "unlikely-arg-type", "java:S5785", "java:S5863" })
    @Test
    void shouldHandleHappyCase() {
        var literal = AnnotationInstanceProvider.of(SuppressWarnings.class);
        assertNotNull(literal.toString());
        assertEquals(AnnotationInstanceProvider.of(SuppressWarnings.class), literal);
        assertEquals(0, literal.hashCode());
        assertEquals(literal, literal);
        assertFalse(literal.equals(AnnotationInstanceProvider.of(Deprecated.class)));
        assertFalse(literal.equals(SuppressWarnings.class));
        assertEquals(SuppressWarnings.class, literal.annotationType());
    }

    @Test
    void shouldHandleExtendedCase() {
        var literal = AnnotationInstanceProvider.of(SuppressWarnings.class,
                CollectionLiterals.immutableMap("value", "test"));
        assertNotNull(literal.toString());
        assertEquals(
                AnnotationInstanceProvider.of(SuppressWarnings.class, CollectionLiterals.immutableMap("value", "test")),
                literal);
        assertNotEquals(0, literal.hashCode());
    }

}
