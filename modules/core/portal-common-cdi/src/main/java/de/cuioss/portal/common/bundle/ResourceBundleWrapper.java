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
 * Defines the portal-contract dealing with UnifiedResourceBundle
 * Technically it provides an interface similar to {@link java.util.ResourceBundle}
 *
 * @author Oliver Wolff
 */
public interface ResourceBundleWrapper extends Serializable {

    /**
     * Returns resolved message by given key.
     *
     * @param key to be looked up
     * @return the resolved message if key is defined, otherwise "??[key value]??"
     *         will be returned and warning will be written
     */
    String getString(String key);

    /**
     * Technical method for {@link java.util.ResourceBundle} caching the resolved keys and
     * values @see {@link java.util.ResourceBundle#getKeys()}
     *
     * @return an Enumeration of the keys contained in this ResourceBundle and its
     *         parent bundles.
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
