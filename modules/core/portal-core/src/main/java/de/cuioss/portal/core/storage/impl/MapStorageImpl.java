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

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.cuioss.portal.core.storage.MapStorage;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Default Implementation of {@link MapStorage}
 * <p>
 * Map like wrapper around session storage. Analogous to
 * com.icw.ehf.cui.application.storage.SessionStorage
 * </p>
 * the actual map is backed by {@link ConcurrentHashMap}, therefore there is no
 * need to synchronize the access.
 *
 * @param <T> type of key. Must implement at least {@link Serializable}
 * @param <V> value, must implement at least {@link Serializable}
 */
@EqualsAndHashCode
@ToString
public class MapStorageImpl<T extends Serializable, V extends Serializable> implements MapStorage<T, V> {

    private static final long serialVersionUID = 6490210131979783018L;

    /** Storage for session persisted objects. */
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
