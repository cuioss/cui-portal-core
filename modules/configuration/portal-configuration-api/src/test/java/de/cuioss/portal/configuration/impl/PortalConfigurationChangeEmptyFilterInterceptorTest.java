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
package de.cuioss.portal.configuration.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.jboss.weld.junit5.auto.AddEnabledInterceptors;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.impl.support.EmptyFilterConfiguration;
import de.cuioss.tools.collect.CollectionLiterals;

@EnableAutoWeld
@AddEnabledInterceptors(PortalConfigurationChangeInterceptorImpl.class)
class PortalConfigurationChangeEmptyFilterInterceptorTest {

    static final Map<String, String> UNKNOWN_ELEMENTS = CollectionLiterals.immutableMap("key1", "value1", "key2",
            "value2", "key3", "value3");

    @SuppressWarnings("unused")
    @Inject
    private EmptyFilterConfiguration emptyFilterConfiguration;

    @Inject
    @PortalConfigurationChangeEvent
    private Event<Map<String, String>> event;

    @Test
    void shouldFailOnEvent() {
        assertThrows(IllegalStateException.class, () -> {
            event.fire(UNKNOWN_ELEMENTS);
        });
    }

}
