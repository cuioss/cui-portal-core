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
