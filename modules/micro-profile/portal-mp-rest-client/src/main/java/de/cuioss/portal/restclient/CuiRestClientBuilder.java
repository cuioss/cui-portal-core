package de.cuioss.portal.restclient;

import static de.cuioss.portal.tracing.PortalTracing.getServiceName;
import static de.cuioss.tools.string.MoreStrings.nullToEmpty;

import java.io.Closeable;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import brave.http.HttpTracing;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.portal.tracing.PortalTracing;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.MoreStrings;

/**
 * Builder for a JAVA MicroProfile based REST client.
 *
 * To enable log debugging / tracing set package de.cuioss.portal.core.restclient to TRACE level in your logger
 * configuration.
 */
public class CuiRestClientBuilder {

    private static final CuiLogger log = new CuiLogger(CuiRestClientBuilder.class);

    private static final String DISABLE_DEFAULT_MAPPER_PROPERTY_KEY = "microprofile.rest.client.disable.default.mapper";

    private final RestClientBuilder mpRestClientBuilder;
    private boolean traceLogEnabled;
    private boolean tracingEnabled;
    private ConnectionMetadata connectionMetadata;
    private String url;
    private final CuiLogger logger;

    /**
     * Creates a REST client builder.
     *
     * <p>Enables the trace-logging, if either the given logger or the {@link CuiRestClientBuilder} logger returns true
     * for {@link CuiLogger#isTraceEnabled()}.</p>
     *
     * @param logger for trace-logging.
     */
    public CuiRestClientBuilder(final CuiLogger logger) {
        this.mpRestClientBuilder = RestClientBuilder.newBuilder();
        this.logger = logger;
        this.traceLogEnabled = logger.isTraceEnabled() || log.isTraceEnabled();
        this.tracingEnabled = true;

        // Advice RestEasy not to add its default exception handler.
        // It would serve the request before we can trace-log anything.
        // Furthermore, it throws an Exception in case the service interfaces return type is javax.ws.rs.core.Response.
        // Both things we don't admire.
        // Also see: https://github.com/eclipse/microprofile-rest-client/issues/195
        disableDefaultExceptionHandler();
        // register(DefaultResponseExceptionMapper.class, Integer.MIN_VALUE - 1);
    }

    public static void debugResponse(final Response response, final CuiLogger log) {
        log.debug("-- Client response filter --\n" +
                "Status: {}\n" +
                "StatusInfo: {}\n" +
                "Allowed Methods: {}\n" +
                "EntityTag: {}\n" +
                "Cookies: {}\n" +
                "Date: {}\n" +
                "Headers: {}\n" +
                "Language: {}\n" +
                "LastModified: {}\n" +
                "Links: {}\n" +
                "Location: {}\n" +
                "MediaType: {}\n",
            response.getStatus(),
            response.getStatusInfo(),
            response.getAllowedMethods(),
            response.getEntityTag(),
            response.getCookies(),
            response.getDate(),
            response.getHeaders(),
            response.getLanguage(),
            response.getLastModified(),
            response.getLinks(),
            response.getLocation(),
            response.getMediaType());
    }

    public CuiRestClientBuilder url(final String url) {
        try {
            mpRestClientBuilder.baseUrl(new URL(url));
            this.url = url;
        } catch (final MalformedURLException e) {
            throw new IllegalArgumentException("The URL '" + url + "' could not be parsed!", e);
        }
        return this;
    }

    public CuiRestClientBuilder url(final URL url) {
        mpRestClientBuilder.baseUrl(url);
        return this;
    }

    public CuiRestClientBuilder uri(final URI uri) {
        mpRestClientBuilder.baseUri(uri);
        return this;
    }

    public CuiRestClientBuilder basicAuth(final String username, final String password) {
        mpRestClientBuilder.register(new BasicAuthenticationFilter(username, password));
        return this;
    }

    public CuiRestClientBuilder bearerAuthToken(final String token) {
        mpRestClientBuilder.register(new BearerTokenAuthFilter(token));
        return this;
    }

    public CuiRestClientBuilder registerExceptionMapper(final ResponseExceptionMapper mapper) {
        mpRestClientBuilder.register(mapper);
        return this;
    }

