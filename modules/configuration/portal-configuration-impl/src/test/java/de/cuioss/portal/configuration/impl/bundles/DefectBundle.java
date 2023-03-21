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
@Priority(PortalPriorities.PORTAL_ASSEMBLY_LEVEL)
@EqualsAndHashCode
@ToString
@SuppressWarnings("javadoc")
public class DefectBundle implements ResourceBundleLocator {

    private static final long serialVersionUID = 7756501560722570148L;

    public static final String HIGH_1 = "com.icw.ehf.cui.l18n.messages.missing";

    @Override
    public List<String> getConfiguredResourceBundles() {
        return immutableList(HIGH_1);
    }
}
