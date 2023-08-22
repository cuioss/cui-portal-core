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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Loads all {@value #META_INF_LOCATION} from the provided classloader
 * {@link ConfigSourceProvider#getConfigSources(java.lang.ClassLoader)}.
 *
 * @author Sven Haag
 * @see <a href=
 *      "https://docs.oracle.com/javase/9/docs/api/java/lang/ClassLoader.html#getResources-java.lang.String-">
 *      JAVA 9 - ClassLoader#getResources</a>
 */
public class YamlDefaultConfigSourceProvider implements ConfigSourceProvider {

    private static final CuiLogger log = new CuiLogger(YamlDefaultConfigSourceProvider.class);

    private static final String ERR_MSG = "Portal-539: Could not load YAML default config";

    /** Default location for yaml microprofile-config files. */
    public static final String META_INF_LOCATION = "META-INF/microprofile-config.yaml";

    /**
     * @param classLoader
     *
     * @return a {@link YamlConfigSource} for each {@value #META_INF_LOCATION}
     */
    @Override
    public Iterable<ConfigSource> getConfigSources(final ClassLoader classLoader) {
        final var builder = new CollectionBuilder<ConfigSource>();
        final Enumeration<URL> resources;

        try {
            resources = classLoader.getResources(META_INF_LOCATION);
        } catch (final IOException e) {
            log.error(ERR_MSG, e);
            return builder.toImmutableList();
        }

        while (resources.hasMoreElements()) {
            try {
                builder.add(new YamlDefaultConfigSource(resources.nextElement()));
            } catch (final Exception e) {
                log.error(ERR_MSG, e);
            }
        }

        return builder.toImmutableList();
    }
}
