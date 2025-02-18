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
package de.cuioss.portal.metrics.utils;

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import de.cuioss.portal.configuration.MetricsConfigKeys;
import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.metrics.Tag;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.Serial;
import java.lang.reflect.Field;

@EnableAutoWeld
@EnablePortalConfiguration(configuration = {
        MetricsConfigKeys.PORTAL_METRICS_APP_NAME + ":test-app",
        PortalConfigurationKeys.APPLICATION_CONTEXT_NAME + ":test-app"
})
@DisplayName("MetricUtils Tests")
class MetricUtilsTest {

    @Inject
    private PortalTestConfiguration configuration;

    @BeforeEach
    void resetMetricsUtils() {
        // Reset both the app name and app tag static fields
        try {
            Field appNameField = MetricsUtils.class.getDeclaredField("metricsAppName");
            Field appTagField = MetricsUtils.class.getDeclaredField("metricsAppTag");
            appNameField.setAccessible(true);
            appTagField.setAccessible(true);
            appNameField.set(null, null);
            appTagField.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset MetricsUtils state", e);
        }

        // Reset configuration to ensure clean state
        configuration.update(MetricsConfigKeys.PORTAL_METRICS_APP_NAME, "test-app");
        configuration.update(PortalConfigurationKeys.APPLICATION_CONTEXT_NAME, "test-app");
    }

    @Nested
    @DisplayName("Basic Tag Tests")
    class BasicTagTests {
        @Test
        @DisplayName("Should contain app name tag")
        void containsAppNameTag() {
            var metricID = MetricsUtils.createMetricId("test");
            assertEquals("test-app", metricID.getTags().get("_app"), "App tag should match configuration");
        }

        @Test
        @DisplayName("Should contain exception class name tag")
        void containsExceptionClassNameTag() {
            var metricID = MetricsUtils.createMetricId("test", new MetricUtilsTestException());
            var tags = metricID.getTags();
            assertEquals(MetricUtilsTestException.class.getName(), tags.get("cause"),
                    "Exception class name should be in cause tag");
            assertEquals("test-app", tags.get("_app"), "App tag should still be present");
        }
    }

    @Nested
    @DisplayName("Web Application Exception Tests")
    class WebAppExceptionTests {
        @Test
        @DisplayName("Should contain HTTP status code tag")
        void containsWebApplicationExceptionHttpStatusCodeTag() {
            // This test requires the Jersey RuntimeDelegate
            try {
                Class.forName("org.glassfish.jersey.internal.RuntimeDelegateImpl");
            } catch (ClassNotFoundException e) {
                assumeTrue(false, "Skipping test: Jersey RuntimeDelegate not available");
                return;
            }

            var metricID = MetricsUtils.createMetricId("test", new WebApplicationException(666));
            assertEquals("666", metricID.getTags().get("httpStatusCode"),
                    "HTTP status code should be present in tag");
        }
    }

    @Nested
    @DisplayName("Complex Tag Builder Tests")
    class ComplexTagTests {
        @Test
        @DisplayName("Should build metric ID with additional tags and exception mapper tags")
        void buildMetricIdWithAdditionalTagsAndExceptionMapperTags() {
            var metricId = MetricsUtils
                    .createMetricIdBuilder("name", new MetricUtilsTestException(),
                            immutableList(new Tag("tag1", "a-value"), new Tag("tag2", "b-value")),
                            immutableList(
                                    throwable -> new Tag("mapper1", "value"),
                                    throwable -> new Tag("mapper2", "value")))
                    .build();

            assertTrue(metricId.getTags().containsKey("tag1"), "Custom tag1 should be present");
            assertTrue(metricId.getTags().containsKey("tag2"), "Custom tag2 should be present");
            assertTrue(metricId.getTags().containsKey("mapper1"), "Mapper1 tag should be present");
            assertTrue(metricId.getTags().containsKey("mapper2"), "Mapper2 tag should be present");
            assertTrue(metricId.getTags().containsKey("cause"), "Cause tag should be present");
        }
    }

    static class MetricUtilsTestException extends Exception {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
