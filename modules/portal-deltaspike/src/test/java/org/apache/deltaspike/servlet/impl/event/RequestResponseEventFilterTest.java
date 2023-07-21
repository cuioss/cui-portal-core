package org.apache.deltaspike.servlet.impl.event;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.deltaspike.core.api.lifecycle.Initialized;
import org.apache.myfaces.test.mock.MockHttpServletRequest;
import org.apache.myfaces.test.mock.MockHttpServletResponse;
import org.easymock.EasyMock;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@EnableAutoWeld
class RequestResponseEventFilterTest {

    private boolean initRequest;
    private boolean destroyRequest;

    private boolean initResponse;
    private boolean destroyResponse;

    @BeforeEach
    void restEventResults() {
        initRequest = false;
        destroyRequest = false;
        initResponse = false;
        destroyResponse = false;
    }

    @Test
    void shouldFilter() throws IOException, ServletException {
        var filter = new RequestResponseEventFilter();
        FilterChain chain = EasyMock.createNiceMock(FilterChain.class);

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), chain);
        assertTrue(destroyRequest);
        assertTrue(destroyResponse);
        assertTrue(initRequest);
        assertTrue(initResponse);
    }

    void initRequest(@Observes @Initialized HttpServletRequest request) {
        initRequest = true;
    }

    void destroyRequest(@Observes @Initialized HttpServletRequest request) {
        destroyRequest = true;
    }

    void initResponse(@Observes @Initialized HttpServletResponse request) {
        initResponse = true;
    }

    void destroyResponse(@Observes @Initialized HttpServletResponse request) {
        destroyResponse = true;
    }

}
