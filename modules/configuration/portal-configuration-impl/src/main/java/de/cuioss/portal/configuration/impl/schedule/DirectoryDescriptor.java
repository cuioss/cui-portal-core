package de.cuioss.portal.configuration.impl.schedule;

import static de.cuioss.tools.collect.CollectionLiterals.mutableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Provides some convenient methods for tracing detecting directory-changes.
 *
 * @author Matthias Walliczek
 *
 */
@EqualsAndHashCode(callSuper = true)
@ToString
final class DirectoryDescriptor extends AbstractFileDescriptor {

    private static final CuiLogger log = new CuiLogger(DirectoryDescriptor.class);

    private List<String> content;

    private long lastModifiedHash;

    /**
     * @param path must not be null and derive an existing directory
     */
    DirectoryDescriptor(final Path path) {
        super(path);
    }

    private List<String> fileList(final Path directory) {
        List<String> fileNames = mutableList();
        try (var directoryStream = Files.newDirectoryStream(directory)) {
            for (Path currentPath : directoryStream) {
                lastModifiedHash += currentPath.toFile().lastModified();
                fileNames.add(currentPath.toString());
                if (currentPath.toFile().isDirectory()) {
                    fileNames.addAll(fileList(currentPath));
                }
            }
        } catch (IOException ex) {
            log.warn("Directory " + directory.toString() + " could not be read", ex);
        }
        return fileNames;
    }

    @Override
    public void update() {
        lastModifiedHash = 0L;
        content = fileList(getPath());
    }

    @Override
    public boolean isUpdated() {
        var oldLastModifiedHash = lastModifiedHash;
        lastModifiedHash = 0L;
        var newContent = fileList(getPath());

        return !newContent.equals(content) || oldLastModifiedHash != lastModifiedHash;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }
}
