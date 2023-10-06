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
package de.cuioss.portal.configuration.yaml;

import java.net.URL;

import de.cuioss.portal.configuration.common.PortalPriorities;
import de.cuioss.tools.io.FileTypePrefix;

/**
 * A modules default YAML
 * {@link org.eclipse.microprofile.config.spi.ConfigSource} and
 * {@link de.cuioss.portal.configuration.FileConfigurationSource} pointing to
 * <code>classpath:META-INF/microprofile-config.yaml</code>.
 *
 * <p>
 * <em>Note: You need to register your config source via SPI
 * {@link org.eclipse.microprofile.config.spi.ConfigSource}.</em>
 * </p>
 *
 * @author Sven Haag
 */
public final class YamlDefaultConfigSource extends YamlConfigSource {

    public YamlDefaultConfigSource() {
        super(FileTypePrefix.CLASSPATH.getPrefix() + "/" + YamlDefaultConfigSourceProvider.META_INF_LOCATION);
    }

    /**
     * @param url must not be null
     */
    public YamlDefaultConfigSource(final URL url) {
        super(FileTypePrefix.URL.getPrefix() + url.toString());
    }

    /**
     * @return 110 and therefore a higher ordinal than
     *         {@code microprofile-config.properties} (100).
     */
    @Override
    public int getPortalPriority() {
        return PortalPriorities.PORTAL_CORE_LEVEL;
    }
}
