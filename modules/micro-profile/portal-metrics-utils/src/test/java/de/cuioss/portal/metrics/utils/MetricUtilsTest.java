package de.cuioss.portal.metrics.utils;

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.ws.rs.WebApplicationException;

import org.eclipse.microprofile.metrics.Tag;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.MetricsConfigKeys;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;

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
        var metricId = MetricsUtils.createMetricIdBuilder("name", new MetricUtilsTestException(),
                immutableList(
                        new Tag("tag1", "a-value"),
                        new Tag("tag2", "b-value")),
                immutableList(
                        throwable -> new Tag("mapper1", "value"),
                        throwable -> new Tag("mapper2", "value")))
                .build();
        assertTrue(metricId.getTags().containsKey("tag1"));
        assertTrue(metricId.getTags().containsKey("tag2"));
        assertTrue(metricId.getTags().containsKey("mapper1"));
        assertTrue(metricId.getTags().containsKey("mapper2"));
        assertTrue(metricId.getTags().containsKey("cause"));
    }

    public static class MetricUtilsTestException extends Exception {

        private static final long serialVersionUID = 1L;
    }
}
