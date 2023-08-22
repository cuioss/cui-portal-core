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
package de.cuioss.portal.tracing;

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_REPORTER_URL;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAlternatives;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

/**
 * @author Sven Haag
 */
@EnableAutoWeld
@AddBeanClasses(ZipkinReporterProducer.class)
@EnableAlternatives(ZipkinReporterProducer.class)
@EnablePortalConfiguration
class ZipkinReporterProducerTest {

    @Inject
    private Provider<Reporter<Span>> reporter;

    @Inject
    @PortalConfigurationSource
    private PortalTestConfiguration configuration;

    @Test
    void injects() {
        assertNull(reporter.get(), "Reporter<Span> must not be null");
        configuration.fireEvent(PORTAL_TRACING_REPORTER_URL, "http://localhost");
        assertNotNull(reporter.get(), "Reporter<Span> must not be null");
    }
}
