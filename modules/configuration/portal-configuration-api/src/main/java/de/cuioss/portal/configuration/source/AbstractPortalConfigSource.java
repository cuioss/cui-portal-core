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
package de.cuioss.portal.configuration.source;

import java.util.Optional;
import java.util.Set;

import org.eclipse.microprofile.config.spi.ConfigSource;

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.common.PortalPriorities;
import de.cuioss.tools.logging.CuiLogger;
import lombok.ToString;

/**
 * Portal default configuration source for {@code properties} files. Per
 * default, the file must be provided under
 * {@code /META-INF/config/module-default.properties}. This MicroProfile
 * {@link ConfigSource} has an ordinal of {@link ConfigSource#DEFAULT_ORDINAL} +
 * {@link PortalPriorities#PORTAL_CORE_LEVEL} per default.
 *
 * <p>
 * This is a {@link FileConfigurationSource} to allow testing with
 * AbstractConfigurationKeyVerifierTest. This should have no side effects as
 * instances of this class are not known to CDI.
 * </p>
 *
 * @author Sven Haag
 */
@ToString
public abstract class AbstractPortalConfigSource implements ConfigSource {

    private static final CuiLogger LOGGER = new CuiLogger(AbstractPortalConfigSource.class);

    /**
     * @return {@link Class#getSimpleName()}
     */
    @Override
    public String getName() {
        if (this instanceof FileConfigurationSource source) {
            return getClass().getSimpleName() + "[source=" + source.getPath() + "]";
        }
        return getClass().getSimpleName();
    }

    /**
     * Do not overwrite this function. Use {@link #getPortalPriority()} instead.
     * <p>
     * As Portal priorities are within the range 0-150, the final value will fit
     * nicely within the MP config source ordinals 100-250. Hence all portal config
     * sources sit below the "EnvConfigSource".
     *
     * @return {@link ConfigSource#DEFAULT_ORDINAL} + {@link #getPortalPriority()}.
     */
    @Override
    public int getOrdinal() {
        return getConfigOrdinalFromSource().orElse(ConfigSource.DEFAULT_ORDINAL + getPortalPriority());
    }

    @Override
    public String getValue(final String key) {
        return getProperties().get(key);
    }

    @Override
    public Set<String> getPropertyNames() {
        return getProperties().keySet();
    }

    /**
     * Provide a portal priority to reflect the importance of this config source.
     * Higher values have higher importance. Don't exceed a value of
     * <code>199</code> as this would conflict with the default config sources
     * provided by SmallRye.
     *
     * @return {@link PortalPriorities#PORTAL_CORE_LEVEL}
     */
    public int getPortalPriority() {
        return PortalPriorities.PORTAL_CORE_LEVEL;
    }

    protected Optional<Integer> getConfigOrdinalFromSource() {
        final var configOrdinal = this.getValue(ConfigSource.CONFIG_ORDINAL);
        if (null != configOrdinal) {
            try {
                final var ordinal = Integer.parseInt(configOrdinal);
                LOGGER.trace("config_ordinal of {}={}", getName(), ordinal);
                return Optional.of(ordinal);
            } catch (final NumberFormatException nfe) {
                LOGGER.trace(nfe, "config_ordinal is not a number. source={}", getName());
            }
        }
        return Optional.empty();
    }
}
