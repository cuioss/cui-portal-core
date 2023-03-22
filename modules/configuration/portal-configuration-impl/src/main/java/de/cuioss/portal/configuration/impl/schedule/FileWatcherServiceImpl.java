package de.cuioss.portal.configuration.impl.schedule;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.SCHEDULER_FILE_SCAN_ENABLED;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.configuration.schedule.FileChangedEvent;
import de.cuioss.portal.configuration.schedule.FileWatcherService;
import de.cuioss.portal.configuration.schedule.PortalFileWatcherService;
import de.cuioss.tools.io.MorePaths;
import de.cuioss.tools.logging.CuiLogger;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Replacement for initial quartz based module (cdi-portal-core-scheduler). It uses
 * {@link WatchService} and {@link Executors} in order to work. It can be dynamically switched on /
 * without losing the registered {@link Path} elements.
 * <p>
 * TODO owolff: Reconsider thread handling
 * TODO owolff: Consider changing the event / api contract in order to transport more precise
 * information, like "I'm interested in changed / added / removed files".
 * TODO owolff consider api changes in order to align with {@link WatchService} design, especially
 * regarding directory instead of file-based-handling
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@PortalFileWatcherService
@PortalInitializer
public class FileWatcherServiceImpl implements FileWatcherService, ApplicationInitializer {

    private static final CuiLogger log = new CuiLogger(FileWatcherServiceImpl.class);

    private static final int EVENT_TIMEOUT = 500;

    @Inject
    @ConfigProperty(name = SCHEDULER_FILE_SCAN_ENABLED)
    private Provider<Boolean> enabledProvider;

    private WatchService watcherService;

    // Tracks actually watched paths, combined with the path representations
    private final Map<WatchKey, Path> watchedPaths = new ConcurrentHashMap<>();
    private final Map<Path, AbstractFileDescriptor> registeredPaths = new ConcurrentHashMap<>();

    private ExecutorService executor;

    /**
     * {@code true} if the service is configured to run
     * {@link PortalConfigurationKeys#SCHEDULER_FILE_SCAN_ENABLED} at all and initialized
     * properly.
     */
    @Getter(AccessLevel.MODULE)
    private boolean upAndRunning = false;

    @Inject
    @FileChangedEvent
    private Event<Path> fileChangeEvent;

    /**
     * Initializes the watcher depending on the configuration of
     * {@link PortalConfigurationKeys#SCHEDULER_FILE_SCAN_ENABLED}
     */
    @Override
    public void initialize() {
        if (enabledProvider.get().booleanValue()) {
            log.debug("Initializing FileWatcherService");
            if (null == watcherService) {
                try {
                    watcherService = FileSystems.getDefault().newWatchService();
                } catch (IOException | UnsupportedOperationException e) {
                    log.error(
                            "Portal-515: Unable to access File-system for detecting changes, due to '{}', use the " +
                                    "configuration property '{}' to disable this feature",
                            e.getMessage(), SCHEDULER_FILE_SCAN_ENABLED, e);
                    upAndRunning = false;
                    return;
                }
            }
            if (null == executor) {
                executor = Executors.newSingleThreadExecutor();
                executor.execute(fileWatchExecutor());
            }
            upAndRunning = true;
            // Using the scheduling like this we won't miss elements that have already been
            // scheduled
            handleScheduling();
            log.debug("FileWatcherService initialized, files are scheduled");
        } else {
            log.debug("Ignoring initialization call, due to configuration of '{}'", SCHEDULER_FILE_SCAN_ENABLED);
            upAndRunning = false;
            if (null != executor) {
                executor.shutdown();
                executor = null;
            }
        }
    }

    /**
     * Handle the shutdown / clean up of the {@link FileWatcherService}
     */
    @Override
    public void destroy() {
        log.debug("Shutting down FileWatcherService");
        upAndRunning = false;
        var paths = getRegisteredPaths();
        log.debug("Unregistering paths");
        watchedPaths.keySet().forEach(WatchKey::cancel);
        unregister(paths.toArray(new Path[0]));
        closeWatcher();
        log.debug("Shutting down executor");
        if (null != executor) {
            executor.shutdown();
        }
        log.debug("Shutting down FileWatcherService was successfull");
    }

    @Override
    public Integer getOrder() {
        return ApplicationInitializer.ORDER_LATE;
    }

