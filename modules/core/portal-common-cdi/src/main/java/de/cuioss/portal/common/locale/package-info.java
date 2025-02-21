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
 * Provides locale management and change notification support for Portal applications.
 * 
 * <h2>Overview</h2>
 * This package contains components for managing application locales and handling
 * locale changes in a CDI-enabled environment. It provides infrastructure for
 * dynamic locale switching and notification of locale-dependent components.
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.common.locale.PortalLocale} - Qualifier annotation
 *       for injecting the current portal locale</li>
 *   <li>{@link de.cuioss.portal.common.locale.LocaleChangeEvent} - CDI event fired
 *       when the application locale changes</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * 
 * <h3>Accessing Current Locale</h3>
 * <pre>
 * &#064;Inject
 * &#064;PortalLocale
 * private Locale currentLocale;
 * </pre>
 * 
 * <h3>Observing Locale Changes</h3>
 * <pre>
 * public void onLocaleChange(&#064;Observes LocaleChangeEvent event) {
 *     Locale oldLocale = event.getOldLocale();
 *     Locale newLocale = event.getNewLocale();
 *     // Handle locale change
 * }
 * </pre>
 * 
 * <h2>Integration</h2>
 * <ul>
 *   <li>Works seamlessly with Portal's resource bundle system</li>
 *   <li>Supports JSF's locale management</li>
 *   <li>Integrates with CDI's event system for change notifications</li>
 * </ul>
 * 
 * <h2>Thread Safety</h2>
 * Components in this package are designed to be thread-safe and suitable for
 * use in concurrent web applications.
 * 
 * @author Oliver Wolff
 * @see java.util.Locale
 * @see jakarta.faces.context.FacesContext#getViewRoot()#getLocale()
 */
package de.cuioss.portal.common.locale;
