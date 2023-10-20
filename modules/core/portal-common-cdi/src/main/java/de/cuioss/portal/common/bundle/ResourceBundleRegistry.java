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

import static de.cuioss.tools.collect.CollectionLiterals.mutableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import de.cuioss.portal.common.priority.PortalPriorities;
import de.cuioss.tools.collect.CollectionLiterals;
import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Registry for the ResourceBundleNames. The injected
 * {@link ResourceBundleLocator}s must have unique paths and define an existing
 * {@link ResourceBundle}. In addition they should be annotated with the
 * corresponding {@link Priority}
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@EqualsAndHashCode(of = "resolvedPaths", doNotUseGetters = true)
@ToString(of = "resolvedPaths", doNotUseGetters = true)
public class ResourceBundleRegistry implements Serializable {

    private static final long serialVersionUID = 2611987921899581695L;

    private static final CuiLogger log = new CuiLogger(ResourceBundleRegistry.class);

    /** "Portal-506: Duplicate ResourceBundlePath found for '{}'" */
    public static final String ERR_DUPLICATE_RESOURCE_PATH = "Portal-506: Duplicate ResourceBundlePath found for '{}'";

    @Inject
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
    void initBean() {
        final List<String> finalPaths = new ArrayList<>();
        // Sort according to ResourceBundleDescripor#order
        final List<ResourceBundleLocator> sortedLocators = PortalPriorities.sortByPriority(mutableList(locatorList));
        for (final ResourceBundleLocator descriptor : sortedLocators) {
            var path = descriptor.getBundlePath();
            if (path.isPresent()) {
                // Check whether the path defines an existing ResourceBundle
                // Check whether path is unique
                if (finalPaths.contains(path.get())) {
                    log.error(ERR_DUPLICATE_RESOURCE_PATH, path);
                }
                finalPaths.add(path.get());
            } else {
                log.debug("No valid path given, ignoring");
            }
        }
        log.debug("Determined ResourceBundle-Oath: '%s'", finalPaths);
        resolvedPaths = CollectionLiterals.immutableList(finalPaths);

    }

}
