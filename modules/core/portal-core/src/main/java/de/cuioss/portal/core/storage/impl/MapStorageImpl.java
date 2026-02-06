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
package de.cuioss.portal.core.storage.impl;

import de.cuioss.portal.core.storage.MapStorage;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe implementation of {@link MapStorage} providing key-value storage capabilities.
 * 
 * <p>This implementation:</p>
 * <ul>
 *   <li>Is backed by {@link ConcurrentHashMap} for thread-safe operations without explicit synchronization</li>
 *   <li>Handles null keys and values gracefully</li>
 *   <li>Provides atomic operations for common map functions</li>
 *   <li>Ensures type-safety through generic parameters</li>
 * </ul>
 *
 * <p><strong>Key features:</strong></p>
 * <ul>
 *   <li>Thread-safe operations</li>
 *   <li>Null-safe implementations</li>
 *   <li>Serializable storage</li>
 *   <li>Default value support</li>
 * </ul>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>
 * MapStorage&lt;String, UserData&gt; storage = new MapStorageImpl&lt;&gt;();
 * storage.put("user123", userData);
 * UserData data = storage.get("user123", new UserData()); // With default value
 * </pre>
 *
 * @param <T> type of key, must implement {@link Serializable}
 * @param <V> type of value, must implement {@link Serializable}
 *
 * @see MapStorage
 * @see de.cuioss.portal.core.storage.SessionStorage
 * @see Serializable
 * @since 1.0
 */
@EqualsAndHashCode
@ToString
public class MapStorageImpl<T extends Serializable, V extends Serializable> implements MapStorage<T, V> {

    @Serial
    private static final long serialVersionUID = 6490210131979783018L;

    /**
     * Storage for session persisted objects.
     */
    private final Map<T, V> storage = new ConcurrentHashMap<>();

    @Override
    public V get(final T key) {
        if (null == key) {
            return null;
        }
        return storage.get(key);
    }

    @Override
    public V get(final T key, final V defaultValue) {
        var value = get(key);
        if (null == value) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public void put(final T key, final V object) {
        if (null != key && null != object) {
            storage.put(key, object);
        }
    }

    @Override
    public V remove(final T key) {
        if (null == key) {
            return null;
        }
        return storage.remove(key);
    }

    @Override
    public boolean containsKey(final T key) {
        if (null == key) {
            return false;
        }
        return storage.containsKey(key);
    }
}
