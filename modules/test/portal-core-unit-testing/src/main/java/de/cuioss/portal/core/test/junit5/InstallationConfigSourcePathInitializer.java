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
package de.cuioss.portal.core.test.junit5;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_DIR;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;

import de.cuioss.portal.configuration.ConfigurationSourceChangeEvent;
import de.cuioss.portal.configuration.impl.source.AbstractInstallationConfigSource;
import de.cuioss.tools.logging.CuiLogger;

/**
 * This is a simple observer observing changes of the portal config dir
 * configuration. If there is a change it re-initialized all
 * AbstractInstallationConfigSources via
 * {@link AbstractInstallationConfigSource#initPath()}.
 * <p>
 * This cannot be done in the config source itself as they cannot be CDI enabled
 * due to the nature of an actual MP config source (init via SPI).
 * <p>
 * This must only apply to junit tests, because per default surefire creates 1
 * JVM per module, not per test class!
 *
 * @author Sven Haag
 */
@ApplicationScoped
public class InstallationConfigSourcePathInitializer {

    private static final CuiLogger LOGGER = new CuiLogger(InstallationConfigSourcePathInitializer.class);

    void configSourceChangeEventListener(@Observes @ConfigurationSourceChangeEvent final Map<String, String> eventMap) {
        if (eventMap.containsKey(PORTAL_CONFIG_DIR)) {
            LOGGER.debug("portal config dir changed");
            for (final ConfigSource configSource : ConfigProvider.getConfig().getConfigSources()) {
                if (configSource instanceof AbstractInstallationConfigSource source) {
                    LOGGER.debug("re-init: {}", configSource.getName());
                    source.initPath();
                }
            }
        }
    }
}
