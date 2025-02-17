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
package de.cuioss.portal.common.bundle;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

/**
 * Core interface for accessing unified resource bundles in the Portal.
 * Provides functionality similar to {@link java.util.ResourceBundle} but with
 * additional Portal-specific features.
 * 
 * <p>The wrapper combines multiple resource bundles into a single access point,
 * supporting dynamic locale changes and fallback mechanisms.
 * 
 * <h2>Thread Safety</h2>
 * Implementations must be thread-safe and support concurrent access.
 * 
 * <h2>Usage</h2>
 * <pre>
 * &#064;Inject
 * private ResourceBundleWrapper bundleWrapper;
 * 
 * // Get a message
 * String message = bundleWrapper.getString("key.name");
 * 
 * // Check if key exists
 * if (bundleWrapper.containsKey("key.name")) {
 *     // Handle existing key
 * }
 * </pre>
 *
 * @author Oliver Wolff
 */
public interface ResourceBundleWrapper extends Serializable {

    /**
     * Resolves a message for the given key.
     *
     * @param key to be looked up, must not be {@code null}
     * @return the resolved message if key is defined, otherwise returns "??[key]??"
     *         and logs a warning
     * @throws NullPointerException if key is {@code null}
     */
    String getString(String key);

    /**
     * Technical method for {@link java.util.ResourceBundle} compatibility.
     * Provides access to all available keys in this bundle and its parents.
     *
     * @return an {@link Enumeration} of all keys in this bundle and its parents,
     *         never {@code null}
     * @see java.util.ResourceBundle#getKeys()
     */
    default Enumeration<String> getKeys() {
        return Collections.enumeration(keySet());
    }

    /**
     * <p>keySet.</p>
     *
     * @return set of all supported keys of this bundle
     */
    Set<String> keySet();

    /**
     * <p>getBundleContent.</p>
     *
     * @return a list of all configured bundles to be used for debugging.
     */
    String getBundleContent();

}
