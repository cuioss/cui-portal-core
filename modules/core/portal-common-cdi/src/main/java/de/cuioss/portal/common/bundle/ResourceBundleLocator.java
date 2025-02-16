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

import static de.cuioss.portal.common.PortalCommonCDILogMessages.BUNDLE;

import de.cuioss.tools.logging.CuiLogger;

import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Used for configuring ResourceBundles. Implementations should provide a
 * {@link jakarta.annotation.Priority}. Because of the overwriting mechanics a higher
 * {@link jakarta.annotation.Priority} of one of the concrete bundles results in a higher priority
 * of said bundles, resulting in the key to be chosen of the ones with the
 * higher ordering. Number higher than 100 should always be reserved for
 * assemblies / applications
 *
 * @author Matthias Walliczek
 */
public interface ResourceBundleLocator extends Serializable {

    /**
     * Logger for this class
     */
    CuiLogger LOGGER = new CuiLogger(ResourceBundleLocator.class);

    /**
     * <p>getBundlePath.</p>
     *
     * @return paths of the resource bundles if it can be loaded. <em>Caution: </em>
     *         {@link de.cuioss.portal.common.bundle.ResourceBundleRegistry} assumes that only loadable paths are
     *         to be returned. Therefore, each implementation must take care.
     */
    Optional<String> getBundlePath();

    /**
     * <p>getBundle.</p>
     *
     * @return an {@link java.util.Optional} {@link java.util.ResourceBundle} derived by the path of
     *         {@link #getBundlePath()}
     * @param locale a {@link java.util.Locale} object
     */
    default Optional<ResourceBundle> getBundle(Locale locale) {
        var bundlePath = getBundlePath();
        if (bundlePath.isEmpty()) {
            LOGGER.debug("ResourceBundle path not defined for class: %s", getClass().getName());
            return Optional.empty();
        }

        try {
            var rb = ResourceBundle.getBundle(bundlePath.get(), locale);
            LOGGER.debug("Successfully loaded %s '%s' for locale '%s'", getClass().getName(), bundlePath.get(), locale);
            return Optional.of(rb);
        } catch (MissingResourceException e) {
            LOGGER.warn(BUNDLE.WARN.LOAD_FAILED.format(getClass().getName(), bundlePath.get(), locale));
            return Optional.empty();
        }
    }

    /**
     * This is needed in the context of a Quarkus module. Otherwise, the
     * {@link ResourceBundle} can not be found. The difference to the default
     * implementation is passing {@code Thread.currentThread().getContextClassLoader()}.
     *
     * @param bundlePath must not be null
     * @param locale must not be null
     * @return an {@link Optional} {@link ResourceBundle}
     */
    private Optional<ResourceBundle> getBundleViaCurrentThreadContextClassLoader(String bundlePath, Locale locale) {
        try {
            var rb = ResourceBundle.getBundle(bundlePath, locale, Thread.currentThread().getContextClassLoader());
            LOGGER.debug("Loaded bundle for %s, path=%s, locale=%s", getClass().getName(), bundlePath, locale);
            return Optional.of(rb);
        } catch (MissingResourceException e) {
            LOGGER.warn(e, () -> BUNDLE.WARN.LOAD_FAILED.format(getClass().getName(), bundlePath, locale));
            return Optional.empty();
        }
    }
}
