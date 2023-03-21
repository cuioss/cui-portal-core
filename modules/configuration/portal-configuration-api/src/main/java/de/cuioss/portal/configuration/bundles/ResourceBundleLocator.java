package de.cuioss.portal.configuration.bundles;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Priority;

/**
 * Utilized for configuring ResourceBundles. implementations should provide a
 * {@link Priority}. Because of the overwriting mechanics a higher
 * {@link Priority} of one of the concrete bundles results in a higher priority
 * of said bundles, resulting in the key to be chosen of the ones with the
 * higher ordering. Number higher than 100 should always be reserved for
 * assemblies / applications
 *
 * @author Matthias Walliczek
 */
public interface ResourceBundleLocator extends Serializable {

    /**
     * @return paths of the resource bundles
     */
    List<String> getConfiguredResourceBundles();

}
