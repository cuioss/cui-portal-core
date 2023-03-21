package de.cuioss.portal.core.servlet;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import de.cuioss.tools.logging.CuiLogger;

/**
 * To retrieve the external hostname including port of the request
 */
@ApplicationScoped
public class ExternalHostnameProducer {

    private static final CuiLogger log = new CuiLogger(ExternalHostnameProducer.class);

    @Inject
    private Provider<HttpServletRequest> httpServletRequest;

    @Produces
    @Dependent
    @Named
    @CuiExternalHostname
    String getExternalHostname() {
        var request = httpServletRequest.get();
        var url = request.getRequestURL();
        var uri = request.getRequestURI();
        var hostname = url.substring(0, url.indexOf(uri));
        log.debug("Resolved hostname: {}", hostname);
        return hostname;
    }

}
