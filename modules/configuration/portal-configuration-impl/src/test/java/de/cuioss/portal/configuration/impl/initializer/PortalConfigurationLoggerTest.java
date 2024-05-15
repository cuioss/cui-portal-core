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
package de.cuioss.portal.configuration.impl.initializer;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import lombok.Getter;

@EnableAutoWeld
@EnableTestLogger
class PortalConfigurationLoggerTest {

    @Inject
    @Getter
    @PortalInitializer
    private PortalConfigurationLogger underTest;

    @Inject
    @PortalInitializer
    private Instance<ApplicationInitializer> applicationInitializers;

    @BeforeEach
    void beforeTest() {
        applicationInitializers.forEach(ApplicationInitializer::initialize);
    }

    @Test
    void shouldIgnoreOnInfoLevel() {
        TestLogLevel.INFO.addLogger(PortalConfigurationLogger.class);
        underTest.initialize();
        LogAsserts.assertNoLogMessagePresent(TestLogLevel.WARN, PortalConfigurationLogger.class);
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Portal Configuration");
    }
}
