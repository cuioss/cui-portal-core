package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_PRODUCTION_FILENAME_DEFAULT;

/**
 * Configuration source for the application-production.yml file.
 *
 * @author Sven Haag, Sven Haag
 */
public class InstallationProductionFileConfigSource extends AbstractInstallationConfigSource {

    /** The name of this MicroProfile ConfigSource */
    public static final String NAME = "ApplicationProductionYamlConfigSource";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    String getFileName() {
        return PORTAL_CONFIG_PRODUCTION_FILENAME_DEFAULT;
    }

    @Override
    protected boolean isRequired() {
        return false;
    }


    @Override
    public int getPortalPriority() {
        return super.getPortalPriority() + 5;
    }
}
