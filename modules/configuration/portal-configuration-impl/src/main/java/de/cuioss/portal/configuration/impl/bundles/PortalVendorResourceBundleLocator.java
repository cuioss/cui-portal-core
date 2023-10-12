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

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;

import de.cuioss.portal.common.bundle.ResourceBundleLocator;
import de.cuioss.portal.common.priority.PortalPriorities;
import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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

    private static final String VENDOR_MESSAGES = "de.cuioss.portal.i18n.vendor-messages";

    private static final long serialVersionUID = -8478481710191113463L;

    @Getter
    private List<String> configuredResourceBundles = immutableList(VENDOR_MESSAGES);

    /**
     * Initializes the bean by loading the {@link ResourceBundle}
     */
    @PostConstruct
    public void initBean() {
        try {
            ResourceBundle.getBundle(VENDOR_MESSAGES, Locale.getDefault());
        } catch (MissingResourceException e) {
            log.info("vendor messages not found at '{}', ignored.", VENDOR_MESSAGES);
            configuredResourceBundles = Collections.emptyList();
        }
    }

}
