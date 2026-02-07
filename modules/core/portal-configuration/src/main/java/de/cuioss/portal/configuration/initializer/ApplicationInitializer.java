/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.configuration.initializer;

/**
 * Contract for {@code ApplicationScoped} beans requiring deterministic initialization order.
 * This interface replaces the standard {@code PostConstruct} approach with a more
 * controlled initialization mechanism.
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Ordered initialization through {@link #getOrder()}</li>
 *   <li>Explicit initialization via {@link #initialize()}</li>
 *   <li>Optional cleanup with {@link #destroy()}</li>
 *   <li>Natural ordering through {@link Comparable}</li>
 * </ul>
 * 
 * <h2>Initialization Order Constants</h2>
 * <ul>
 *   <li>{@link #ORDER_EARLY} = 100: Early initialization phase</li>
 *   <li>{@link #ORDER_INTERMEDIATE} = 50: Standard initialization phase</li>
 *   <li>{@link #ORDER_LATE} = 10: Late initialization phase</li>
 * </ul>
 * Higher values indicate earlier initialization. Custom values between these constants
 * can be used for fine-grained control.
 * 
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Must be used with {@code ApplicationScoped} beans only</li>
 *   <li>Should be qualified with {@link PortalInitializer}</li>
 *   <li>Implement {@link #initialize()} for initialization logic</li>
 *   <li>Override {@link #getOrder()} to control initialization order</li>
 *   <li>Override {@link #destroy()} if cleanup is needed</li>
 * </ul>
 * 
 * <h2>Example Usage</h2>
 * <pre>
 * &#64;ApplicationScoped
 * &#64;PortalInitializer
 * public class EarlyInitializer implements ApplicationInitializer {
 *     
 *     &#64;Override
 *     public void initialize() {
 *         // Early initialization logic
 *     }
 *     
 *     &#64;Override
 *     public Integer getOrder() {
 *         return ORDER_EARLY;
 *     }
 *     
 *     &#64;Override
 *     public void destroy() {
 *         // Cleanup logic
 *     }
 * }
 * </pre>
 *
 * @author Oliver Wolff
 */
@SuppressWarnings("squid:S1214") // Constants in interfaces are acceptable for this use case
public interface ApplicationInitializer extends Comparable<ApplicationInitializer> {

    /**
     * Performs the actual initialization of the bean. This method replaces any
     * {@code PostConstruct} annotated methods and provides deterministic ordering.
     * <p>
     * Implementation Notes:
     * <ul>
     *   <li>Will be called exactly once during application startup</li>
     *   <li>Should handle its own error cases</li>
     *   <li>Should not depend on other beans' initialization state</li>
     * </ul>
     */
    void initialize();

    /**
     * Performs cleanup operations when the application is shutting down.
     * Analogous to {@code PreDestroy} but with deterministic ordering.
     * <p>
     * The default implementation is a no-op. Override this method to:
     * <ul>
     *   <li>Close resources</li>
     *   <li>Flush caches</li>
     *   <li>Persist state</li>
     *   <li>Perform other cleanup tasks</li>
     * </ul>
     */
    default void destroy() {
    }

    /**
     * Defines the initialization order relative to other initializers.
     * Higher values indicate earlier initialization.
     * <p>
     * Standard values:
     * <ul>
     *   <li>{@link #ORDER_EARLY} (100): Early initialization</li>
     *   <li>{@link #ORDER_INTERMEDIATE} (50): Standard initialization</li>
     *   <li>{@link #ORDER_LATE} (10): Late initialization</li>
     * </ul>
     * Custom values can be used for fine-grained control.
     *
     * @return the initialization order, defaults to {@link #ORDER_INTERMEDIATE}
     */
    default Integer getOrder() {
        return ORDER_INTERMEDIATE;
    }

    /**
     * Implements natural ordering based on {@link #getOrder()}.
     * Higher order values are initialized first.
     *
     * @param other the other initializer to compare with
     * @return comparison result based on order values
     */
    @Override
    default int compareTo(final ApplicationInitializer other) {
        return other.getOrder().compareTo(getOrder());
    }

    /**
     * Indicates early initialization phase. Beans with this order will be
     * initialized before beans with {@link #ORDER_INTERMEDIATE} or {@link #ORDER_LATE}.
     * Use this for core infrastructure components that other beans depend on.
     */
    Integer ORDER_EARLY = 100;

    /**
     * Indicates standard initialization phase. This is the default order
     * used when {@link #getOrder()} is not overridden. Use this for beans
     * that don't have specific ordering requirements.
     */
    Integer ORDER_INTERMEDIATE = 50;

    /**
     * Indicates late initialization phase. Beans with this order will be
     * initialized after beans with {@link #ORDER_EARLY} or {@link #ORDER_INTERMEDIATE}.
     * Use this for beans that depend on other initialized components.
     */
    Integer ORDER_LATE = 10;
}
