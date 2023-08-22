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
package de.cuioss.portal.core.storage.impl;

import static de.cuioss.test.generator.Generators.integers;
import static de.cuioss.test.generator.Generators.nonEmptyStrings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.cuioss.portal.core.storage.MapStorage;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.valueobjects.ValueObjectTest;

class MapStorageImplTest extends ValueObjectTest<MapStorageImpl<String, String>> {

    private final TypedGenerator<Integer> integerGenerator = integers(0, 10);
    private final TypedGenerator<String> strings = nonEmptyStrings();

    @Override
    protected MapStorageImpl<String, String> anyValueObject() {
        final var mapStorageImpl = new MapStorageImpl<String, String>();
        for (var i = 0; i < integerGenerator.next(); i++) {
            mapStorageImpl.put(strings.next(), strings.next());
        }
        return mapStorageImpl;
    }

    @Test
    void shouldHandleEmptyStorage() {
        final MapStorage<String, String> storage = new MapStorageImpl<>();
        final var key = strings.next();
        final var value = strings.next();

        assertFalse(storage.containsKey(key));
        assertNull(storage.get(key));
        assertEquals(value, storage.get(key, value));
        assertNull(storage.remove(key));
    }

    @Test
    void shouldHandleNullKeysAndValuesGracefully() {
        final MapStorage<String, String> storage = new MapStorageImpl<>();

        final var defaultValue = strings.next();

        assertFalse(storage.containsKey(null));

        // Should not fail
        storage.put(null, null);
        storage.put(defaultValue, null);
        assertNull(storage.get(null));
        assertEquals(defaultValue, storage.get(null, defaultValue));
        assertNull(storage.get(null, null));
        assertNull(storage.remove(null));
    }

    @Test
    void shouldProvideDefaultOperations() {
        final MapStorage<String, String> storage = new MapStorageImpl<>();
        final var key = strings.next();
        final var value = strings.next();

        assertFalse(storage.containsKey(key));
        assertNull(storage.get(key));
        assertNull(storage.remove(key));

        storage.put(key, value);
        assertTrue(storage.containsKey(key));
        assertEquals(value, storage.get(key));
        assertEquals(value, storage.get(key, ""));
        assertEquals(value, storage.remove(key));

        assertFalse(storage.containsKey(key));
        assertNull(storage.get(key));
        assertNull(storage.remove(key));
    }
}
