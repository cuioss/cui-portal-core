package de.cuioss.portal.configuration.initializer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

/**
 * Defines an lifecycle methods and the (relative order) when this should happen.
 * To be used for {@link ApplicationScoped} beans only that need to be initialized in a
 * specific order.
 * <ul>
 * <li>The {@link #initialize()} replaces any {@link PostConstruct} method</li>
 * <li>The {@link #destroy()} replaces the {@link PreDestroy} method. The default implementation for
 * this method is NOOP</li>
 * <li>The {@link #getOrder()} defines an initialization method and the (relative order) when this
 * should happen. The default implementation returns {@link #ORDER_INTERMEDIATE}
 * </ul>
 *
 * @author Oliver Wolff
 */
@SuppressWarnings("squid:S1214") // owolff: constants in interfaces are OK for us
public interface ApplicationInitializer extends Comparable<ApplicationInitializer> {

    /**
     * Actually initializes the specified bean. Analogous to {@link PostConstruct}
     */
    void initialize();

    /**
     * Destroys / cleans up the bean, analogous to {@link PreDestroy}. The default implementation by
     * this interface is a NOOP implementation.
     */
    default void destroy() {
    }

    /**
     * The default implementation of {@link Comparable#compareTo(Object)}
     */
    @Override
    default int compareTo(final ApplicationInitializer other) {
        return other.getOrder().compareTo(getOrder());
    }

    /**
     * @return an {@link Integer} defining the order the individual implementations will be
     *         {@link #initialize()}. The higher the number, the earlier the bean will be
     *         initialized. Always use the provided constants. The default implementation returns
     *         {@link #ORDER_INTERMEDIATE}
     */
    default Integer getOrder() {
        return ORDER_INTERMEDIATE;
    }

    /** Defines a high order, saying the initializing will be done quite early in the process. */
    Integer ORDER_EARLY = 100;

    /**
     * Defines a medium order, saying the initializing will be done somewhere in between the
     * initialization process.
     */
    Integer ORDER_INTERMEDIATE = 50;

    /**
     * Defines a low order, saying the initializing will be done at the end of the
     * initialization process.
     */
    Integer ORDER_LATE = 10;
}
