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

import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier that identifies beans implementing {@link ApplicationInitializer}.
 * This qualifier enables the Portal's initialization framework to discover and
 * manage beans requiring ordered initialization.
 * 
 * <h2>Usage</h2>
 * Apply this qualifier to {@link jakarta.enterprise.context.ApplicationScoped} beans
 * that implement {@link ApplicationInitializer}:
 * 
 * <pre>
 * &#64;ApplicationScoped
 * &#64;PortalInitializer
 * public class DatabaseInitializer implements ApplicationInitializer {
 *     &#64;Override
 *     public void initialize() {
 *         // Initialization logic
 *     }
 *     
 *     &#64;Override
 *     public Integer getOrder() {
 *         return ORDER_EARLY;  // Initialize early
 *     }
 * }
 * </pre>
 * 
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Only use with {@link jakarta.enterprise.context.ApplicationScoped} beans</li>
 *   <li>Must be combined with {@link ApplicationInitializer} implementation</li>
 *   <li>Enables automatic discovery by the initialization framework</li>
 *   <li>Supports injection points for framework components</li>
 * </ul>
 * 
 * @author Oliver Wolff
 * @see ApplicationInitializer
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface PortalInitializer {
}
