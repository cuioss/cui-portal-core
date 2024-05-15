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

import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.PortalConfigurationChangeInterceptor;
import lombok.Getter;
import lombok.Setter;

@ApplicationScoped
public class HappyCaseSingleConfiguration {

    public static final String PREFIX = "happy.case.configuration.";
    public static final String SINGLE_KEY = PREFIX + "single";
    public static final String SINGLE_VALUE = "singleHappyValue";

    public static final Map<String, String> HAPPY_CASE_MAP = immutableMap(SINGLE_KEY, SINGLE_VALUE);

    @Getter
    @Setter
    private Map<String, String> deltaMap = new HashMap<>();

    @PortalConfigurationChangeInterceptor(key = SINGLE_KEY)
    void configurationChangeEventListener(
            @Observes @PortalConfigurationChangeEvent final Map<String, String> deltaMap) {
        this.deltaMap = deltaMap;
    }

    public void reset() {
        deltaMap = new HashMap<>();
    }

    public boolean isEmpty() {
        return deltaMap.isEmpty();
    }
}
