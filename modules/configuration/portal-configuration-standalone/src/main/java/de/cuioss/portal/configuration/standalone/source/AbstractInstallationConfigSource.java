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
package de.cuioss.portal.configuration.standalone.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_DIR;
import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_DIR_DEFAULT;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigPropertyFromSysOrEnv;

import java.nio.file.Paths;
import java.util.Map;

import de.cuioss.portal.common.priority.PortalPriorities;
import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.impl.source.LoaderUtils;
import de.cuioss.portal.configuration.source.AbstractPortalConfigSource;
import de.cuioss.tools.collect.CollectionLiterals;
import de.cuioss.tools.io.FileSystemLoader;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Abstract class for loading installation configuration such as
 * application.yml. A non existing file is nicely handled and re-loaded if it is
 * added in a later point in time.
 *
 * @author Sven Haag
 */
public abstract class AbstractInstallationConfigSource extends AbstractPortalConfigSource
        implements FileConfigurationSource {

    private static final CuiLogger LOGGER = new CuiLogger(AbstractInstallationConfigSource.class);

    @SuppressWarnings("java:S3077") // owolff: Not a problem because all implementations set are
                                    // immutable
    protected volatile Map<String, String> properties;

    protected String path;

    protected boolean isRequired() {
        return true;
    }

    private boolean present;

    /**
     * @return file name in config dir. Must not be {@code null} nor empty!
     */
    abstract String getFileName();

    @Override
    public int getPortalPriority() {
        return PortalPriorities.PORTAL_INSTALLATION_LEVEL;
    }

    @Override
    public String getPath() {
        if (null == path) {
            initPath();
            reload();
        }
        return path;
    }

    @Override
    public boolean isReadable() {
        if (null == path) {
            initPath();
        }
        return present;
    }

    /**
     * A path is always initialized, despite the file might not exist yet! if
     * {@link #reload()} is triggered, the file might actually exist and we can load
     * properties from it.
     */
    public void initPath() {
        LOGGER.debug(() -> "init path for: " + getName());
        properties = null; // a changed path dictates reloading of our properties

        final var filePath = Paths.get(getPortalConfigDir(), getFileName()).toAbsolutePath();
        path = filePath.toString();

        if (filePath.toFile().isFile()) {
            present = true;
            LOGGER.info("Portal-016: Reading installation configuration from: {}", filePath);
        } else {
            present = false;
            if (isRequired()) {
                LOGGER.warn("Portal-121: Given configuration file '{}' does not represent an existing/readable file.",
                        filePath);
            } else {
                LOGGER.info("Portal-121: Given configuration file '{}' does not represent an existing/readable file.",
                        filePath);
            }
        }
    }

    private String getPortalConfigDir() {
        final var configDirProvider = resolveConfigPropertyFromSysOrEnv(PORTAL_CONFIG_DIR);
        if (configDirProvider.isPresent()) {
            final var newPath = configDirProvider.get().trim();
            if (!"".equals(newPath)) {
                LOGGER.debug("Resolved portal.configuration.dir: {}", configDirProvider.get());
                return configDirProvider.get();
            }
            LOGGER.debug("portal config dir environment variable is an empty string, using default");
        }

        LOGGER.warn("Portal-158: Config property '{}' not set as environment or system variable. Using default: {}",
                PORTAL_CONFIG_DIR, PORTAL_CONFIG_DIR_DEFAULT);
        return PORTAL_CONFIG_DIR_DEFAULT;
    }

    @Override
    public Map<String, String> getProperties() {
        if (null == properties) {
            reload();
            if (null == properties) {
                // seems the file does not exist (yet).
                // we keep polling each time this config source is queried.
                return CollectionLiterals.immutableMap();
            }
        }

        return properties;
    }

    @Override
    public void reload() {
        if (isReadable()) {
            LOGGER.trace("Reloading: {}", path);
            properties = LoaderUtils.loadConfigurationFromSource(new FileSystemLoader(path));
            LOGGER.trace("Reloaded content from {}, keys are {}", path, properties.keySet());
        }
    }
}
