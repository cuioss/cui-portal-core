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

import static de.cuioss.tools.string.MoreStrings.isEmpty;

import java.util.Locale;
import java.util.Map;

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.impl.PropertiesConfigurationProvider;
import de.cuioss.tools.collect.CollectionLiterals;
import de.cuioss.tools.io.FileLoaderUtility;
import de.cuioss.tools.logging.CuiLogger;
import lombok.Getter;

/**
 * @author Sven Haag
 */
public class PropertiesConfigSource extends AbstractPortalConfigSource implements FileConfigurationSource {

    private static final CuiLogger log = new CuiLogger(PropertiesConfigSource.class);

    @Getter
    protected String path;

    @Getter
    private final Map<String, String> properties;

    @Getter
    private final boolean readable;

    /**
     * Instantly loads the given file. The path might be prefixed with one of the
     * {@link de.cuioss.tools.io.FileTypePrefix}s.
     *
     * @param path to the properties file
     *
     * @throws IllegalArgumentException if {@code path} is empty or {@code null} or
     *                                  does not have a {@code properties} file
     *                                  extension.
     */
    public PropertiesConfigSource(final String path) {
        this(path, false);
    }

    /**
     * Instantly loads the given file. The path might be prefixed with one of the
     * {@link de.cuioss.tools.io.FileTypePrefix}s.
     *
     * @param path     to the properties file
     * @param optional if true, doesn't throw an exception if the file isn't
     *                 available.
     *
     * @throws IllegalArgumentException if {@code path} is empty or {@code null} or
     *                                  does not have a {@code properties} file
     *                                  extension.
     */
    public PropertiesConfigSource(final String path, final boolean optional) {
        this.path = path;
        validateFileType();
        final var fileLoader = FileLoaderUtility.getLoaderForPath(getPath());
        if (!optional || fileLoader.isReadable()) {
            this.properties = new PropertiesConfigurationProvider(fileLoader).getConfigurationMap();
            this.readable = true;
        } else {
            this.properties = CollectionLiterals.immutableMap();
            this.readable = false;
        }
        log.trace("Loaded data for {}: {}", getPath(), getProperties());
    }

    private void validateFileType() {
        if (isEmpty(getPath())) {
            throw new IllegalArgumentException("Path must not be null nor empty");
        }
        final var lowerPath = getPath().toLowerCase(Locale.ROOT);
        if (!lowerPath.endsWith(".properties")) {
            throw new IllegalArgumentException("Only properties files are permitted. Given: " + lowerPath);
        }
    }
}
