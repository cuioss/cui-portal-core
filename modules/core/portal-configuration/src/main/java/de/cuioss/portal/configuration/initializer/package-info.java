/**
 * Provides a deterministic initialization framework for {@link jakarta.enterprise.context.ApplicationScoped}
 * beans in the Portal Configuration module.
 * 
 * <h2>Overview</h2>
 * This package solves the problem of non-deterministic initialization order when using
 * {@link jakarta.annotation.PostConstruct} with application-scoped beans. Instead of relying
 * on access order, it provides a structured way to define initialization order through
 * explicit priorities.
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.initializer.ApplicationInitializer} - 
 *       Interface defining the initialization contract and ordering</li>
 *   <li>{@link de.cuioss.portal.configuration.initializer.PortalInitializer} - 
 *       Qualifier for identifying initializer beans</li>
 * </ul>
 * 
 * <h2>Initialization Order</h2>
 * The framework provides three standard priority levels:
 * <ul>
 *   <li>{@code ORDER_EARLY = 100} - For beans that need early initialization</li>
 *   <li>{@code ORDER_INTERMEDIATE = 50} - For standard initialization (default)</li>
 *   <li>{@code ORDER_LATE = 10} - For beans that should be initialized last</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * To use this framework:
 * <ol>
 *   <li>Implement {@link de.cuioss.portal.configuration.initializer.ApplicationInitializer}</li>
 *   <li>Add {@link de.cuioss.portal.configuration.initializer.PortalInitializer} qualifier</li>
 *   <li>Override {@code getOrder()} if needed (defaults to INTERMEDIATE)</li>
 *   <li>Implement {@code initialize()} method with initialization logic</li>
 * </ol>
 * 
 * <h2>Benefits</h2>
 * <ul>
 *   <li>Deterministic initialization order</li>
 *   <li>Clear separation of initialization concerns</li>
 *   <li>Standardized cleanup through destroy() method</li>
 *   <li>Easy to understand priority system</li>
 * </ul>
 * 
 * @author Oliver Wolff
 */
package de.cuioss.portal.configuration.initializer;
