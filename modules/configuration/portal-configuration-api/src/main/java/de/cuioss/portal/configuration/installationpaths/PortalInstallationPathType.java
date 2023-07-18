package de.cuioss.portal.configuration.installationpaths;

import java.nio.file.Path;

/**
 * Describing the type of installation path that was changed.
 *
 * @author Matthias Walliczek
 */
public enum PortalInstallationPathType {

    /** The customization directory was changed */
    CUSTOMIZATION_DIRECTORY;

    /**
     * Simple Helper method for creating
     * {@link PortalInstallationPathChangedPayload}s
     *
     * @param oldPath may be null
     * @param newPath may be null
     *
     * @return {@link PortalInstallationPathChangedPayload} with the given path
     */
    public PortalInstallationPathChangedPayload ofPath(Path oldPath, Path newPath) {
        return new PortalInstallationPathChangedPayload(this, oldPath, newPath);
    }
}
