package de.cuioss.portal.restclient;

import static de.cuioss.portal.configuration.impl.producer.ConnectionMetadataProducer.MISSING_BASENAME_MSG;
import static de.cuioss.tools.collect.CollectionLiterals.mutableList;
import static de.cuioss.tools.lang.MoreObjects.requireType;
import static de.cuioss.tools.string.MoreStrings.requireNotEmpty;

import java.io.Closeable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import de.cuioss.portal.configuration.impl.producer.ConnectionMetadataProducer;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.tools.base.Preconditions;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Produces a {@link RestClientHolder} to the given service interface.
 */
@ApplicationScoped
public class RestClientProducer {

    private static final CuiLogger log = new CuiLogger(RestClientProducer.class);

    @Produces
    @Dependent
    @PortalRestClient(baseName = "unused")
    <T extends Closeable> RestClientHolder<T> produceRestClient(final InjectionPoint injectionPoint) {
        final var annotationMetaData = ConfigurationHelper.resolveAnnotation(injectionPoint, PortalRestClient.class)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Expected injectionPoint annotated with @PortalRestClient, but was not:" + injectionPoint));

        var type = requireType(injectionPoint.getType(), ParameterizedType.class);
        List<Type> arguments = mutableList(type.getActualTypeArguments());
        Preconditions.checkArgument(!arguments.isEmpty());

        @SuppressWarnings("unchecked")
        var serviceInterface = (Class<T>) arguments.get(0);

        // Basename must be present
        final var baseName = suffixNameWithDot(requireNotEmpty(annotationMetaData.baseName(), MISSING_BASENAME_MSG));

        log.debug("Producing DsmlClient for baseName ='{}'", baseName);

        final var failOnInvalidConfiguration = annotationMetaData.failOnInvalidConfiguration();
        try {
            var connectionMetadata = ConnectionMetadataProducer.createConnectionMetadata(baseName,
                    failOnInvalidConfiguration);
            return new RestClientHolder<>(new CuiRestClientBuilder(resolveCuiLogger(injectionPoint, serviceInterface))
                    .connectionMetadata(connectionMetadata).build(serviceInterface));
        } catch (IllegalArgumentException e) {
            log.error("Initialization of RestClientHolder failed", e);
            return new RestClientHolder<>(null);
        }
    }

    /**
     * @param name to be suffixed, must not be null
     * @return the given name suffixed with a dot
     */
    public static String suffixNameWithDot(final String name) {
        return name.endsWith(".") ? name : name + ".";
    }

    private Optional<Class<?>> resolveCallerClass(InjectionPoint ip) {
        if (null != ip && null != ip.getMember() && null != ip.getMember().getDeclaringClass()) {

            // works only due to @Dependent scope injection point!
            final Class<?> clazz = ip.getMember().getDeclaringClass();
            if (clazz.getName().contains("$Proxy$")) { // checking for the sake of a peaceful mind
                log.debug("not using class {} as it seems to be a Weld CDI proxy", clazz.getName());
                return Optional.empty();
            }
            return Optional.of(clazz);
        }
        return Optional.empty();
    }

    private CuiLogger resolveCuiLogger(InjectionPoint ip, Class<?> fallback) {
        final var clazz = resolveCallerClass(ip).orElse(fallback);
        log.debug("Using logger class: {}", clazz.getName());
        return new CuiLogger(clazz);
    }
}