    @Override
    public void register(Path... paths) {

        for (Path path : paths) {
            log.trace("Register called for {}", path);
            var created = AbstractFileDescriptor.create(path);
            if (created.isPresent()) {
                var absolute = created.get().getPath();
                if (!registeredPaths.containsKey(absolute)) {
                    log.debug("Adding Path '{}' to registeredPaths", absolute);
                    registeredPaths.put(absolute, created.get());
                } else {
                    log.debug("Path '{}' already registered, ignoring", absolute);
                }
            }
        }
        handleScheduling();
    }

    private void handleScheduling() {
        if (isUpAndRunning()) {
            registeredPaths.values().forEach(a -> a.addWatchKey(watcherService, watchedPaths));
        }
    }

    @Override
    public void unregister(Path... paths) {
        for (Path path : paths) {
            log.trace("Unregister called for {}", path);
            var absolute = MorePaths.getRealPathSafely(path);
            if (null != registeredPaths.remove(absolute)) {
                log.debug("Unregistered path '{}' from fileWatch", absolute);
            } else if (isUpAndRunning()) {
                log.debug("Path '{}' not found within watchedPaths, can therefore not be removed.", absolute);
            }
        }
    }

    @Override
    public List<Path> getRegisteredPaths() {
        List<Path> paths =
            registeredPaths.values().stream().map(AbstractFileDescriptor::getPath).collect(Collectors.toList());
        log.trace("getRegisteredPaths callled, returning {}", paths);
        return paths;
    }

    void configurationChangeEventListener(
            @Observes @PortalConfigurationChangeEvent Map<String, String> deltaMap) {
        if (deltaMap.containsKey(SCHEDULER_FILE_SCAN_ENABLED)) {
            final var enabled = Boolean.parseBoolean(deltaMap.get(SCHEDULER_FILE_SCAN_ENABLED));
            log.debug("Portal-011: Changed configuration of '{}' found, new value is '{}', reconfigure",
                    SCHEDULER_FILE_SCAN_ENABLED,
                    enabled);
            if (enabled) {
                initialize();
            } else {
                destroy();
            }
        }
    }

    @SuppressWarnings("squid:S1188") // Not to much logic, so the number of lines are ok
    private Runnable fileWatchExecutor() {
        return () -> {
            while (upAndRunning) {
                WatchKey watchKey = null;
                try {
                    watchKey = watcherService.take();
                    Thread.sleep(EVENT_TIMEOUT);
                    handleChangedWatchKey(watchKey);
                } catch (InterruptedException ie) {
                    log.debug("Interrupted, exiting loop", ie);
                    Thread.currentThread().interrupt();
                    break;
                } catch (ClosedWatchServiceException e) {
                    // Hm feels a little clumsy. Should consider correct Interruption handling
                    log.debug("Shutdown while waiting");
                } catch (Exception e) {
                    log.error("Portal-515: Error while polling / accessing the file-system", e);
                } finally {
                    if (null != watchKey) {
                        watchKey.reset();
                    }
                }
            }
        };
    }

    private void handleChangedWatchKey(WatchKey watchKey) {
        var events = watchKey.pollEvents();
        if (events.isEmpty()) {
            log.warn("Portal-515: Invalid element found, watchKey='{}', ignoring", watchKey);
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace("Handling WatchKey-Events {}",
                    events.stream().map(w -> w.context() + "-" + w.kind()).collect(Collectors.toList()));
        }
        List<AbstractFileDescriptor> changed =
            registeredPaths.values().stream().filter(AbstractFileDescriptor::isUpdated).collect(Collectors.toList());

        if (changed.isEmpty()) {
            log.debug("No actual changes found for path-change WatchKey-Events {}", events);
        }
        for (AbstractFileDescriptor element : changed) {
            log.debug("Delivering notification for path changes of: '{}'", element.getPath());
            try {
                // Handling the event should not throw an exception, because this will break the
                // iteration over all
                // event listener - however, if it happens (it is a file system operation that may
                // fail due to IO
                // errors) it should at least not crash the service at all.
                fileChangeEvent.fire(element.getPath());
            } catch (Exception e) {
                log.error("Portal-533: Handling fileChangedEvent failed for file {}: ", e,
                        element.getPath().toString());
            }
            element.update();
        }
    }

    /**
     * Cancels all registered watches and unregisters all paths.
     */
    void clear() {
        unregister(getRegisteredPaths().toArray(new Path[0]));
        watchedPaths.keySet().forEach(WatchKey::cancel);
        this.watchedPaths.clear();
    }

    private void closeWatcher() {
        log.debug("Closing WatchService");
        if (null != watcherService) {
            try {
                watcherService.close();
            } catch (IOException e) {
                log.debug("Unable to close Watch-Service, due to '{}'", e.getMessage(), e);
            }
        }
    }
}