    public CuiRestClientBuilder sslContext(final SSLContext sslContext) {
        mpRestClientBuilder.sslContext(sslContext);
        return this;
    }

    /**
     * Sets various properties based on the given <code>connectionMeta</code>.
     * <ul>
     *     <li>service url</li>
     *     <li>tracing enabled</li>
     *     <li>ssl context</li>
     *     <li>login credentials</li>
     *     <li>context map</li>
     *     <li>hostname verifier</li>
     *     <li>connection timeout</li>
     *     <li>read timeout</li>
     * </ul>
     *
     * @param connectionMeta
     *
     * @return this builder
     */
    @SuppressWarnings("squid:S3510") // owolff: False Positive, By design
    public CuiRestClientBuilder connectionMetadata(final ConnectionMetadata connectionMeta) {
        this.connectionMetadata = connectionMeta;
        url(connectionMeta.getServiceUrl());
        tracingEnabled(connectionMeta.isTracingEnabled());

        mpRestClientBuilder.sslContext(connectionMeta.resolveSSLContext());
        switch (connectionMeta.getAuthenticationType()) {
            case BASIC:
                basicAuth(connectionMeta.getLoginCredentials().getUsername(),
                        connectionMeta.getLoginCredentials().getPassword());
                break;
            case TOKEN_FROM_USER:
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
            mpRestClientBuilder.hostnameVerifier((hostname, sslSession) -> true);
        }
        if (connectionMeta.getConnectionTimeout() > 0) {
            mpRestClientBuilder.connectTimeout(connectionMeta.getConnectionTimeout(),
                    connectionMeta.getConnectionTimeoutUnit());
        }
        if (connectionMeta.getReadTimeout() > 0) {
            mpRestClientBuilder.readTimeout(connectionMeta.getReadTimeout(), connectionMeta.getReadTimeoutUnit());
        }
        if (!MoreStrings.isBlank(connectionMeta.getProxyHost()) && null != connectionMeta.getProxyPort()
                && connectionMeta.getProxyPort() > 0) {
            mpRestClientBuilder.proxyAddress(connectionMeta.getProxyHost(), connectionMeta.getProxyPort());
        }
        return this;
    }

    /**
     * @param value Enable|Disable trace logging capabilities for this REST client.
     *         Defaults to {@link CuiLogger#isTraceEnabled()} for the given logger.
     *         This is unrelated to the distributed tracing capabilities.
     *
     * @return this builder
     * @see LogClientRequestFilter
     * @see LogReaderInterceptor
     */
    public CuiRestClientBuilder traceLogEnabled(final boolean value) {
        traceLogEnabled = value;
        return this;
    }

    /**
     * @param value Enable|Disable the distributed tracing for this client. Only effective if
     *         {@link de.cuioss.portal.configuration.TracingConfigKeys#PORTAL_TRACING_ENABLED} is enabled.
     *         This could be overwritten by {@link #connectionMetadata(ConnectionMetadata)}.
     *
     * @return this builder
     */
    public CuiRestClientBuilder tracingEnabled(final boolean value) {
        tracingEnabled = value;
        return this;
    }

    /**
     * @param component to be registered
     *
     * @return this builder
     * @see Configurable#register(java.lang.Object)
     */
    public CuiRestClientBuilder register(final Object component) {
        mpRestClientBuilder.register(component);
        return this;
    }

    /**
     * @param component to be registered
     * @param priority overwrite value for the components {@link javax.annotation.Priority}
     *
     * @return this builder
     * @see Configurable#register(Object, int)
     */
    public CuiRestClientBuilder register(final Object component, final int priority) {
        mpRestClientBuilder.register(component, priority);
        return this;
    }

    /**
     * @param key property key to be registered
     * @param value property value to be registered
     *
     * @return this builder
     * @see Configurable#property(String, Object)
     */
    public CuiRestClientBuilder property(final String key, final Object value) {
        mpRestClientBuilder.property(key, value);
        return this;
    }

