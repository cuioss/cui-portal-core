/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.metrics.utils;

import de.cuioss.portal.configuration.MetricsConfigKeys;
import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.easymock.EasyMock;
import org.eclipse.microprofile.metrics.Tag;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.Serial;
import java.lang.reflect.Field;

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;
import static org.junit.jupiter.api.Assertions.*;

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
        } /*~~(TODO: Catch specific not Exception. Suppress: // cui-rewrite:disable InvalidExceptionUsageRecipe)~~>*/catch (Exception e) {
            /*~~(TODO: Throw specific not RuntimeException. Suppress: // cui-rewrite:disable InvalidExceptionUsageRecipe)~~>*/throw new RuntimeException("Failed to reset MetricsUtils state", e);
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
            // Mock the Response.getStatus() method
            Response mockResponse = EasyMock.createNiceMock(Response.class);
            EasyMock.expect(mockResponse.getStatus()).andReturn(666).anyTimes();
            EasyMock.replay(mockResponse);

            // Create a mock WebApplicationException that returns our mock Response
            WebApplicationException mockException = EasyMock.createNiceMock(WebApplicationException.class);
            EasyMock.expect(mockException.getResponse()).andReturn(mockResponse).anyTimes();
            EasyMock.replay(mockException);

            var metricID = MetricsUtils.createMetricId("test", mockException);
            assertEquals("666", metricID.getTags().get("http_status"),
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

    @Nested
    @DisplayName("HTTP Status Code Tag Tests")
    class HttpStatusCodeTagTests {
        @Test
        @DisplayName("Should create tag with HTTP status code")
        void shouldCreateTagWithHttpStatusCode() {
            // Create mock Response
            Response mockResponse = EasyMock.createNiceMock(Response.class);
            EasyMock.expect(mockResponse.getStatus()).andReturn(200).anyTimes();
            EasyMock.replay(mockResponse);

            var tag = MetricsUtils.createHttpStatusCodeTag(mockResponse);

            assertEquals("http_status", tag.getTagName(), "Tag name should be 'http_status'");
            assertEquals("200", tag.getTagValue(), "Tag value should be '200'");
        }

        @Test
        @DisplayName("Should return null for null response")
        void shouldReturnNullForNullResponse() {
            var tag = MetricsUtils.createHttpStatusCodeTag(null);

            assertNull(tag, "Tag should be null for null response");
        }
    }
}
