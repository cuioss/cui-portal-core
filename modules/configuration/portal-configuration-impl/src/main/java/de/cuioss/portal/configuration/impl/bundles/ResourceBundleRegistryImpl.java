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
package de.cuioss.portal.configuration.impl.bundles;

import static de.cuioss.tools.collect.CollectionLiterals.mutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import de.cuioss.portal.configuration.bundles.PortalResourceBundleLocator;
import de.cuioss.portal.configuration.bundles.PortalResourceBundleRegistry;
import de.cuioss.portal.configuration.bundles.ResourceBundleLocator;
import de.cuioss.portal.configuration.bundles.ResourceBundleRegistry;
import de.cuioss.portal.configuration.common.PortalPriorities;
import de.cuioss.tools.collect.CollectionLiterals;
import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Registry for the ResourceBundleNames. The injected
 * {@link ResourceBundleLocator} must have unique paths and define an existing
 * {@link ResourceBundle}
 *
 * @author Oliver Wolff
 */
@Named
@ApplicationScoped
@EqualsAndHashCode(of = "resolvedPaths", doNotUseGetters = true)
@ToString(of = "resolvedPaths", doNotUseGetters = true)
@PortalResourceBundleRegistry
public class ResourceBundleRegistryImpl implements ResourceBundleRegistry {

    private static final long serialVersionUID = 2611987921899581695L;

    private static final CuiLogger log = new CuiLogger(ResourceBundleRegistryImpl.class);

    /** "Portal-506: Duplicate ResourceBundlePath found for '{}'" */
    public static final String ERR_DUPLICATE_RESOURCE_PATH = "Portal-506: Duplicate ResourceBundlePath found for '{}'";

    /** "Portal-507: No ResourceBundle found with path " */
    public static final String ERR_NO_RESOURCE_FOUND = "Portal-507: No ResourceBundle found with path ";

    @Inject
    @PortalResourceBundleLocator
    Instance<ResourceBundleLocator> locatorList;

    /**
     * The computed / resolved names in the correct order
     */
    @Getter
    private List<String> resolvedPaths;

    /**
     * Initializes the bean. See class documentation for expected result.
     */
    @PostConstruct
    public void initBean() {

        var defaultLocale = Locale.getDefault();
        final List<String> finalPaths = new ArrayList<>();
        // Sort according to ResourceBundleDescripor#order
        final List<ResourceBundleLocator> sortedLocators = PortalPriorities.sortByPriority(mutableList(locatorList));
        for (final ResourceBundleLocator descriptor : sortedLocators) {
            for (final String path : descriptor.getConfiguredResourceBundles()) {
                // Check whether the path defines an existing ResourceBundle
                try {
                    ResourceBundle.getBundle(path, defaultLocale);
                    // Check whether path is unique
                    if (finalPaths.contains(path)) {
                        log.error(ERR_DUPLICATE_RESOURCE_PATH, path);
                    }
                    finalPaths.add(path);
                } catch (MissingResourceException e) {
                    log.error(ERR_NO_RESOURCE_FOUND + path, e);
                }
            }
        }
        resolvedPaths = CollectionLiterals.immutableList(finalPaths);
    }

}
