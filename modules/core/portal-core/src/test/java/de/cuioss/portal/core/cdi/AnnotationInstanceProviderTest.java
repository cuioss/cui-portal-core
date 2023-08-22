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
package de.cuioss.portal.core.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertNotEquals(literal, AnnotationInstanceProvider.of(Deprecated.class));
        assertNotEquals(SuppressWarnings.class, literal);
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
