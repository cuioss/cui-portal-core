/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.configuration.impl.schedule;

import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigProperty;
import static de.cuioss.tools.collect.MoreCollections.difference;
import static de.cuioss.tools.string.MoreStrings.isEmpty;
import static java.nio.file.Files.isSameFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;

import de.cuioss.portal.configuration.ConfigurationSourceChangeEvent;
import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.Reloadable;
import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.configuration.schedule.FileChangedEvent;
import de.cuioss.portal.configuration.schedule.FileWatcherService;
import de.cuioss.portal.configuration.schedule.PortalFileWatcherService;
import de.cuioss.tools.collect.MapBuilder;
import de.cuioss.tools.collect.MapDifference;
import de.cuioss.tools.io.FileTypePrefix;
import de.cuioss.tools.logging.CuiLogger;

/**
 * <p>
 * Watch all actual file paths of {@link FileConfigurationSource}
 * implementations. This cannot be done easily in the config sources themselves,
 * because they are not CDI based. Therefore we are utilizing
 * {@link FileConfigurationSource#reload()}.
 * </p>
 *
 * <p>
 * If, after reload, a config change was detected, All other {@link Reloadable}
 * config sources are checked whether they contain one of the changed keys as a
 * placeholder. If so, the corresponding key is added to the delta/diff map of
 * the {@link PortalConfigurationChangeEvent}, which gets fired at the end of
 * this process.
 * </p>
 *
 * <h3>Caveat!</h3> Placeholder indirections are not tracked! For instance given
 * this config:
 *
 * <pre>
 * parent.key=parent.value
 * child.key=${parent.key}
 * child.child.key=${child.key}
 * </pre>
 *
 * The key <code>child.child.key</code> would not be part of the delta map,
 * despite its value might change!
 *
 * @author Sven Haag
 */
@ApplicationScoped
@PortalInitializer
public class ConfigChangeObserver implements ApplicationInitializer {

    private static final CuiLogger LOGGER = new CuiLogger(ConfigChangeObserver.class);

    private static final Predicate<String> FILE_PATHS_TO_SKIP = (var path) -> isEmpty(path)
            || FileTypePrefix.CLASSPATH.is(path) || FileTypePrefix.URL.is(path);

    @Inject
    @PortalFileWatcherService
    Instance<FileWatcherService> fileWatcherServices;

    @Inject
    Instance<FileConfigurationSource> fileConfigurationSources;

    /** Fired, if the configuration of the changed file has changed */
    @Inject
    @ConfigurationSourceChangeEvent
    Event<Map<String, String>> sourceChangeEvent;

    /** To be fired if any of the registered files has changed */
    @Inject
    @PortalConfigurationChangeEvent
    Event<Map<String, String>> configChangeEvent;

    @Override
    public void initialize() {
        // register all FileConfigurationSource paths
        fileWatcherServices.forEach(fileWatcher -> fileConfigurationSources.stream()
                .filter(FileConfigurationSource::isReadable).forEach(configSource -> {
                    final var path = configSource.getPath();

                    if (FILE_PATHS_TO_SKIP.test(path)) {
                        LOGGER.debug("Skipping path '{}' from source: {}", path, configSource);
                        return;
                    }

                    fileWatcher.register(Paths.get(path));
                }));
    }

    @Override
    public Integer getOrder() {
        // late, because the configuration system has to initialize first
        return ORDER_LATE;
    }

    /**
     * Listen for file changes. Fire source change event.
     *
     * @param changedFilePath file path of the changed file
     */
    void fileChangeListener(@Observes @FileChangedEvent final Path changedFilePath) {
        LOGGER.debug("File change detected for: {}", changedFilePath);

        final var fileChangeDeltaMap = getFileChangeDeltaMap(changedFilePath);
        final var deltaMap = getAffectedPropertiesAndIndirections(fileChangeDeltaMap);

        if (deltaMap.isEmpty()) {
            LOGGER.info("Portal-013: No changes found, no reloading will happen");
        } else {
            LOGGER.info("Portal-014: New configuration loaded, affected keys: {}", deltaMap.keySet());
            sourceChangeEvent.fire(deltaMap);
        }
    }

    private Map<String, String> getAffectedPropertiesAndIndirections(Map<String, String> changedProperties) {
        final Map<String, String> deltaMap = new HashMap<>(changedProperties);
        final Map<String, String> affectedProperties = new HashMap<>(changedProperties);

        // limit check of affected config key indirections to 10 iterations
        final var LOOP_LIMIT = 10;
        for (var i = 0; i < LOOP_LIMIT; i++) {
            LOGGER.debug("Processing affected properties: {}", affectedProperties);
            affectedProperties.putAll(getAffectedProperties(affectedProperties));

            // remove already found keys to prevent loop
            deltaMap.keySet().forEach(affectedProperties::remove);

            if (affectedProperties.isEmpty()) {
                LOGGER.debug("Leaving loop after {} iterations", i);
                // no more keys found -> exit the loop
                break;
            }

            LOGGER.debug("Adding affected properties to delta map: {}", affectedProperties);
            deltaMap.putAll(affectedProperties);

            if (LOOP_LIMIT == (i + 1)) {
                LOGGER.debug("Reached loop limit of {} iterations for finding config placeholder indirections",
                        LOOP_LIMIT);
            }
        }

        return deltaMap;
    }

