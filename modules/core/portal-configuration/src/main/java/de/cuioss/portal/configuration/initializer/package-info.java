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
