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

import de.cuioss.portal.configuration.bundles.PortalResourceBundleLocator;
import de.cuioss.portal.configuration.bundles.ResourceBundleLocator;
import de.cuioss.portal.configuration.common.PortalPriorities;
import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Defines the vendor specific bundle to be defined within a portal application, that is
 * "com.icw.ehf.cui.portal.i18n.vendor-messages" with the
 * Priority {@link PortalPriorities#PORTAL_ASSEMBLY_LEVEL + 10}
 *
 * @author Matthias Walliczek
 */
@PortalResourceBundleLocator
@Priority(PortalPriorities.PORTAL_ASSEMBLY_LEVEL + 10)
@ApplicationScoped
@EqualsAndHashCode
@ToString
public class PortalVendorResourceBundleLocator implements ResourceBundleLocator {

    private static final CuiLogger log = new CuiLogger(PortalVendorResourceBundleLocator.class);

    private static final String VENDOR_MESSAGES = "com.icw.ehf.cui.portal.i18n.vendor-messages";

    private static final long serialVersionUID = -8478481710191113463L;

    @Getter
    private List<String> configuredResourceBundles =
        immutableList(VENDOR_MESSAGES);

    /**
     * Initializes the bean beand by loading the {@link ResourceBundle}
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
