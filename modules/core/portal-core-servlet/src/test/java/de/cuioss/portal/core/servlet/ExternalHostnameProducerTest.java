package de.cuioss.portal.core.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.deltaspike.core.api.common.DeltaSpike;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.test.jsf.mocks.CuiMockHttpServletRequest;

@EnableAutoWeld
@AddBeanClasses(ExternalHostnameProducer.class)
class ExternalHostnameProducerTest {

    @Produces
    @DeltaSpike
    private CuiMockHttpServletRequest request = new CuiMockHttpServletRequest();

    @CuiExternalHostname
    @Inject
    private Provider<String> hostnameProvider;

    String host;

    @BeforeEach
    void beforeEach() {
        String context = "/context";
        String path = "/request";
        host = "https://myapp:8080";
        String currentUrl = host + context + path;
        request.setRequestUrl(currentUrl);
        request.setContextPath(context);
        request.setPathInfo(path);
    }

    @Test
    void shouldProvideHostname() {
        assertEquals(host, hostnameProvider.get());
    }
}
