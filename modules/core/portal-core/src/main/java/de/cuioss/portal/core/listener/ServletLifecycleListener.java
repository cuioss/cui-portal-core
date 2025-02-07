/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.core.listener;

import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.core.PortalCoreLogMessages;
import de.cuioss.portal.core.servlet.CuiContextPath;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.Collections;
import java.util.List;

import static de.cuioss.tools.collect.CollectionLiterals.mutableList;

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
    Instance<ApplicationInitializer> applicationInitializers;

    @Produces
    @Dependent
    @Named
    @CuiContextPath
    String contextPath = "portal";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        contextPath = sce.getServletContext().getContextPath();
        LOGGER.info(PortalCoreLogMessages.LIFECYCLE_CONTEXT_INITIALIZING.format(contextPath));
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
        LOGGER.debug(PortalCoreLogMessages.LIFECYCLE_INITIALIZER_DEBUG.format(contextPath, initializers));
        for (final ApplicationInitializer applicationInitializer : initializers) {
            LOGGER.debug(PortalCoreLogMessages.LIFECYCLE_INITIALIZING_COMPONENT.format(applicationInitializer, context));
            applicationInitializer.initialize();
        }
        LOGGER.debug(PortalCoreLogMessages.LIFECYCLE_INITIALIZE_COMPLETE.format(contextPath));
    }

    /**
     * Destroys all available {@link ApplicationInitializer} instances according to
     * their <em>reversed</em> {@link ApplicationInitializer#getOrder()}
     */
    @PreDestroy
    public void applicationDestroyListener() {
        LOGGER.debug(PortalCoreLogMessages.LIFECYCLE_DESTROYING.format(contextPath));
        LOGGER.info(PortalCoreLogMessages.LIFECYCLE_CONTEXT_SHUTDOWN.format(contextPath));
        final List<ApplicationInitializer> finalizer = mutableList(applicationInitializers);
        finalizer.sort(Collections.reverseOrder());
        LOGGER.debug(PortalCoreLogMessages.LIFECYCLE_FINALIZER_DEBUG.format(contextPath, finalizer));
        for (final ApplicationInitializer applicationInitializer : finalizer) {
            LOGGER.debug(PortalCoreLogMessages.LIFECYCLE_DESTROYING_COMPONENT.format(applicationInitializer, contextPath));
            try {
                applicationInitializer.destroy();
            } catch (RuntimeException e) {
                LOGGER.warn(PortalCoreLogMessages.LIFECYCLE_DESTROY_ERROR.format(applicationInitializer, contextPath, e.getMessage()));
                LOGGER.debug(e, "Detailed exception");
            }
        }
        LOGGER.debug(PortalCoreLogMessages.LIFECYCLE_FINALIZE_COMPLETE.format(contextPath));
    }

}
