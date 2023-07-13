package de.cuioss.portal.core.test.producer;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.test.mock.MockHttpServletResponse;

import de.cuioss.test.jsf.mocks.CuiMockHttpServletRequest;
import de.cuioss.test.jsf.producer.TestHttpServletRequestProducer;
import de.cuioss.test.jsf.producer.TestHttpServletResponseProducer;
import de.cuioss.test.jsf.producer.TestServletProducers;
import lombok.Getter;
import lombok.Setter;

/**
 * Produces {@link HttpServletRequest} and {@link HttpServletResponse}. In
 * contrast to {@link TestServletProducers} it is not fetching them from the
 * {@link FacesContext} but instantiate them directly.
 *
 * It is only meant to produce Objects that are usually provided by the
 * container and therefore are needed on special circumstances
 *
 * It is designed as 'opt-in'. Use with {@code @AddBeanClasses}.
 *
 * @see TestHttpServletRequestProducer
 * @see TestHttpServletResponseProducer
 */
public class TestServletMockProducers {

    @Getter
    @Setter
    private CuiMockHttpServletRequest servletRequest = new CuiMockHttpServletRequest();

    @Getter
    @Setter
    private MockHttpServletResponse servletResponse = new MockHttpServletResponse();

    @Produces
    @Typed({ HttpServletRequest.class })
    @RequestScoped
    HttpServletRequest produceServletRequest() {
        return servletRequest;
    }

    @Produces
    @Typed({ HttpServletResponse.class })
    @RequestScoped
    HttpServletResponse produceServletResponse() {
        return servletResponse;
    }
}
