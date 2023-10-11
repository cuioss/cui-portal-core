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
package de.cuioss.portal.core.test.tests.configuration;

import org.eclipse.microprofile.config.spi.ConfigSource;

import de.cuioss.portal.common.priority.PortalPriorities;
import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.source.PropertiesConfigSource;
import de.cuioss.tools.io.FileTypePrefix;

/**
 * Portal default configuration source for {@code properties} files. Per
 * default, the file must be provided under
 * {@value PropertiesDefaultConfigSource#META_INF_LOCATION}. This MicroProfile
 * {@link ConfigSource} has an ordinal of {@link ConfigSource#DEFAULT_ORDINAL} +
 * {@link PortalPriorities#PORTAL_CORE_LEVEL} per default.
 *
 * <p>
 * This is a {@link FileConfigurationSource} too, to allow testing with
 * AbstractConfigurationKeyVerifierTest.
 * </p>
 *
 * <p>
 * <em>This class is not registered via SPI</em>, and doesn't need to. The unit
 * test will only load the file to compare it against the given class containing
 * the possible config keys for that module.
 * </p>
 *
 * @author Sven Haag
 */
public final class PropertiesDefaultConfigSource extends PropertiesConfigSource {

    public static final String META_INF_LOCATION = "META-INF/microprofile-config.properties";
    @SuppressWarnings("java:S1075") // owolff: the delimiter is correct for classpath-resources
    public static final String CLASSPATH_LOCATION = FileTypePrefix.CLASSPATH.getPrefix() + "/" + META_INF_LOCATION;

    /**
     * Loading properties from {@link #CLASSPATH_LOCATION}. This basically is a
     * convenience constructor for unit testing a modules default config.
     */
    public PropertiesDefaultConfigSource() {
        super(CLASSPATH_LOCATION);
    }
}
