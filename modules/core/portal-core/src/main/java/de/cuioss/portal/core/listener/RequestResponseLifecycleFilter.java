package de.cuioss.portal.core.listener;

import java.io.IOException;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import de.cuioss.portal.core.listener.literal.ServletDestroyed;
import de.cuioss.portal.core.listener.literal.ServletDestroyedLiteral;
import de.cuioss.portal.core.listener.literal.ServletInitialized;
import de.cuioss.portal.core.listener.literal.ServletInitializedLiteral;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Inspired by deltaspike. Fires events for {@link ServletInitialized} and
 * {@link ServletDestroyed}.
 */
public class RequestResponseLifecycleFilter implements Filter {

    private static final CuiLogger LOGGER = new CuiLogger(RequestResponseLifecycleFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        var beanManager = CDI.current().getBeanManager();

        LOGGER.trace("Fire @Initialized events");
        beanManager.fireEvent(request, ServletInitializedLiteral.INSTANCE);
        beanManager.fireEvent(response, ServletInitializedLiteral.INSTANCE);

        try {
            LOGGER.trace("Execute Chain");
            chain.doFilter(request, response);
        } finally {
            LOGGER.trace("Fire @Destroyed events");
            beanManager.fireEvent(request, ServletDestroyedLiteral.INSTANCE);
            beanManager.fireEvent(response, ServletDestroyedLiteral.INSTANCE);
        }
    }

}
