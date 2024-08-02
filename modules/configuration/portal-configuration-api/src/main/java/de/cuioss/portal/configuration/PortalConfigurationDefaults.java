/**
 *
 */
package de.cuioss.portal.configuration;

import de.cuioss.portal.common.bundle.ResourceBundleWrapper;
import lombok.experimental.UtilityClass;

/**
 * Defines Defaults for the portal-configuration system
 */
@UtilityClass
public class PortalConfigurationDefaults {

    /**
     * Defines the path for the optional bundle, that can be used for overwriting /
     * extending the portal-built in messages. The content will be included to the
     * Portal unified ResourceBundle, see {@link ResourceBundleWrapper}
     */
    public static final String CUSTOM_BUNDLE_PATH = "i18n.custom-messages";

}
