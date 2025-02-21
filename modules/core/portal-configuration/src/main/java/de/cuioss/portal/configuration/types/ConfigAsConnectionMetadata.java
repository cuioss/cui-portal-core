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
package de.cuioss.portal.configuration.types;

import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier for injecting connection configuration as {@link ConnectionMetadata} instances.
 * This qualifier provides comprehensive configuration for various connection types
 * including authentication, caching, and connection properties.
 * <p>
 * Features:
 * <ul>
 *   <li>Multiple authentication methods</li>
 *   <li>Certificate management</li>
 *   <li>Caching configuration</li>
 *   <li>Connection type support</li>
 *   <li>Flexible configuration structure</li>
 * </ul>
 * <p>
 * <h2>Configuration Structure</h2>
 * All properties use a common base name prefix. For example, with base name
 * "app.service.connection", properties would be:
 * <p>
 * <h3>1. Basic Properties</h3>
 * <ul>
 *   <li>{@code baseName.id} - Optional identifier (defaults to baseName)</li>
 *   <li>{@code baseName.description} - Optional connection description</li>
 *   <li>{@code baseName.url} - Required service URL</li>
 *   <li>{@code baseName.type} - Optional connection type:
 *     <ul>
 *       <li>JMX</li>
 *       <li>REST</li>
 *       <li>DATABASE</li>
 *       <li>UNDEFINED (default)</li>
 *     </ul>
 *   </li>
 * </ul>
 * <p>
 * <h3>2. Authentication Configuration</h3>
 * Set via {@code baseName.authentication}:
 * <p>
 * a) Certificate Authentication:
 * <pre>
 * baseName.authentication=certificate
 * baseName.authentication.certificate.keystore.location=path/to/keystore
 * baseName.authentication.certificate.keystore.password=store-password
 * baseName.authentication.certificate.keystore.keypassword=key-password
 * </pre>
 * <p>
 * b) Basic Authentication:
 * <pre>
 * baseName.authentication=basic
 * baseName.authentication.basic.username=user
 * baseName.authentication.basic.password=pass
 * </pre>
 * <p>
 * c) Application Token:
 * <pre>
 * baseName.authentication=token.application
 * baseName.authentication.token.application.token=your-token
 * baseName.authentication.token.application.key=X-Auth-Token
 * </pre>
 * <p>
 * d) User Token (Experimental):
 * <pre>
 * baseName.authentication=token.user
 * baseName.authentication.token.user.token.source=token-source
 * baseName.authentication.token.user.key=token-header
 * </pre>
 * <p>
 * <h3>3. Cache Configuration</h3>
 * Optional caching settings:
 * <pre>
 * baseName.config.cache=true
 * baseName.config.max-age=60     # max age in minutes
 * baseName.config.max-stale=30   # stale tolerance in minutes
 * </pre>
 * <p>
 * Usage example:
 * <pre>
 * &#64;Inject
 * &#64;ConfigAsConnectionMetadata(
 *     baseName = "app.auth.service",
 *     failOnInvalidConfiguration = true
 * )
 * private ConnectionMetadata authService;
 * </pre>
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ConfigAsConnectionMetadata {

    /**
     * The base name prefix for all connection configuration properties.
     * <p>
     * This prefix will be used to locate all related configuration properties
     * such as URL, authentication settings, and cache configuration.
     * <p>
     * For example, if baseName is "app.service":
     * <ul>
     *   <li>URL will be read from "app.service.url"</li>
     *   <li>Auth type from "app.service.authentication"</li>
     *   <li>Cache settings from "app.service.config.cache"</li>
     * </ul>
     *
     * @return the base configuration prefix
     */
    @Nonbinding
    String baseName();

    /**
     * Controls validation behavior for missing or invalid configuration.
     * <p>
     * When true:
     * <ul>
     *   <li>Validates all required properties are present</li>
     *   <li>Checks property value formats</li>
     *   <li>Throws {@link IllegalArgumentException} on validation failure</li>
     * </ul>
     * When false:
     * <ul>
     *   <li>Allows missing optional properties</li>
     *   <li>Uses default values where possible</li>
     *   <li>May result in incomplete configuration</li>
     * </ul>
     *
     * @return {@code true} to enable strict validation,
     *         {@code false} to allow partial configuration
     */
    @Nonbinding
    boolean failOnInvalidConfiguration() default true;
}
