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

import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;

import de.cuioss.portal.configuration.bundles.PortalResourceBundleLocator;
import de.cuioss.portal.configuration.bundles.ResourceBundleLocator;
import de.cuioss.portal.configuration.common.PortalPriorities;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Defines the base bundles to be defined within a portal application, that are
 * "de.cuioss.portal.ui.i18n.portal-messages", "de.icw.cui.core.l18n.messages",
 * "de.cuioss.jsf.api.core.l18n.messages" with the Priority
 * {@link PortalPriorities#PORTAL_CORE_LEVEL}
 *
 * @author Matthias Walliczek
 */
@PortalResourceBundleLocator
@Priority(PortalPriorities.PORTAL_CORE_LEVEL)
@ApplicationScoped
@EqualsAndHashCode
@ToString
public class PortalDefaultResourceBundleLocator implements ResourceBundleLocator {

    private static final long serialVersionUID = -8478481710191113463L;

    @Getter
    private final List<String> configuredResourceBundles = immutableList("de.cuioss.portal.ui.i18n.portal-messages",
            "de.cuioss.jsf.api.core.l18n.messages");
}
