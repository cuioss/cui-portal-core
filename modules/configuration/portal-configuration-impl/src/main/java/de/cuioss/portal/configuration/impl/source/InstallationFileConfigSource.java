package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_FILENAME_DEFAULT;

/**
 * Configuration source for the application.yml file.
 *
 * @author Sven Haag, Sven Haag
 */
public class InstallationFileConfigSource extends AbstractInstallationConfigSource {

    /** The name of this MicroProfile ConfigSource */
    public static final String NAME = "ApplicationYamlConfigSource";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    String getFileName() {
        return PORTAL_CONFIG_FILENAME_DEFAULT;
    }
}
