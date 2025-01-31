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

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class AbstractConfigurationKeyVerifierTestTest extends AbstractConfigurationKeyVerifierTest {

    static final String PATH1 = "mock-configuration";

    @Getter
    private String configSourceName;

    @Getter
    private List<String> keysIgnoreList;

    @Getter
    private List<String> configurationKeysIgnoreList;

    @BeforeEach
    void before() {
        configSourceName = PATH1;
        keysIgnoreList = new ArrayList<>();
        configurationKeysIgnoreList = new ArrayList<>();
    }

    @Override
    public Class<?> getKeyHolder() {
        return ConfigurationKeys.class;
    }

    @Test
    void shouldBlackListKey() {
        assertEquals(4, super.resolveKeyNames().size());
        keysIgnoreList.add(ConfigurationKeys.KEY_1);
        assertEquals(3, super.resolveKeyNames().size());
    }

    @Test
    void shouldFilterConfigurationKey() {
        assertEquals(4, super.resolveConfigurationKeyNames().size());
        configurationKeysIgnoreList.add("de");
        assertEquals(1, super.resolveConfigurationKeyNames().size());
    }
}
