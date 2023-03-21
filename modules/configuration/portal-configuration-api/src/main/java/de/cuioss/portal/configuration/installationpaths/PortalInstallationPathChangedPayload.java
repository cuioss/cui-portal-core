package de.cuioss.portal.configuration.installationpaths;

import java.nio.file.Path;
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Payload of a {@link PortalInstallationPathChangedEvent}. Contains an enum describing which path
 * was changed and the new path.
 *
 * @author Matthias Walliczek
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class PortalInstallationPathChangedPayload {

    @Getter
    private final PortalInstallationPathType type;
    private final Path oldPath;
    private final Path newPath;

    /**
     * @return the old path
     */
    public Optional<Path> getOldPath() {
        return Optional.ofNullable(oldPath);
    }

    /**
     * @return the new path
     */
    public Optional<Path> getNewPath() {
        return Optional.ofNullable(newPath);
    }
}
