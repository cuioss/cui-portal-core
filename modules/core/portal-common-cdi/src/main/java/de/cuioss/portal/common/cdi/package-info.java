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
 * Provides core CDI utilities and infrastructure components for the Portal framework.
 * 
 * <h2>Overview</h2>
 * This package contains essential CDI-related utilities and components that form the
 * foundation of the Portal's dependency injection infrastructure. It provides tools
 * for programmatic bean management, annotation handling, and CDI extension support.
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.common.cdi.PortalBeanManager} - Utility class for
 *       programmatic CDI bean access and management</li>
 *   <li>{@link de.cuioss.portal.common.cdi.AnnotationInstanceProvider} - Creates
 *       runtime instances of annotations using dynamic proxies</li>
 * </ul>
 * 
 * <h2>Usage Guidelines</h2>
 * <ul>
 *   <li>Use {@code PortalBeanManager} for programmatic bean access instead of direct
 *       {@code BeanManager} usage</li>
 *   <li>Leverage {@code AnnotationInstanceProvider} for dynamic annotation creation
 *       in CDI extensions</li>
 *   <li>Follow CDI best practices for bean scoping and lifecycle management</li>
 * </ul>
 * 
 * <h2>Thread Safety</h2>
 * All components in this package are designed to be thread-safe and suitable for
 * use in concurrent environments.
 * 
 * @author Oliver Wolff
 */
package de.cuioss.portal.common.cdi;
