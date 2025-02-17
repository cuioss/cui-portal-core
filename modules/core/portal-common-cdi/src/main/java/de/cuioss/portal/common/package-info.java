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

/**
 * Provides core CDI-based infrastructure and utilities for Portal applications.
 * 
 * <h2>Overview</h2>
 * This package serves as the foundation for CDI integration in Portal applications.
 * It provides essential infrastructure parts, utilities, and services that
 * enable seamless CDI integration across the Portal framework.
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>CDI bean management and utilities</li>
 *   <li>Resource bundle integration</li>
 *   <li>Locale management</li>
 *   <li>Priority-based component ordering</li>
 * </ul>
 * 
 * <h2>Package Organization</h2>
 * <ul>
 *   <li>{@code cdi} - Core CDI utilities and infrastructure</li>
 *   <li>{@code bundle} - Resource bundle management system</li>
 *   <li>{@code locale} - Locale management and change notification</li>
 *   <li>{@code priority} - Priority-based ordering utilities</li>
 * </ul>
 * 
 * <h2>Best Practices</h2>
 * <ul>
 *   <li>Use provided CDI utilities instead of direct {@code BeanManager} access</li>
 *   <li>Leverage resource bundle system for internationalization</li>
 *   <li>Follow priority guidelines for component ordering</li>
 *   <li>Observe locale change events for dynamic language updates</li>
 * </ul>
 * 
 * <h2>Thread Safety</h2>
 * All components in this package and its subpackages are designed to be thread-safe
 * and suitable for use in concurrent web applications.
 * 
 * @author Oliver Wolff
 * @see jakarta.enterprise.inject.spi.CDI
 * @see java.util.ResourceBundle
 * @see jakarta.annotation.Priority
 */
package de.cuioss.portal.common;
