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
 * Provides project stage management for Portal applications.
 *
 * <h2>Overview</h2>
 * This package contains components for managing and determining the current
 * project stage (Development, Production, etc.) of a Portal application.
 * It enables stage-specific behavior and configuration across the application.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.common.stage.ProjectStage} - Enumeration defining
 *       available project stages and providing stage detection logic</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 * <pre>
 *
 * // Check for specific stage
 * if (stage.isDevelopment()) {
 *     // Enable development-specific features
 * }
 *
 * // Configure based on stage
 * switch (stage) {
 *     case DEVELOPMENT:
 *         enableDetailedLogging();
 *         break;
 *     case PRODUCTION:
 *         enableCaching();
 *         break;
 * }
 * </pre>
 *
 * <h2>Thread Safety</h2>
 * All components in this package are thread-safe. The project stage is determined
 * at application startup and remains constant throughout the application lifecycle.
 *
 * @author Oliver Wolff
 * @see jakarta.faces.application.ProjectStage
 */
package de.cuioss.portal.common.stage;
