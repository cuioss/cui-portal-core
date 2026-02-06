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
 * Provides exception classes for handling connection-related errors in the Portal
 * configuration system. This package defines a hierarchy of exceptions:
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.connections.exception.ConnectionException} -
 *       Base exception for all connection-related errors</li>
 *   <li>{@link de.cuioss.portal.configuration.connections.exception.ConnectionConfigurationException} -
 *       Specific to configuration validation failures</li>
 * </ul>
 * <p>
 * The exceptions work in conjunction with {@link de.cuioss.portal.configuration.connections.exception.ErrorReason}
 * to provide structured error information that can be:
 * <ul>
 *   <li>Logged consistently with appropriate severity</li>
 *   <li>Translated into user-friendly messages</li>
 *   <li>Used for automated error handling and recovery</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
package de.cuioss.portal.configuration.connections.exception;
