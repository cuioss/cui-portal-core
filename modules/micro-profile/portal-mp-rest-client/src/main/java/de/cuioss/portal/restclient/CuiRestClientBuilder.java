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

import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.MoreStrings;
import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of MicroProfile REST Client builder for the Portal environment.
 * Provides configuration and integration with Portal's authentication and
 * logging infrastructure.
 *
 * <p>Supported features:
 * <ul>
 *   <li>Connection configuration via {@link ConfigAsConnectionMetadata}</li>
 *   <li>Authentication (Basic, Bearer Token)</li>
 *   <li>Request/Response logging</li>
 *   <li>SSL configuration</li>
 * </ul>
 *
 * <p>This builder is typically used through {@link RestClientProducer}
 * and {@link PortalRestClient}. For direct usage, obtain an instance via
 * {@link RestClientBuilder#newBuilder()}.
 *
 * @see RestClientBuilder
 * @see PortalRestClient
 * @see RestClientProducer
 */
public class CuiRestClientBuilder {

    private static final CuiLogger LOGGER = new CuiLogger(CuiRestClientBuilder.class);

    private static final String DISABLE_DEFAULT_MAPPER_PROPERTY_KEY = "microprofile.rest.client.disable.default.mapper";
    public static final String RESPONSE_EXCEPTION_MAPPER = "org.jboss.resteasy.microprofile.client.DefaultResponseExceptionMapper";

    private final RestClientBuilder mpRestClientBuilder;
    private boolean traceLogEnabled;
    private final CuiLogger givenLogger;

    /**
     * Creates a REST client builder.
     *
     * <p>
     * Enables the trace-logging, if either the given logger or the
     * {@link CuiRestClientBuilder} logger returns true for
     * {@link CuiLogger#isTraceEnabled()}.
     * </p>
     *
     * @param givenLogger for trace-logging.
     */
    public CuiRestClientBuilder(final CuiLogger givenLogger) {
        mpRestClientBuilder = RestClientBuilder.newBuilder();
        this.givenLogger = givenLogger;
        traceLogEnabled = givenLogger.isTraceEnabled() || LOGGER.isTraceEnabled();

        // Advice RestEasy not to add its default exception handler.
        // It would serve the request before we can trace-log anything.
        // Furthermore, it throws an Exception in case the service interfaces return
        // type is
        // javax.ws.rs.core.Response.
        // Both things we don't admire.
        // Also see: https://github.com/eclipse/microprofile-rest-client/issues/195
        disableDefaultExceptionHandler();
        // register(DefaultResponseExceptionMapper.class, Integer.MIN_VALUE - 1);
    }

    /**
     * Debugs a given Response to the given logger
     *
     * @param response    must not be null
     * @param givenLogger must not be null
     */
    public static void debugResponse(final Response response, final CuiLogger givenLogger) {
        givenLogger.debug("""
                        -- Client response filter --
                        Status: %s
                        StatusInfo: %s
                        Allowed Methods: %s
                        EntityTag: %s
                        Cookies: %s
                        Date: %s
                        Headers: %s
                        Language: %s
                        LastModified: %s
                        Links: %s
                        Location: %s
                        MediaType: %s""",
                response.getStatus(), response.getStatusInfo(), response.getAllowedMethods(),
                response.getEntityTag(), response.getCookies(), response.getDate(), response.getHeaders(),
                response.getLanguage(), response.getLastModified(), response.getLinks(), response.getLocation(),
                response.getMediaType());
    }

    /**
     * Sets various properties based on the given <code>connectionMeta</code>.
     * <ul>
     * <li>service url</li>
     * <li>tracing enabled</li>
     * <li>ssl context</li>
     * <li>login credentials</li>
     * <li>context map</li>
     * <li>hostname verifier</li>
     * <li>connection timeout</li>
     * <li>read timeout</li>
     * </ul>
     *
     * @return this builder
     */
    @SuppressWarnings("squid:S3510") // owolff: False Positive, By design
    public CuiRestClientBuilder connectionMetadata(final ConnectionMetadata connectionMeta) {
        url(connectionMeta.getServiceUrl());

        sslContext(connectionMeta.resolveSSLContext());
        switch (connectionMeta.getAuthenticationType()) {
            case BASIC:
                basicAuth(connectionMeta.getLoginCredentials().getUsername(),
                        connectionMeta.getLoginCredentials().getPassword());
                break;
            case TOKEN_APPLICATION:
                mpRestClientBuilder.register(new TokenFilter(connectionMeta.getTokenResolver()));
                break;
            default:
                break;
        }
        for (final Map.Entry<Serializable, Serializable> entry : connectionMeta.getContextMap().entrySet()) {
            mpRestClientBuilder.property(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        if (connectionMeta.isDisableHostNameVerification()) {
            hostnameVerifier((hostname, sslSession) -> true); // NOSONAR:
            // owolff: This is documented to be only used in the context of testing
        }
        if (connectionMeta.getConnectionTimeout() > 0) {
            connectTimeout(connectionMeta.getConnectionTimeout(), connectionMeta.getConnectionTimeoutUnit());
        }
        if (connectionMeta.getReadTimeout() > 0) {
            readTimeout(connectionMeta.getReadTimeout(), connectionMeta.getReadTimeoutUnit());
        }
        if (!MoreStrings.isBlank(connectionMeta.getProxyHost()) && null != connectionMeta.getProxyPort()
                && connectionMeta.getProxyPort() > 0) {
            proxyAddress(connectionMeta.getProxyHost(), connectionMeta.getProxyPort());
        }
        return this;
    }

    /**
     * @param value Enable|Disable trace logging capabilities for this REST client.
     *              Defaults to {@link CuiLogger#isTraceEnabled()} for the given
     *              logger. This is unrelated to the distributed tracing
     *              capabilities.
     * @return this builder
     * @see LogClientRequestFilter
     * @see LogReaderInterceptor
     */
    public CuiRestClientBuilder traceLogEnabled(final boolean value) {
        traceLogEnabled = value;
        return this;
    }

    /**
     * @param component to be registered
     * @return this builder
     * @see Configurable#register(java.lang.Object)
     */
    public CuiRestClientBuilder register(final Object component) {
        mpRestClientBuilder.register(component);
        return this;
    }

    /**
     * @param component to be registered
     * @param priority  overwrite value for the components
     *                  {@link jakarta.annotation.Priority}
     * @return this builder
     * @see Configurable#register(Object, int)
     */
    public CuiRestClientBuilder register(final Object component, final int priority) {
        mpRestClientBuilder.register(component, priority);
        return this;
    }

    /**
     * @param key   property key to be registered
     * @param value property value to be registered
     * @return this builder
     * @see Configurable#property(String, Object)
     */
    public CuiRestClientBuilder property(final String key, final Object value) {
        mpRestClientBuilder.property(key, value);
        return this;
    }

    /**
     * Enables the RestEasy default exception mapper for this MP REST client. Per
     * default, this exception mapper is disabled. It registers it with priority
     * {@link Integer#MIN_VALUE}, instead of {@link Integer#MAX_VALUE}, to allow
     * trace-logging of responses.
     * <p>
     * Effect: Every response code of >=400 throws a general
     * {@link jakarta.ws.rs.WebApplicationException}.
     *
     * @return this builder
     */
    public CuiRestClientBuilder enableDefaultExceptionHandler() {
        try {
            Class<?> defaultResponseExceptionMapper = Class.forName(RESPONSE_EXCEPTION_MAPPER, false, CuiRestClientBuilder.class.getClassLoader());
            register(defaultResponseExceptionMapper.getDeclaredConstructor().newInstance(), Integer.MIN_VALUE);
            disableDefaultExceptionHandler();
        } catch (final Exception e) {
            LOGGER.error(e, RestClientLogMessages.ERROR.DEFAULT_HANDLER_LOAD_ERROR.format(RESPONSE_EXCEPTION_MAPPER));
        }
        return this;
    }

    /**
     * Disables the RestEasy default exception mapper for this MP REST client. Per
     * default, this exception mapper is disabled.
     * <p>
     * Effect: Exceptions like {@link jakarta.ws.rs.BadRequestException} are thrown
     * instead of a general {@link jakarta.ws.rs.WebApplicationException}.
     *
     * @return this builder
     */
    public CuiRestClientBuilder disableDefaultExceptionHandler() {
        property(DISABLE_DEFAULT_MAPPER_PROPERTY_KEY, true);
        return this;
    }

    /**
     * Adds the target url
     *
     * @param url to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder url(final String url) {
        try {
            mpRestClientBuilder.baseUrl(new URL(url));
        } catch (final MalformedURLException e) {
            throw new IllegalArgumentException("The URL '" + url + "' could not be parsed!", e);
        }
        return this;
    }

    /**
     * Adds the target url
     *
     * @param url to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder url(final URL url) {
        mpRestClientBuilder.baseUrl(url);
        return this;
    }

    /**
     * Adds the target uri
     *
     * @param uri to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder uri(final URI uri) {
        mpRestClientBuilder.baseUri(uri);
        return this;
    }

    /**
     * Adds the credentials for basic-auth
     *
     * @param username to be passed to he contained builder
     * @param password to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder basicAuth(final String username, final String password) {
        mpRestClientBuilder.register(new BasicAuthenticationFilter(username, password));
        return this;
    }

    /**
     * Adds the credentials for bearer-auth
     *
     * @param token to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder bearerAuthToken(final String token) {
        mpRestClientBuilder.register(new BearerTokenAuthFilter(token));
        return this;
    }

    /**
     * Adds the ResponseExceptionMapper
     *
     * @param mapper to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder registerExceptionMapper(final ResponseExceptionMapper<?> mapper) {
        mpRestClientBuilder.register(mapper);
        return this;
    }

    /**
     * Adds the sslContext
     *
     * @param sslContext to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder sslContext(final SSLContext sslContext) {
        mpRestClientBuilder.sslContext(sslContext);
        return this;
    }

    /**
     * Adds the connection timeout
     *
     * @param amount   to be passed to he contained builder
     * @param timeUnit to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder connectTimeout(long amount, TimeUnit timeUnit) {
        mpRestClientBuilder.connectTimeout(amount, timeUnit);
        return this;
    }

    /**
     * Adds the read timeout
     *
     * @param amount   to be passed to he contained builder
     * @param timeUnit to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder readTimeout(long amount, TimeUnit timeUnit) {
        mpRestClientBuilder.readTimeout(amount, timeUnit);
        return this;
    }

    /**
     * Adds the QueryParamStyle
     *
     * @param queryParamStyle to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder queryParamStyle(QueryParamStyle queryParamStyle) {
        mpRestClientBuilder.queryParamStyle(queryParamStyle);
        return this;
    }

    /**
     * Adds the proxy address
     *
     * @param host to be passed to he contained builder
     * @param port to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder proxyAddress(String host, int port) {
        mpRestClientBuilder.proxyAddress(host, port);
        return this;
    }

    /**
     * Adds the followRedirects
     *
     * @param followRedirects to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder followRedirects(boolean followRedirects) {
        mpRestClientBuilder.followRedirects(followRedirects);
        return this;
    }

    /**
     * Adds the hostnameVerifier
     *
     * @param hostnameVerifier to be passed to he contained builder
     * @return this builder
     */
    public CuiRestClientBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        mpRestClientBuilder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    /**
     * @return the current configuration of the contained builder
     */
    public Configuration getConfiguration() {
        return mpRestClientBuilder.getConfiguration();
    }

    /**
     * Create an implementation of the service interface T using the rest client.
     * If {@code traceLogEnabled} it implicitly registers a {@link LogClientRequestFilter} two {@link LogClientResponseFilter} and a {@link LogReaderInterceptor}
     *
     * @param clazz the service interface which also must extend
     *              {@link java.io.Closeable}
     * @param <T>   the service type
     * @return T the service class
     */
    public <T extends Closeable> T build(final Class<T> clazz) {
        LOGGER.debug("Building REST client for class: %s", clazz.getName());
        if (traceLogEnabled) {
            LOGGER.debug("Configuring trace-logging");
            register(new LogClientRequestFilter(givenLogger));

            // LogClientResponseFilter is an abstract class to allow multi-registering via anonymous class.
            register(new LogClientResponseFilter(givenLogger, "First ClientResponseFilter") {

            }, Integer.MAX_VALUE);
            register(new LogClientResponseFilter(givenLogger, "Last ClientResponseFilter") {

            }, Integer.MIN_VALUE);
            register(new LogReaderInterceptor(givenLogger));
        }

        return mpRestClientBuilder.build(clazz);
    }
}
