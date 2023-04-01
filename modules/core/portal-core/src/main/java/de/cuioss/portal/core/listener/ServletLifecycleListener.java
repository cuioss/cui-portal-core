package de.cuioss.portal.core.listener;

import static de.cuioss.tools.collect.CollectionLiterals.mutableList;

import java.util.Collections;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.deltaspike.core.api.lifecycle.Initialized;

import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.core.servlet.CuiContextPath;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Central Listener for Application life-cycle events.
 * <p>
 * Currently it listens to the {@link Initialized} for the
 * {@link ServletContext} and {@link PreDestroy}, uses it for an ordered configuration, see
 * {@link ApplicationInitializer}
 * </p>
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
public class ServletLifecycleListener {

    private static final CuiLogger LOGGER = new CuiLogger(ServletLifecycleListener.class);

    @Inject
    @PortalInitializer
    private Instance<ApplicationInitializer> applicationInitializers;

    @Inject
    @CuiContextPath
    private Instance<String> contextPath;

    /**
     * Stores contextPath locally for situations, where the resolving is not posible anymore, what
     * may happen on shutdown of a system.
     */
    private String resolvedContextPath;

    /**
     * Initializes all available {@link ApplicationInitializer} instances according to their
     * {@link ApplicationInitializer#getOrder()}
     *
     * @param context currently unused
     */
    public void applicationInitializerListener(
            @Observes @Initialized final ServletContext context) {
        var path = getContextPath();
        final List<ApplicationInitializer> initializers =
            mutableList(applicationInitializers);
        Collections.sort(initializers);
        LOGGER.debug("ServletLifecycleListener called for '{}', initializing with order: {}", path, initializers);
        for (final ApplicationInitializer applicationInitializer : initializers) {
            LOGGER.trace("Initializing '{}' for '{}'", applicationInitializer, path);
            applicationInitializer.initialize();
        }
        LOGGER.debug("Initialize successfully called for all elements for '{}'", path);
    }

    /**
     * Destroys all available {@link ApplicationInitializer} instances according to their
     * <em>reversed</em> {@link ApplicationInitializer#getOrder()}
     */
    @PreDestroy
    public void applicationDestroyListener() {
        LOGGER.debug("Executing applicationDestroyListener");
        var path = getContextPath();
        LOGGER.info("Portal-008: Shutting down '{}'", path);
        final List<ApplicationInitializer> finalizer =
            mutableList(applicationInitializers);
        Collections.sort(finalizer, Collections.reverseOrder());
        LOGGER.debug("ServletLifecycleListener called for '{}', finalizing with order: {}", path, finalizer);
        for (final ApplicationInitializer applicationInitializer : finalizer) {
            LOGGER.trace("Destroying '{}' for '{}'", applicationInitializer, path);
            try {
                applicationInitializer.destroy();
            } catch (RuntimeException e) {
                LOGGER.warn(
                        "Runtime Exception occurred while trying to destroy '{}' for '{}'. message='{}', stracktrace will be available at DEBUG-level",
                        applicationInitializer,
                        path, e.getMessage());
                LOGGER.debug("Detailed exception", e);
            }
        }
        LOGGER.debug("Finalize successfully called for all elements for '{}'", path);
    }

    String getContextPath() {
        if (null != resolvedContextPath) {
            return resolvedContextPath;
        }
        LOGGER.debug("Resolving context path from @CuiContextPath");
        if (contextPath.isResolvable()) {
            try {
                resolvedContextPath = contextPath.get();
                return resolvedContextPath;
                // isResolvable does not prevent runtime exceptions
            } catch (RuntimeException e) {
                LOGGER.debug("Unable to resolve contextPath at runtime", e);
            }
        }
        return "portal";
    }

}