    /**
     * Enables the RestEasy default exception mapper for this MP REST client.
     * Per default, this exception mapper is disabled. It registers it with priority {@link Integer#MIN_VALUE},
     * instead of {@link Integer#MAX_VALUE}, to allow trace-logging of responses.
     *
     * Effect: Every response code of >=400 throws a general {@link javax.ws.rs.WebApplicationException}.
     *
     * @return this builder
     */
    public CuiRestClientBuilder enableDefaultExceptionHandler() {
        try {
            Class defaultResponseExceptionMapper = Class.forName(
                    "org.jboss.resteasy.microprofile.client.DefaultResponseExceptionMapper",
                    false, CuiRestClientBuilder.class.getClassLoader());
            register(defaultResponseExceptionMapper.newInstance(), Integer.MIN_VALUE);
            disableDefaultExceptionHandler();
        } catch (final Exception e) {
            log.error(
                    "Portal-541: Could not load org.jboss.resteasy.microprofile.client.DefaultResponseExceptionMapper",
                    e);
        }
        return this;
    }

    /**
     * Disables the RestEasy default exception mapper for this MP REST client.
     * Per default, this exception mapper is disabled.
     *
     * Effect: Exceptions like {@link javax.ws.rs.BadRequestException} are thrown instead of a general
     * {@link javax.ws.rs.WebApplicationException}.
     *
     * @return this builder
     */
    public CuiRestClientBuilder disableDefaultExceptionHandler() {
        property(DISABLE_DEFAULT_MAPPER_PROPERTY_KEY, true);
        return this;
    }

    public CuiRestClientBuilder connectTimeout(long amount, TimeUnit timeUnit) {
        mpRestClientBuilder.connectTimeout(amount, timeUnit);
        return this;
    }

    public CuiRestClientBuilder readTimeout(long amount, TimeUnit timeUnit) {
        mpRestClientBuilder.readTimeout(amount, timeUnit);
        return this;
    }

    public CuiRestClientBuilder queryParamStyle(QueryParamStyle queryParamStyle) {
        mpRestClientBuilder.queryParamStyle(queryParamStyle);
        return this;
    }

    public CuiRestClientBuilder proxyAddress(String host, int port) {
        mpRestClientBuilder.proxyAddress(host, port);
        return this;
    }

    public CuiRestClientBuilder followRedirects(boolean followRedirects) {
        mpRestClientBuilder.followRedirects(followRedirects);
        return this;
    }

    public CuiRestClientBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        mpRestClientBuilder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    public Configuration getConfiguration() {
        return mpRestClientBuilder.getConfiguration();
    }

    /**
     * Create an implementation of the service interface T using the rest client.
     *
     * @param clazz the service interface which also must extend {@link java.io.Closeable}
     * @param <T> the services type
     *
     * @return T the service class
     */
    public <T extends Closeable> T build(final Class<T> clazz) {
        if (traceLogEnabled) {
            log.debug("trace logging engaged");
            register(new LogClientRequestFilter(logger));
            register(new LogClientResponseFilter(logger, "First ClientResponseFilter") {

            }, Integer.MAX_VALUE);
            register(new LogClientResponseFilter(logger, "Last ClientResponseFilter") {

            }, Integer.MIN_VALUE);
            register(new LogReaderInterceptor(logger));
        }
        if (isTracingEnabled()) {
            registerTracingFilter();
        }

        return mpRestClientBuilder.build(clazz);
    }

    private void registerTracingFilter() {
        log.debug("Adding distributed tracing filter");
        final var serviceName = nullToEmpty(
                null != connectionMetadata
                        ? getServiceName(connectionMetadata)
                        : getServiceName(url));
        log.debug("Using serviceName: {}", serviceName);

        // HttpTracing is closed on response at: brave.jaxrs2.TracingClientFilter#filter
        @SuppressWarnings("squid:S2095") final var tracing =
                HttpTracing.create(PortalTracing.createTracing()).clientOf(serviceName);
        register(new TracingRequestFilter(tracing));
        register(new TracingResponseFilter(tracing));
    }

    private boolean isTracingEnabled() {
        return tracingEnabled && PortalTracing.isEnabled();
    }
}
