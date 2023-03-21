package de.cuioss.portal.configuration.schedule;

import java.nio.file.Path;
import java.util.List;

/**
 * @author Oliver Wolff
 */
public interface FileWatcherService {

    /**
     * Registers one or more {@link Path}s to be watched.
     *
     * @param paths to be registered.
     */
    void register(Path... paths);

    /**
     * Unregisters one or more {@link Path}s to be watched. Not existing paths will be silently
     * ignored
     *
     * @param paths to be unregistered.
     */
    void unregister(Path... paths);

    /**
     * @return the paths currently under control of this fileWatcher
     */
    List<Path> getRegisteredPaths();
}
