package de.cuioss.portal.core.test.producer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

@EnableAutoWeld
@AddBeanClasses(TestServletMockProducers.class)
class TestServletMockProducersTest {

    @Inject
    private Provider<HttpServletRequest> requestProvider;

    @Inject
    private Provider<HttpServletResponse> responseProvider;

    @Test
    void shouldProduceREquestAndResponse() {
        assertNotNull(requestProvider.get());
        assertNotNull(responseProvider.get());
    }

}
