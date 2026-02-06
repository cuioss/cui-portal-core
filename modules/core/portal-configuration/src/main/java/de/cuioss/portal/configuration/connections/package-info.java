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
 * Provides the core framework for configuring and managing server-to-server connections
 * in the Portal environment. This package includes:
 * <ul>
 *   <li>Connection configuration and metadata management</li>
 *   <li>Authentication strategies (Basic, Certificate, Token)</li>
 *   <li>Transport security configuration (SSL/TLS)</li>
 *   <li>Connection type definitions (REST, JMX, etc.)</li>
 * </ul>
 * <p>
 * Key components:
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.connections.impl.ConnectionMetadata} - Core connection configuration</li>
 *   <li>{@link de.cuioss.portal.configuration.connections.TokenResolver} - Token-based authentication</li>
 *   <li>{@link de.cuioss.portal.configuration.connections.impl.ConnectionType} - Supported connection types</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
package de.cuioss.portal.configuration.connections;
