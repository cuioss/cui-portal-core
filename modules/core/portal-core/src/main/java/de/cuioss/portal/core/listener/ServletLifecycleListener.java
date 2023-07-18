package de.cuioss.portal.core.listener;

import static de.cuioss.tools.collect.CollectionLiterals.mutableList;

import java.util.Collections;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.Initialized;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.core.servlet.CuiContextPath;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Central Listener for Application life-cycle events.
 * <p>
 * Currently it listens to the {@link Initialized} for the
 * {@link ServletContext} and {@link PreDestroy}, uses it for an ordered
 * configuration, see {@link ApplicationInitializer}
 * </p>
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@WebListener
public class ServletLifecycleListener implements ServletContextListener {

    private static final CuiLogger LOGGER = new CuiLogger(ServletLifecycleListener.class);

    @Inject
    @PortalInitializer
    private Instance<ApplicationInitializer> applicationInitializers;

    @Produces
    @Dependent
    @Named
    @CuiContextPath
    private String contextPath = "portal";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        contextPath = sce.getServletContext().getContextPath();
        LOGGER.info("Initializing Context for {}", contextPath);
        applicationInitializerListener(sce.getServletContext());
    }

    /**
     * Initializes all available {@link ApplicationInitializer} instances according
     * to their {@link ApplicationInitializer#getOrder()}
     *
     * @param context currently unused
     */
    private void applicationInitializerListener(final ServletContext context) {
        final List<ApplicationInitializer> initializers = mutableList(applicationInitializers);
        Collections.sort(initializers);
        LOGGER.debug("ServletLifecycleListener called for '{}', initializing with order: {}", contextPath,
                initializers);
        for (final ApplicationInitializer applicationInitializer : initializers) {
            LOGGER.trace("Initializing '{}' for '{}'", applicationInitializer, context);
            applicationInitializer.initialize();
        }
        LOGGER.debug("Initialize successfully called for all elements for '{}'", contextPath);
    }

    /**
     * Destroys all available {@link ApplicationInitializer} instances according to
     * their <em>reversed</em> {@link ApplicationInitializer#getOrder()}
     */
    @PreDestroy
    public void applicationDestroyListener() {
        LOGGER.debug("Executing applicationDestroyListener");
        LOGGER.info("Portal-008: Shutting down '{}'", contextPath);
        final List<ApplicationInitializer> finalizer = mutableList(applicationInitializers);
        Collections.sort(finalizer, Collections.reverseOrder());
        LOGGER.debug("ServletLifecycleListener called for '{}', finalizing with order: {}", contextPath, finalizer);
        for (final ApplicationInitializer applicationInitializer : finalizer) {
            LOGGER.trace("Destroying '{}' for '{}'", applicationInitializer, contextPath);
            try {
                applicationInitializer.destroy();
            } catch (RuntimeException e) {
                LOGGER.warn(
                        "Runtime Exception occurred while trying to destroy '{}' for '{}'. message='{}', stracktrace will be available at DEBUG-level",
                        applicationInitializer, contextPath, e.getMessage());
                LOGGER.debug("Detailed exception", e);
            }
        }
        LOGGER.debug("Finalize successfully called for all elements for '{}'", contextPath);
    }

}
