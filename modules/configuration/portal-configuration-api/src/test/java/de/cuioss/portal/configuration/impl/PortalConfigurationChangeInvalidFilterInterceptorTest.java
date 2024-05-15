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
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.jboss.weld.junit5.auto.AddEnabledInterceptors;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.impl.support.InvalidFilterConfiguration;

@EnableAutoWeld
@AddEnabledInterceptors(PortalConfigurationChangeInterceptorImpl.class)
class PortalConfigurationChangeInvalidFilterInterceptorTest {

    @Inject
    private InvalidFilterConfiguration invalidConfiguration;

    @Inject
    @PortalConfigurationChangeEvent
    private Event<String> event;

    @Test
    void shouldFailOnEvent() {
        assertTrue(invalidConfiguration.isEmpty());
        assertThrows(IllegalStateException.class, () -> {
            event.fire("boom");
        });
    }

}
