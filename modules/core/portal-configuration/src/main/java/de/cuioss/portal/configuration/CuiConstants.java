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
package de.cuioss.portal.configuration;

import lombok.experimental.UtilityClass;

/**
 * Provides common constants used throughout the Portal application.
 * These constants represent fixed values that are unlikely to change
 * and are used across multiple components.
 * 
 * <h2>Usage Areas</h2>
 * <ul>
 *   <li>Resource path prefixes</li>
 *   <li>Framework-specific constants</li>
 *   <li>Common path elements</li>
 * </ul>
 * 
 * <h2>Best Practices</h2>
 * <ul>
 *   <li>Use these constants instead of hardcoding values</li>
 *   <li>Only add constants that are truly application-wide</li>
 *   <li>Document the purpose and context of each constant</li>
 * </ul>
 *
 * @author Oliver Wolff
 *
 */
@UtilityClass
public class CuiConstants {

    /**
     * The standard prefix for JSF resource mapping in the servlet context.
     * This prefix is used by the JSF framework to identify and serve resources
     * like JavaScript files, CSS files, and images through the FacesServlet.
     * 
     * <h3>Example Usage</h3>
     * <pre>
     * // Constructing a JSF resource URL
     * String resourceUrl = FACES_RESOURCES_PREFIX + "styles/main.css";
     * // Result: "/jakarta.faces.resource/styles/main.css"
     * </pre>
     */
    public static final String FACES_RESOURCES_PREFIX = "/jakarta.faces.resource/";
}
