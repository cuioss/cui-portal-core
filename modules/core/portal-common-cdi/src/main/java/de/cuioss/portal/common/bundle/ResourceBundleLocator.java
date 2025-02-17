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

import de.cuioss.portal.common.PortalCommonLogMessages;
import de.cuioss.tools.logging.CuiLogger;

import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Interface for locating and providing {@link ResourceBundle}s in the Portal.
 * 
 * <h2>Overview</h2>
 * Implementations locate and provide access to resource bundles, supporting the
 * Portal's hierarchical bundle resolution system.
 * 
 * <h2>Priority System</h2>
 * Implementations should provide a {@link jakarta.annotation.Priority} to define
 * their position in the resolution chain:
 * <ul>
 *   <li>Higher priority values (higher numbers) take precedence</li>
 *   <li>Values 1-99: Core portal bundles</li>
 *   <li>Values 100+: Reserved for assemblies/applications</li>
 * </ul>
 * 
 * <h2>Implementation Requirements</h2>
 * <ul>
 *   <li>Must be thread-safe</li>
 *   <li>Must handle {@link MissingResourceException} gracefully</li>
 *   <li>Should cache bundles when possible</li>
 *   <li>Must only return valid, loadable bundle paths</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>
 * &#064;Priority(150)
 * &#064;ApplicationScoped
 * public class CustomBundleLocator implements ResourceBundleLocator {
 *     
 *     &#064;Override
 *     public Optional<String> getBundlePath() {
 *         return Optional.of("com.example.messages");
 *     }
 *     
 *     &#064;Override
 *     public ResourceBundle getBundle(Locale locale) {
 *         try {
 *             return ResourceBundle.getBundle(getBundlePath().get(), locale);
 *         } catch (MissingResourceException e) {
 *             LOGGER.warn(e, PortalCommonLogMessages.WARN.LOAD_FAILED.format(
 *                 getBundlePath().get(), locale, e.getMessage()));
 *             return null;
 *         }
 *     }
 * }
 * </pre>
 *
 * @author Matthias Walliczek
 * @see ResourceBundleRegistry
 * @see jakarta.annotation.Priority
 */
public interface ResourceBundleLocator extends Serializable {

    /**
     * Logger for this class
     */
    CuiLogger LOGGER = new CuiLogger(ResourceBundleLocator.class);

    /**
     * Returns the path to the resource bundle.
     *
     * @return Optional containing the bundle path if it exists and can be loaded,
     *         empty Optional otherwise
     * @see ResourceBundleRegistry#initBean()
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
            LOGGER.warn(PortalCommonLogMessages.WARN.LOAD_FAILED.format(getClass().getName(), bundlePath.get(), locale));
            return getBundleViaCurrentThreadContextClassLoader(bundlePath.get(), locale);
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
            LOGGER.warn(e, PortalCommonLogMessages.WARN.LOAD_FAILED.format(getClass().getName(), bundlePath, locale));
            return Optional.empty();
        }
    }
}
