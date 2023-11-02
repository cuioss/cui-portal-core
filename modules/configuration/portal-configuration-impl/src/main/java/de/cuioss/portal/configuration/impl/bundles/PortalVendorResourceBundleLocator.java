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

import static de.cuioss.portal.configuration.PortalConfigurationDefaults.CUSTOM_BUNDLE_PATH;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;

import de.cuioss.portal.common.bundle.ResourceBundleLocator;
import de.cuioss.portal.common.priority.PortalPriorities;
import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Defines the vendor specific bundle to be defined within a portal application,
 * that is "de.cuioss.portal.i18n.vendor-messages" with the Priority
 * {@link PortalPriorities#PORTAL_ASSEMBLY_LEVEL + 10}
 *
 * @author Matthias Walliczek
 */
@Priority(PortalPriorities.PORTAL_ASSEMBLY_LEVEL + 10)
@ApplicationScoped
@EqualsAndHashCode
@ToString
public class PortalVendorResourceBundleLocator implements ResourceBundleLocator {

    private static final CuiLogger log = new CuiLogger(PortalVendorResourceBundleLocator.class);

    private static final long serialVersionUID = -8478481710191113463L;

    private String bundle;

    /**
     * Initializes the bean by loading the {@link ResourceBundle}
     */
    @PostConstruct
    public void initBean() {
        try {
            ResourceBundle.getBundle(CUSTOM_BUNDLE_PATH, Locale.getDefault());
            bundle = CUSTOM_BUNDLE_PATH;
            log.info("Custom messages found at '{}', ignoring.", CUSTOM_BUNDLE_PATH);
        } catch (MissingResourceException e) {
            log.info("Custom messages not found at '{}', ignoring.", CUSTOM_BUNDLE_PATH);
            bundle = null;
        }
    }

    @Override
    public Optional<String> getBundlePath() {
        return Optional.ofNullable(bundle);
    }

    @Override
    public String toString() {
        return "%s: Path='%s'".formatted(getClass().getName(), CUSTOM_BUNDLE_PATH);
    }

}
