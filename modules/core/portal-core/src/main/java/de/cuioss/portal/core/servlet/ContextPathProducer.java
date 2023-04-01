package de.cuioss.portal.core.servlet;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.ServletContext;

import org.apache.deltaspike.core.api.common.DeltaSpike;

/**
 * Produces the servlets context path.
 */
@ApplicationScoped
public class ContextPathProducer {

    @Inject
    @DeltaSpike
    private Provider<ServletContext> servletContext;

    /**
     * @return the derived context path
     */
    @Produces
    @Dependent
    @Named
    @CuiContextPath
    String getContextPath() {
        return this.servletContext.get().getContextPath();
    }
}
