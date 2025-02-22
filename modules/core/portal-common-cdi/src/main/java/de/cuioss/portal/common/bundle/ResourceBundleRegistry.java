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
import de.cuioss.portal.common.priority.PortalPriorities;
import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static de.cuioss.tools.collect.CollectionLiterals.mutableList;

/**
 * Application-scoped registry managing and validating {@link ResourceBundleLocator}s.
 * 
 * <h2>Overview</h2>
 * The registry collects and manages all available {@link ResourceBundleLocator}s,
 * ensuring they are properly ordered by priority and validated for correctness.
 * 
 * <h2>Initialization Process</h2>
 * During {@link PostConstruct}, the registry:
 * <ol>
 *   <li>Collects all CDI-injected {@link ResourceBundleLocator}s</li>
 *   <li>Sorts them by {@link jakarta.annotation.Priority}</li>
 *   <li>Validates each locator's bundle existence and path</li>
 *   <li>Ensures path uniqueness across all locators</li>
 *   <li>Creates an immutable list of valid locators</li>
 * </ol>
 * 
 * <h2>Usage</h2>
 * <pre>
 * &#064;Inject
 * private ResourceBundleRegistry registry;
 * 
 * public void process() {
 *     List&lt;ResourceBundleLocator&gt; locators = registry.getResolvedPaths();
 *     // Process locators in priority order
 * }
 * </pre>
 * 
 * <h2>Thread Safety</h2>
 * This bean is thread-safe. After initialization, it provides an immutable view
 * of the validated locators.
 *
 * @author Oliver Wolff
 * @see ResourceBundleLocator
 * @see jakarta.annotation.Priority
 */
@ApplicationScoped
@ToString
@EqualsAndHashCode(of = "resolvedPaths")
public class ResourceBundleRegistry implements Serializable {

    @Serial
    private static final long serialVersionUID = 2611987921899581695L;

    private static final CuiLogger LOGGER = new CuiLogger(ResourceBundleRegistry.class);

    @Inject
    Instance<ResourceBundleLocator> locatorList;

    /**
     * The computed / resolved locators in priority order. This list is immutable and
     * contains only valid locators with unique paths.
     */
    @Getter
    @NonNull
    private List<ResourceBundleLocator> resolvedPaths;

    /**
     * Initializes the bean by processing all injected {@link ResourceBundleLocator}s.
     * The initialization process:
     * <ul>
     *   <li>Sorts locators by priority</li>
     *   <li>Validates each locator's bundle and path</li>
     *   <li>Ensures path uniqueness</li>
     *   <li>Creates an immutable list of valid locators</li>
     * </ul>
     *
     * @throws IllegalStateException if no valid resource bundles are found
     */
    @PostConstruct
    @SuppressWarnings("java:S3655")
    // owolff: false positive - isPresent is checked
    void initBean() {
        final var validLocators = new CollectionBuilder<ResourceBundleLocator>();
        var locators = mutableList(locatorList);
        final var prioritizedLocators = PortalPriorities.sortByPriority(locators);
        final var registeredPaths = new ArrayList<String>();

        for (final ResourceBundleLocator locator : prioritizedLocators) {
            var resolvedBundle = locator.getBundle(Locale.getDefault());
            if (resolvedBundle.isPresent() && locator.getBundlePath().isPresent()) {
                var bundlePath = locator.getBundlePath().get();
                if (registeredPaths.contains(bundlePath)) {
                    LOGGER.warn(PortalCommonLogMessages.WARN.DUPLICATE_PATH.format(bundlePath));
                } else {
                    LOGGER.debug("Adding bundle path %s", bundlePath);
                    validLocators.add(locator);
                    registeredPaths.add(bundlePath);
                }
            } else {
                LOGGER.warn(PortalCommonLogMessages.WARN.MISSING_PATH.format(locator.getClass().getName()));
            }
        }

        resolvedPaths = validLocators.toImmutableList();

        if (resolvedPaths.isEmpty()) {
            LOGGER.warn(PortalCommonLogMessages.WARN.NO_VALID_BUNDLES.format());
        } else {
            LOGGER.debug("Resulting bundle paths: %s", resolvedPaths.stream()
                    .map(loc -> loc.getBundlePath().orElse("undefined"))
                    .collect(Collectors.joining(", ")));
        }
    }
}
