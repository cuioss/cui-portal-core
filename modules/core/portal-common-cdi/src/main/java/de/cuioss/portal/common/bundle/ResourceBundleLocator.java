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
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import de.cuioss.tools.logging.CuiLogger;
import jakarta.annotation.Priority;

/**
 * Used for configuring ResourceBundles. Implementations should provide a
 * {@link Priority}. Because of the overwriting mechanics a higher
 * {@link Priority} of one of the concrete bundles results in a higher priority
 * of said bundles, resulting in the key to be chosen of the ones with the
 * higher ordering. Number higher than 100 should always be reserved for
 * assemblies / applications
 *
 * @author Matthias Walliczek
 */
public interface ResourceBundleLocator extends Serializable {

    CuiLogger LOGGER = new CuiLogger(ResourceBundleLocator.class);

    /**
     * @return paths of the resource bundles if it can be loaded. <em>Caution: </em>
     *         {@link ResourceBundleRegistry} assumes that only loadable paths are
     *         to be returned. Therefore, each implementation must take care.
     */
    Optional<String> getBundlePath();

    /**
     * @return an {@link Optional} {@link ResourceBundle} derived by the path of
     *         {@link #getBundlePath()}
     */
    default Optional<ResourceBundle> getBundle(Locale locale) {
        var bundlePath = getBundlePath();
        if (bundlePath.isEmpty()) {
            LOGGER.debug("No ResourceBundle to be loaded is present");
            return Optional.empty();
        }
        try {
            Optional<ResourceBundle> loadedBundle = Optional.of(ResourceBundle.getBundle(bundlePath.get(), locale));
            LOGGER.debug("Successfully loaded %s '%s' for '%s'", getClass().getName(), bundlePath.get(), locale);
            return loadedBundle;
        } catch (MissingResourceException e) {
            LOGGER.warn("Unable to load %s '%s' for '%s'".formatted(getClass().getName(), bundlePath.get(), locale), e);
            return Optional.empty();
        }
    }

}
