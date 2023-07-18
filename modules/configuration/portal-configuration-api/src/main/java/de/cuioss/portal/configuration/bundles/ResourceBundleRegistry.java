package de.cuioss.portal.configuration.bundles;

import java.io.Serializable;
import java.util.List;

/**
 * Registry for the ResourceBundleNames. Provides the resulting list of
 * configured resource bundles ordered by priority.
 *
 * @author Matthias Walliczek
 */
public interface ResourceBundleRegistry extends Serializable {

    /**
     * @return The computed / resolved names in the correct order
     */
    List<String> getResolvedPaths();

}
