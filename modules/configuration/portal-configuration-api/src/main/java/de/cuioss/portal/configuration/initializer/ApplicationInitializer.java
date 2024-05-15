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
package de.cuioss.portal.configuration.initializer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Defines lifecycle methods and the (relative order) when this should
 * happen. To be used for {@link ApplicationScoped} beans only that need to be
 * initialized in a specific order.
 * <ul>
 * <li>The {@link #initialize()} replaces any {@link PostConstruct} method</li>
 * <li>The {@link #destroy()} replaces the {@link PreDestroy} method. The
 * default implementation for this method is NOOP</li>
 * <li>The {@link #getOrder()} defines an initialization method and the
 * (relative order) when this should happen. The default implementation returns
 * {@link #ORDER_INTERMEDIATE}
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
     * Destroys / cleans up the bean, analogous to {@link PreDestroy}. The default
     * implementation by this interface is a NOOP implementation.
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
     * @return an {@link Integer} defining the order the individual implementations
     * will be {@link #initialize()}. The higher the number, the earlier the
     * bean will be initialized. Always use the provided constants. The
     * default implementation returns {@link #ORDER_INTERMEDIATE}
     */
    default Integer getOrder() {
        return ORDER_INTERMEDIATE;
    }

    /**
     * Defines a high order, saying the initializing will be done quite early in the
     * process.
     */
    Integer ORDER_EARLY = 100;

    /**
     * Defines a medium order, saying the initializing will be done somewhere in
     * between the initialization process.
     */
    Integer ORDER_INTERMEDIATE = 50;

    /**
     * Defines a low order, saying the initializing will be done at the end of the
     * initialization process.
     */
    Integer ORDER_LATE = 10;
}
