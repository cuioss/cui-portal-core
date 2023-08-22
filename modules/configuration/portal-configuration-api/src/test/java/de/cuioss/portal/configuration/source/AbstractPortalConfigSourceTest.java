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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Map;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.junit.jupiter.api.Test;

import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;

// FIXME rootLevel should not be necessary
@EnableTestLogger(rootLevel = TestLogLevel.TRACE, trace = AbstractPortalConfigSource.class)
class AbstractPortalConfigSourceTest {

    @Test
    void invalidOrdinal() {
        assertEquals(110, createWithOrdinal("b00m").getOrdinal());
        LogAsserts.assertSingleLogMessagePresent(TestLogLevel.TRACE,
                "config_ordinal is not a number. source=AbstractPortalConfigSourceTestSource");
    }

    @Test
    void validOrdinal() {
        assertEquals(42, createWithOrdinal("42").getOrdinal());
        LogAsserts.assertSingleLogMessagePresent(TestLogLevel.TRACE,
                "config_ordinal of AbstractPortalConfigSourceTestSource=42");
    }

    private AbstractPortalConfigSource createWithOrdinal(final String value) {
        return new AbstractPortalConfigSource() {
            @Override
            public Map<String, String> getProperties() {
                return Collections.singletonMap(ConfigSource.CONFIG_ORDINAL, value);
            }

            @Override
            public String getName() {
                return "AbstractPortalConfigSourceTestSource";
            }
        };
    }
}
