package de.cuioss.portal.configuration.impl.schedule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Provides some convenient methods for tracing detecting file-changes.
 *
 * @author Oliver Wolff
 */
@EqualsAndHashCode(callSuper = true)
@ToString
final class FileDescriptor extends AbstractFileDescriptor {

    private static final CuiLogger log = new CuiLogger(FileDescriptor.class);

    private long modificationDate;

    /**
     * @param path must not be null and derive an existing file
     */
    FileDescriptor(final Path path) {
        super(path);
    }

    @Override
    public void update() {
        try {
            modificationDate = retrieveModificationDate();
        } catch (final IOException e) {
            log.warn("Unable to read metadata for file " + getPath().toString(),
                    e);
        }
    }

    @Override
    public boolean isUpdated() {
        var newdate = 0L;
        try {
            newdate = retrieveModificationDate();
        } catch (final IOException e) {
            log.warn("Unable to read metadata for file " + getPath().toString(),
                    e);
        }

        return newdate > modificationDate;
    }

    private long retrieveModificationDate() throws IOException {
        return Files.getLastModifiedTime(getPath()).toMillis();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
