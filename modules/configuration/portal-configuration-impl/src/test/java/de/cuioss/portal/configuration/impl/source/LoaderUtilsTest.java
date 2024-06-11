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

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.io.FileLoader;
import org.junit.jupiter.api.Test;

import static de.cuioss.portal.configuration.impl.source.LoaderUtils.loadConfigurationFromSource;
import static de.cuioss.portal.configuration.impl.support.TestResourcesRepository.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableTestLogger(debug = LoaderUtils.class)
class LoaderUtilsTest {

    @Test
    void shouldCreateFromFileResources() {
        assertTrue(loadConfigurationFromSource((FileLoader) null).isEmpty());
        assertTrue(loadConfigurationFromSource((FileConfigurationSource) null).isEmpty());
        assertTrue(loadConfigurationFromSource(EMPTY_PATH).isEmpty());
    }
}
