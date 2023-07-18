package de.cuioss.portal.core.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.test.jsf.mocks.CuiMockHttpServletRequest;

@EnableAutoWeld
@AddBeanClasses(ExternalHostnameProducer.class)
class ExternalHostnameProducerTest {

    @Produces
    private CuiMockHttpServletRequest request = new CuiMockHttpServletRequest();

    @CuiExternalHostname
    @Inject
    private Provider<String> hostnameProvider;

    String host;

    @BeforeEach
    void beforeEach() {
        var context = "/context";
        var path = "/request";
        host = "https://myapp:8080";
        var currentUrl = host + context + path;
        request.setRequestUrl(currentUrl);
        request.setContextPath(context);
        request.setPathInfo(path);
    }

    @Test
    void shouldProvideHostname() {
        assertEquals(host, hostnameProvider.get());
    }
}
