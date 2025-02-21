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
package de.cuioss.portal.restclient;

import de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Qualifier for injecting configured REST clients in the Portal environment.
 * Used in conjunction with {@link ConfigAsConnectionMetadata} to provide
 * connection details for the client.
 *
 * <p>Example usage:
 * <pre>
 * &#64;Inject
 * &#64;PortalRestClient(configPath = "my.service")
 * private MyServiceClient client;
 * </pre>
 *
 * <p>The configuration path must point to a valid connection metadata configuration
 * that provides the necessary connection details like URL, authentication, etc.
 *
 * @see ConfigAsConnectionMetadata
 * @see CuiRestClientBuilder
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface PortalRestClient {

    /**
     * @return the basename of the configuration, see
     * {@link ConfigAsConnectionMetadata} for details
     */
    @Nonbinding
    String baseName();

    /**
     * @return boolean indicating whether the corresponding producer should throw an
     * {@link IllegalArgumentException} in case the properties contain
     * errors.
     * Defaults to <code>true</code>.
     * In case of <code>false</code>
     * will return the created {@link RestClientHolder} without structural
     * checks on the configuration
     */
    @Nonbinding
    boolean failOnInvalidConfiguration() default true;
}