    private Map<String, String> getAffectedProperties(Map<String, String> fileChangeDeltaMap) {
        final Map<String, String> affectedProperties = new HashMap<>();

        for (final ConfigSource configSource : ConfigProvider.getConfig().getConfigSources()) {
            if (configSource instanceof Reloadable) {
                checkForChangedKeys(fileChangeDeltaMap, affectedProperties, configSource);
            }
        }

        return affectedProperties;
    }

    private void checkForChangedKeys(Map<String, String> fileChangeDeltaMap,
            final Map<String, String> affectedProperties, final ConfigSource configSource) {
        LOGGER.debug("Checking for changed keys, config source: {}", configSource.getName());
        for (final String changedKey : fileChangeDeltaMap.keySet()) {
            for (Map.Entry<String, String> entry : configSource.getProperties().entrySet()) {
                if (containsPlaceholder(entry.getValue(), changedKey)) {
                    LOGGER.debug("Found changed key={} in config source={}", entry.getKey(), configSource.getName());
                    // The actual value for this key probably only changes if it gets resolved.
                    // Say, the value of this very config source has not changed,
                    // but we need to fire a delta for this key as it might have changed after
                    // resolving.
                    if (!fileChangeDeltaMap.containsKey(entry.getKey())) {
                        affectedProperties.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    private Map<String, String> getFileChangeDeltaMap(Path changedFilePath) {
        for (final ConfigSource configSource : ConfigProvider.getConfig().getConfigSources()) {
            if (configSource instanceof FileConfigurationSource) {
                var deltaMap = handleFileConfigSourceChange(configSource, changedFilePath);

                if (deltaMap.isPresent()) {
                    LOGGER.debug("Config source {} reloaded, affected keys: {}", configSource.getName(),
                            deltaMap.get().keySet());
                    // we don't expect 2 config sources with the same path
                    return deltaMap.get();
                }

            }
        }
        return Collections.emptyMap();
    }
    
    @SuppressWarnings("el-syntax")
    private boolean containsPlaceholder(final String haystack, final String needle) {
        return (null != haystack) && (null != needle)
                && (haystack.contains("${" + needle + "}") || haystack.contains("${" + needle + ":"));
    }

    /**
     * @param configSource
     * @param changedFilePath
     *
     * @return true, if config source change was processed
     */
    private Optional<Map<String, String>> handleFileConfigSourceChange(final ConfigSource configSource,
            final Path changedFilePath) {

        final var fileConfigSource = (FileConfigurationSource) configSource;

        if (FILE_PATHS_TO_SKIP.test(fileConfigSource.getPath())) {
            return Optional.empty();
        }

        try {
            if (!changedFilePath.toFile().isFile()) {
                LOGGER.debug("Not a file: {}", changedFilePath.toAbsolutePath());
                return Optional.empty();
            }

            final var configSourcePath = Paths.get(fileConfigSource.getPath()).toAbsolutePath();
            if (!configSourcePath.toFile().isFile()) {
                LOGGER.debug("Not a file: {}", configSourcePath);
                return Optional.empty();
            }

            if (isSameFile(configSourcePath, changedFilePath)) {
                LOGGER.info("Portal-003: Reloading installation specific configuration, found at: {}",
                        changedFilePath.toAbsolutePath().toString());

                return Optional.of(getDiffAfterReload(configSource, fileConfigSource));
            }
        } catch (final IOException e) {
            LOGGER.error(e, "Portal-503: Unable to reload FileConfigurationSource: {}", fileConfigSource);
        }

        return Optional.empty();
    }

    private Map<String, String> getDiffAfterReload(ConfigSource configSource,
            FileConfigurationSource fileConfigSource) {
        final var before = MapBuilder.copyFrom(configSource.getProperties()).toImmutableMap();
        fileConfigSource.reload();
        final var after = MapBuilder.copyFrom(configSource.getProperties()).toImmutableMap();

        return getDeltaMap(before, after);
    }

    private Map<String, String> getDeltaMap(final Map<String, String> before, final Map<String, String> after) {
        final var builder = new MapBuilder<String, String>();
        final MapDifference<String, String> difference = difference(before, after);

        if (!difference.areEqual()) {
            final var changedKeys = difference.entriesDiffering().keySet();
            final var addedKeys = difference.entriesOnlyOnRight().keySet();
            final var removedKeys = difference.entriesOnlyOnLeft().keySet();

            // Add ChangedKeys
            for (final String key : changedKeys) {
                builder.put(key, resolveConfigProperty(key).orElse(""));
            }
            // Add Additional Keys
            for (final String key : addedKeys) {
                builder.put(key, resolveConfigProperty(key).orElse(""));
            }
            // Add removed keys as empty String in order to signal the removal of a key
            for (final String key : removedKeys) {
                builder.put(key, "");
            }
        }

        return builder.toImmutableMap();
    }

    /**
     * Listens to events of type {@link ConfigurationSourceChangeEvent} and fires
     * {@link PortalConfigurationChangeEvent} with same map.
     *
     * @param eventMap changed data
     */
    void configSourceChangeEventListener(@Observes @ConfigurationSourceChangeEvent final Map<String, String> eventMap) {
        configChangeEvent.fire(eventMap);
    }
}
