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

import de.cuioss.portal.configuration.MetricsConfigKeys;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.metrics.Tag;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.Serial;

@EnableAutoWeld
@EnablePortalConfiguration(configuration = MetricsConfigKeys.PORTAL_METRICS_APP_NAME + ":test-app")
class MetricUtilsTest {

    @Test
    void containsAppNameTag() {
        var metricID = MetricsUtils.createMetricId("test");
        assertEquals("test-app", metricID.getTags().get("_app"));
    }

    @Test
    void containsExceptionClassNameTag() {
        var metricID = MetricsUtils.createMetricId("test", new MetricUtilsTestException());
        var tags = metricID.getTags();
        assertEquals(MetricUtilsTestException.class.getName(), tags.get("cause"));
        assertEquals("test-app", tags.get("_app"));
    }

    @Test
    @Disabled("Caused by: java.lang.ClassNotFoundException: org.glassfish.jersey.internal.RuntimeDelegateImpl")
    void containsWebApplicationExceptionHttpStatusCodeTag() {
        var metricID = MetricsUtils.createMetricId("test", new WebApplicationException(666));
        assertEquals("666", metricID.getTags().get("httpStatusCode"));
    }

    @Test
    void buildMetricIdWithAdditionalTagsAndExceptionMapperTags() {
        var metricId = MetricsUtils
                .createMetricIdBuilder("name", new MetricUtilsTestException(),
                        immutableList(new Tag("tag1", "a-value"), new Tag("tag2", "b-value")), immutableList(
                        throwable -> new Tag("mapper1", "value"), throwable -> new Tag("mapper2", "value")))
                .build();
        assertTrue(metricId.getTags().containsKey("tag1"));
        assertTrue(metricId.getTags().containsKey("tag2"));
        assertTrue(metricId.getTags().containsKey("mapper1"));
        assertTrue(metricId.getTags().containsKey("mapper2"));
        assertTrue(metricId.getTags().containsKey("cause"));
    }

    public static class MetricUtilsTestException extends Exception {

        @Serial
        private static final long serialVersionUID = 1L;
    }
}
