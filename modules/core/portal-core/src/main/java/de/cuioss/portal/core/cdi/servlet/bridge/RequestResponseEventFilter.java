package de.cuioss.portal.core.cdi.servlet.bridge;

import java.io.IOException;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import de.cuioss.portal.core.cdi.servlet.literal.DestroyedLiteral;
import de.cuioss.portal.core.cdi.servlet.literal.Initialized;
import de.cuioss.portal.core.cdi.servlet.literal.InitializedLiteral;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Inspired by deltaspike. Fires events for {@link Initialized} and
 * {@link Destroyed}.
 */
public class RequestResponseEventFilter implements Filter {

    private static final CuiLogger LOGGER = new CuiLogger(RequestResponseEventFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        var beanManager = CDI.current().getBeanManager();

        LOGGER.trace("Fire @Initialized events");
        beanManager.fireEvent(request, InitializedLiteral.INSTANCE);
        beanManager.fireEvent(response, InitializedLiteral.INSTANCE);

        try {
            LOGGER.trace("Execute Chain");
            chain.doFilter(request, response);
        } finally {
            LOGGER.trace("Fire @Destroyed events");
            beanManager.fireEvent(request, DestroyedLiteral.INSTANCE);
            beanManager.fireEvent(response, DestroyedLiteral.INSTANCE);
        }
    }

}
