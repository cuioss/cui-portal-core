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

import de.cuioss.portal.common.PortalCommonCDILogMessages;
import de.cuioss.portal.common.priority.PortalPriorities;
import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static de.cuioss.tools.collect.CollectionLiterals.mutableList;

/**
 * Registry for all available {@link ResourceBundleLocator}s.
 * The injected {@link ResourceBundleLocator}s must have unique paths and define an existing
 * resource bundle. The registry will sort them according to their {@link jakarta.annotation.Priority}
 * annotation.
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@EqualsAndHashCode(of = "resolvedPaths", doNotUseGetters = true)
@ToString(of = "resolvedPaths", doNotUseGetters = true)
public class ResourceBundleRegistry implements Serializable {

    @Serial
    private static final long serialVersionUID = 2611987921899581695L;

    private static final CuiLogger LOGGER = new CuiLogger(ResourceBundleRegistry.class);

    @Inject
    Instance<ResourceBundleLocator> locatorList;

    /**
     * The computed / resolved names in the correct order
     */
    @Getter
    private List<ResourceBundleLocator> resolvedPaths;

    /**
     * Initializes the bean. See class documentation for expected result.
     */
    @PostConstruct
    @SuppressWarnings("java:S3655")
    // owolff: false positive isPresent is checked
    void initBean() {
        final var finalPaths = new CollectionBuilder<ResourceBundleLocator>();
        // Sort according to ResourceBundleDescriptor#order
        final List<ResourceBundleLocator> sortedLocators = PortalPriorities.sortByPriority(mutableList(locatorList));
        final var foundPaths = new ArrayList<String>();
        for (final ResourceBundleLocator descriptor : sortedLocators) {
            var resolvedBundle = descriptor.getBundle(Locale.getDefault());
            if (resolvedBundle.isPresent() && descriptor.getBundlePath().isPresent()) {
                // Check whether the path is unique
                if (foundPaths.contains(descriptor.getBundlePath().get())) {
                    LOGGER.warn(PortalCommonCDILogMessages.DUPLICATE_RESOURCE_PATH.format(
                            descriptor.getClass().getName()));
                } else {
                    LOGGER.debug(PortalCommonCDILogMessages.ADDING_BUNDLE.format(
                            descriptor.getBundlePath().get()));
                    finalPaths.add(descriptor);
                    foundPaths.add(descriptor.getBundlePath().get());
                }
            } else {
                LOGGER.warn(PortalCommonCDILogMessages.IGNORING_BUNDLE.format(
                        descriptor.getClass().getName()));
            }
        }
        resolvedPaths = finalPaths.toImmutableList();
        LOGGER.debug(PortalCommonCDILogMessages.RESULTING_BUNDLES.format(
                resolvedPaths.stream()
                        .map(loc -> loc.getBundlePath().orElse("undefined"))
                        .collect(Collectors.joining(", "))));
    }
}
