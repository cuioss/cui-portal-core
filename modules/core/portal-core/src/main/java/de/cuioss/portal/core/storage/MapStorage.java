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
package de.cuioss.portal.core.storage;

import java.io.Serializable;

/**
 * Map like wrapper around session storage.
 *
 * @author Oliver Wolff
 * @param <T> type of key. Must implement at least {@link Serializable}
 * @param <V> value, must implement at least {@link Serializable}
 */
public interface MapStorage<T extends Serializable, V extends Serializable> extends Serializable {

    /**
     * Returns object for the associated key.
     *
     * @param key with which the specified value is associated. If it is
     *            <code>null</code> <code>null</code> will be returned
     * @return value is associated with the specified key. May be <code>null</code>.
     */
    V get(T key);

    /**
     * Returns object for the associated key.
     *
     * @param key          with which the specified value is to be associated. If it
     *                     is <code>null</code> the default value will be returned
     * @param defaultValue to be returned if the value is <code>null</code>.
     * @return value associated with the specified key. If it is <code>null</code>
     *         the default value will be returned
     */
    V get(T key, V defaultValue);

    /**
     * Put a new object in storage.
     *
     * @param key    key with which the specified value is to be associated. If it
     *               is <code>null</code> the call will be ignored.
     * @param object - value to be associated with the specified key. If it is
     *               <code>null</code> the call will be ignored.
     */
    void put(T key, V object);

    /**
     * Removes the object from storage for the key.
     *
     * @param key key with which the specified value is to be associated.
     * @return object to which the key was associated.
     */
    V remove(T key);

    /**
     * Returns true if this map contains a mapping for the specified key. More
     * formally, returns true if and only if this map contains a mapping for a key k
     * such that (key==null ? k==null : key.equals(k)). (There can be at most one
     * such mapping.)
     *
     * @param key key whose presence in this map is to be tested If it is
     *            <code>null</code> it will return false.
     * @return true if this map contains a mapping for the specified key
     */
    boolean containsKey(T key);

}
