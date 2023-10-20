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
package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;
import static de.cuioss.tools.string.MoreStrings.isEmpty;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.source.ConfigurationSourceResolver;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.FileLoaderUtility;
import de.cuioss.tools.logging.CuiLogger;
import lombok.val;
import lombok.experimental.UtilityClass;

/**
 * Provides utilities for loading / managing Resources that are used for
 * obtaining actual configurations
 *
 * @author Oliver Wolff
 */
@UtilityClass
public class LoaderUtils {

    private static final CuiLogger LOG = new CuiLogger(LoaderUtils.class);

    private static final String FILETYPE_NOT_PROVIDED_MSG = "Portal-151: Unsupported configuration file type: {}. Supported is: {}. If you want to support yaml files do not forget to add 'portal-configuration-yaml' to the deployment";

    /**
     * Loads the the content of a given {@link FileConfigurationSource}
     *
     * @param source to be loaded from, supported is: yml, yaml, properties
     *
     * @return the {@link Map} of the contained configuration
     */
    public static Map<String, String> loadConfigurationFromSource(final FileConfigurationSource source) {
        if (null == source || isEmpty(source.getPath())) {
            LOG.debug("Nothing to load found");
            return Collections.emptyMap();
        }
        for (var resolver : loadResolver()) {
            var provider = resolver.resolve(FileLoaderUtility.getLoaderForPath(source.getPath()));
            if (provider.isPresent()) {
                return provider.get().getConfigurationMap();
            }
        }

        LOG.warn(FILETYPE_NOT_PROVIDED_MSG, source, retrieveSupportedSuffixes());
        return Collections.emptyMap();
    }

    /**
     * Loads the the content of a given {@link FileLoader}
     *
     * @param source to be loaded from, supported is: yml, yaml, properties
     *
     * @return the {@link Map} of the contained configuration
     */
    public static Map<String, String> loadConfigurationFromSource(final FileLoader source) {
        if (null == source || isEmpty(source.getFileName().getOriginalName())) {
            LOG.debug("Nothing to load found");
            return Collections.emptyMap();
        }
        for (var resolver : loadResolver()) {
            var provider = resolver.resolve(source);
            if (provider.isPresent()) {
                return provider.get().getConfigurationMap();
            }
        }

        LOG.warn(FILETYPE_NOT_PROVIDED_MSG, source, retrieveSupportedSuffixes());
        return Collections.emptyMap();
    }

    static List<ConfigurationSourceResolver> loadResolver() {
        return immutableList(ServiceLoader.load(ConfigurationSourceResolver.class).iterator());
    }

    static Set<String> retrieveSupportedSuffixes() {
        val result = new TreeSet<String>();
        for (var resolver : loadResolver()) {
            result.addAll(resolver.supportedSuffixes());
        }
        return result;
    }

}
