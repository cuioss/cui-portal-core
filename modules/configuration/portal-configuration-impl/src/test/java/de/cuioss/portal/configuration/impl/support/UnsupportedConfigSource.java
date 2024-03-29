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
package de.cuioss.portal.configuration.impl.support;

import java.util.Map;

import de.cuioss.portal.common.priority.PortalPriorities;
import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.source.PropertiesConfigSource;

/**
 * Representing a {@linkplain FileConfigurationSource} to an existing file that
 * is not supported.
 *
 * @author Sven Haag
 */
final public class UnsupportedConfigSource extends PropertiesConfigSource {

    public UnsupportedConfigSource() {
        super("file:target/test-classes/config/supported.not");
    }

    @Override
    public Map<String, String> getProperties() {
        throw new IllegalStateException("this should not be called as the file cannot be loaded in the first place");
    }

    @Override
    public int getPortalPriority() {
        return PortalPriorities.PORTAL_MODULE_LEVEL;
    }
}
