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
 * Provides utility classes for Portal resource management and loading.
 * 
 * <h2>Overview</h2>
 * This package contains utility classes for loading and managing resources in
 * Portal applications. It provides standardized access to application resources
 * across different environments and deployment scenarios.
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.common.util.PortalResourceLoader} - Utility class
 *       for loading resources from the classpath and web context</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * <pre>
 * // Load a classpath resource
 * InputStream configFile = PortalResourceLoader.getResourceAsStream("config/app.properties");
 * 
 * // Check if a resource exists
 * boolean exists = PortalResourceLoader.resourceExists("templates/email.html");
 * 
 * // Load multiple resources
 * List&lt;URL&gt; templates = PortalResourceLoader.getResources("templates/*.html");
 * </pre>
 * 
 * <h2>Resource Resolution</h2>
 * Resources are resolved in the following order:
 * <ol>
 *   <li>Current thread's context classloader</li>
 *   <li>Portal module classloader</li>
 *   <li>Web application context (if available)</li>
 * </ol>
 * 
 * <h2>Thread Safety</h2>
 * All components in this package are thread-safe and can be safely used
 * in concurrent environments.
 * 
 * @author Oliver Wolff
 * @see java.lang.ClassLoader#getResource
 * @see jakarta.servlet.ServletContext#getResource
 */
package de.cuioss.portal.common.util;
