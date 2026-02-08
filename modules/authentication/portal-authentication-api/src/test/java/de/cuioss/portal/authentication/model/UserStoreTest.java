/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.authentication.model;

import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeSerializable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserStoreTest implements ShouldBeSerializable<UserStore> {

    @Override
    public UserStore getUnderTest() {
        return new UserStore("testName", "Test Display Name");
    }

    @Test
    void shouldCreateWithNameAndDisplayName() {
        var store = new UserStore("name", "displayName");
        assertEquals("name", store.getName());
        assertEquals("displayName", store.getDisplayName());
    }

    @Test
    void shouldCreateWithNameOnly() {
        var store = new UserStore("name");
        assertEquals("name", store.getName());
        assertEquals("name", store.getDisplayName());
    }

    @Test
    void shouldRejectNullName() {
        assertThrows(NullPointerException.class, () -> new UserStore(null));
    }

    @Test
    void shouldRejectNullDisplayName() {
        assertThrows(NullPointerException.class, () -> new UserStore("name", null));
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        var store1 = new UserStore("name", "display");
        var store2 = new UserStore("name", "display");
        var store3 = new UserStore("other", "display");

        assertEquals(store1, store2);
        assertEquals(store1.hashCode(), store2.hashCode());
        assertNotEquals(store1, store3);
    }

    @Test
    void shouldImplementToString() {
        var store = new UserStore("name", "display");
        var toString = store.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("name"));
        assertTrue(toString.contains("display"));
    }
}
