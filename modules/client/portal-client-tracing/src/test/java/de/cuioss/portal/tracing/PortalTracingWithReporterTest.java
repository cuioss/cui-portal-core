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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAlternatives;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.tools.logging.CuiLogger;
import lombok.ToString;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

@EnableAutoWeld
@EnablePortalConfiguration
@AddBeanClasses({ PortalTracing.class })
@EnableAlternatives(PortalTracingWithReporterTest.class)
class PortalTracingWithReporterTest {

    private Reporter<Span> reporter;

    @Inject
    private Provider<Reporter<Span>> reporterProvider;

    @Test
    void injects() {
        reporter = new SpanTestReporterProducer();
        assertNotNull(reporterProvider.get(), "Alternative Reporter<Span> must not be null");
    }

    @Test
    void reports() {
        var testSpanReporter = new SpanTestReporterProducer();
        reporter = testSpanReporter;
        PortalTracing.createTracing().tracer().newTrace().finish();
        assertEquals(1, testSpanReporter.spans.size());
    }

    @Produces
    @Dependent
    @Alternative
    Reporter<Span> zipkinReporter() {
        return reporter;
    }

    @ToString
    static class SpanTestReporterProducer implements Reporter<Span> {

        private static final CuiLogger log = new CuiLogger(SpanTestReporterProducer.class);

        private final List<String> spans;

        SpanTestReporterProducer() {
            spans = new ArrayList<>();
        }

        @Override
        public void report(final Span span) {
            log.info(span.toString());
            spans.add(span.toString());
        }
    }
}
