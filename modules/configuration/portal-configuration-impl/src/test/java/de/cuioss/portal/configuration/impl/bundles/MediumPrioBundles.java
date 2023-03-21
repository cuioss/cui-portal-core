package de.cuioss.portal.configuration.impl.bundles;

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;

import java.util.List;

import javax.annotation.Priority;

import de.cuioss.portal.configuration.bundles.PortalResourceBundleLocator;
import de.cuioss.portal.configuration.bundles.ResourceBundleLocator;
import de.cuioss.portal.configuration.common.PortalPriorities;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@PortalResourceBundleLocator
@Priority(PortalPriorities.PORTAL_MODULE_LEVEL)
@EqualsAndHashCode
@ToString
@SuppressWarnings("javadoc")
public class MediumPrioBundles implements ResourceBundleLocator {

    private static final long serialVersionUID = 7756501560722570148L;

    public static final String MEDIUM_1 = "com.icw.ehf.cui.l18n.messages.medium1";

    public static final String MEDIUM_2 = "com.icw.ehf.cui.l18n.messages.medium2";

    @Override
    public List<String> getConfiguredResourceBundles() {
        return immutableList(MEDIUM_1, MEDIUM_2);
    }

}
