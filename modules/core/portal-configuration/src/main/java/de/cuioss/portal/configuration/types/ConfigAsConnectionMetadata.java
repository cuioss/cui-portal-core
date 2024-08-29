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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;

/**
 * <p>
 * Injects a number of related config-properties as a
 * {@link ConnectionMetadata}. In case the required elements are not there /
 * empty the producer will throw an {@link IllegalArgumentException}, see
 * {@link #failOnInvalidConfiguration()}.
 * <p>
 * 'baseName' is the string that is equal for all other derived keys, e.g. for
 * the value-set-client: 'value-set-client.connection.url':
 * 'value-set-client.connection.' is the 'baseName' for configuring the
 * connection of the value-set-client.
 * </p>
 *
 * The expected structure is
 * <h3>Basic Information</h3>
 * <ul>
 * <li><code>baseName.id</code>: Optional, id for that connection, usually used
 * for URL parameter or similar. If it is not set the baseName will be used</li>
 * <li><code>baseName.description</code>: Optional, description for that
 * connection.</li>
 * <li><code>baseName.url</code>: Required, the URL of the service to connect
 * to.</li>
 * <li><code>baseName.type</code>: Optional, must be one of "JMX",
 * "REST","DATABASE" "UNDEFINED" (case insensitive). If not set it defaults to
 * "UNDEFINED"</li>
 * </ul>
 *
 * <h3>Authentication</h3>
 * <ul>
 * <li><code>baseName.authentication</code>: Must be one of "none", "basic",
 * "certificate", "token.application" or "token.user" (case insensitive).
 * Defaults to "none"</li>
 * </ul>
 * <p>
 * In case of authentication type "certificate" a key-store can be configured
 * for this connection: If it is not configured the key-store configured for the
 * system will be used
 * </p>
 * <ul>
 * <li><code>baseName.authentication.certificate.keystore.location</code></li>
 * <li><code>baseName.authentication.certificate.keystore.password</code></li>
 * <li><code>baseName.authentication.certificate.keystore.keypassword</code> (To
 * be used for cases where the actual certificate has a different password
 * compared to the {@code keystore.password})</li>
 * </ul>
 * <p>
 * In case of authentication type "basic" the following properties must be
 * defined:
 * </p>
 * <ul>
 * <li><code>baseName.authentication.basic.username</code></li>
 * <li><code>baseName.authentication.basic.password</code></li>
 * </ul>
 * <p>
 * In case of authentication type "token.application" the following properties
 * must be defined:
 * </p>
 * <ul>
 * <li><code>baseName.authentication.token.application.token</code>: Identifying
 * the actual token to be used. Usually a client token</li>
 * <li><code>baseName.authentication.token.application.key</code>: Identifying
 * the name of the header under which the token is added to the request, e.g.
 * "X-Vault-Token"</li>
 * </ul>
 * <p>
 * <em>Caution: The approach with "token.user" s work in progress / experimental
 * </em> In case of authentication type "token.user" the following properties
 * must be defined:
 * </p>
 * <ul>
 * <li><code>baseName.authentication.token.user.token.source</code>: Identifies
 * the Information from the AuthenticatedUserInfo that is used as source for the
 * token</li>
 * <li><code>baseName.authentication.token.user.key</code>: Identifying the name
 * of the header under which the token is added to the request, e.g.
 * "X-Vault-Token"</li>
 * </ul>
 * <p>
 * In case your connection uses a secured transport like https you can
 * optionally configure a specific keystore / truststore for this connection. If
 * not the corresponding system settings will be used
 * </p>
 * <ul>
 * <li><code>baseName.transport.secure.keystore.location</code></li>
 * <li><code>baseName.transport.secure.keystore.password</code></li>
 * <li><code>baseName.transport.secure.keystore.keypassword</code> (To be used
 * for cases where the actual certificate has a different password compared to
 * the {@code keystore.password})</li>
 * <li><code>baseName.transport.secure.truststore.location</code></li>
 * <li><code>baseName.transport.secure.truststore.password</code></li>
 * <li><code>baseName.transport.secure.disableHostNameVerification</code>: If
 * set to {@code true} the hostname will not be validated for this connection.
 * <em>Caution:</em> Setting this value will compromise security: Only to be
 * used for testing!!</li>
 * </ul>
 * <h3>Additional Parameter</h3>
 * <p>
 * In addition to the standard configuration you can configure connection
 * specific parameter. The base-name therefore is <code>baseName.config</code>.
 * </p>
 * <ul>
 * <li>{@code baseName.config.client}:okclient Enables the okHttpClient for
 * connections of type REST. This client is more efficient and reliable within
 * complex network scenarios and provides a built-in caching: Http code 304 will
 * always handled correctly. In addition the caching can be configured in many
 * aspects, see other parameter.</li>
 * <li>{@code baseName.config.max-age}: Sets the maximum age of a cached
 * response in minutes. If the cache response's age exceeds maxAge, it will not
 * be used and a network request will be made.</li>
 * <li>{@code baseName.config.max-stale}: Accept cached responses in minutes
 * that have exceeded their freshness lifetime by up to maxStale. If
 * unspecified, stale cache responses will not be used.</li>
 * <li>{@code baseName.config.min-fresh}: Sets the minimum number of minutes
 * that a response will continue to be fresh for. If the response will be stale
 * when minFresh have elapsed, the cached response will not be used and a
 * network request will be made.</li>
 * </ul>
 *
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface ConfigAsConnectionMetadata {

    /**
     * @return the name of the base property. Can end with a dot or not.
     */
    @Nonbinding
    String baseName();

    /**
     * @return boolean indicating whether the corresponding producer should throw an
     *         {@link IllegalArgumentException} in case the properties contain
     *         errors. Defaults to <code>true</code>. In case of <code>false</code>
     *         will return the created {@link ConnectionMetadata} without calling
     *         {@link ConnectionMetadata#validate()}
     */
    @Nonbinding
    boolean failOnInvalidConfiguration() default true;
}
