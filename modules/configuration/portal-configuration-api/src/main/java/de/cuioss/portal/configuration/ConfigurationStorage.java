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
package de.cuioss.portal.configuration;

import java.io.IOException;
import java.util.Map;

/**
 * Configuration property persistence enabler. Extends
 * {@linkplain ConfigurationSource} in order to be able to decide what should be
 * saved and what not. Each {@linkplain ConfigurationStorage} should only store
 * things they provide.
 *
 * @author Sven Haag
 */
public interface ConfigurationStorage extends ConfigurationSource {

    /**
     * Save configuration while overwriting existing properties.
     *
     * @param map
     * @throws IOException
     */
    void updateConfigurationMap(Map<String, String> map) throws IOException;

    /**
     * Save configuration property. Overwrites an existing property or creates a new
     * one.
     *
     * @param key
     * @param value
     * @throws IOException
     */
    void updateConfigurationProperty(String key, String value) throws IOException;
}
