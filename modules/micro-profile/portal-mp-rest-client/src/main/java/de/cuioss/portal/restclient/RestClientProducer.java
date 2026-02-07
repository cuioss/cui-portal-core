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
package de.cuioss.portal.restclient;

import de.cuioss.portal.configuration.impl.producer.ConnectionMetadataProducer;
import de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.tools.base.Preconditions;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

import java.io.Closeable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static de.cuioss.portal.configuration.impl.producer.ConnectionMetadataProducer.MISSING_BASENAME_MSG;
import static de.cuioss.tools.collect.CollectionLiterals.mutableList;
import static de.cuioss.tools.lang.MoreObjects.requireType;
import static de.cuioss.tools.string.MoreStrings.requireNotEmpty;

/**
 * CDI producer for REST client instances in the Portal environment.
 * Creates and configures REST clients based on {@link PortalRestClient}
 * configuration.
 *
 * <p>Usage:
 * <pre>
 * &#64;Inject
 * &#64;PortalRestClient(configPath = "my.service")
 * private MyServiceClient client;
 * </pre>
 *
 * <p>The producer handles:
 * <ul>
 *   <li>Client creation via {@link CuiRestClientBuilder}</li>
 *   <li>Configuration lookup</li>
 *   <li>Authentication setup</li>
 *   <li>Lifecycle management</li>
 * </ul>
 *
 * @see PortalRestClient
 * @see CuiRestClientBuilder
 * @see ConfigAsConnectionMetadata
 */
@ApplicationScoped
public class RestClientProducer {

    private static final CuiLogger LOGGER = new CuiLogger(RestClientProducer.class);

    @Produces
    @Dependent
    @PortalRestClient(baseName = "unused")
    public <T extends Closeable> RestClientHolder<T> produceRestClient(final InjectionPoint injectionPoint) {
        LOGGER.debug("Producing REST client for injection point: %s", injectionPoint.getMember());

        final var annotationMetaData = ConfigurationHelper.resolveAnnotation(injectionPoint, PortalRestClient.class)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Expected injectionPoint annotated with @PortalRestClient, but was not:" + injectionPoint));

        var type = requireType(injectionPoint.getType(), ParameterizedType.class);
        List<Type> arguments = mutableList(type.getActualTypeArguments());
        Preconditions.checkArgument(!arguments.isEmpty());

        @SuppressWarnings("unchecked") var serviceInterface = (Class<T>) arguments.getFirst();

        // Basename must be present
        final var baseName = suffixNameWithDot(requireNotEmpty(annotationMetaData.baseName(), MISSING_BASENAME_MSG));

        LOGGER.debug("Producing RestClient for baseName ='%s'", baseName);

        final var failOnInvalidConfiguration = annotationMetaData.failOnInvalidConfiguration();
        try {
            var connectionMetadata = ConnectionMetadataProducer.createConnectionMetadata(baseName,
                    failOnInvalidConfiguration);
            return new RestClientHolder<>(new CuiRestClientBuilder(resolveCuiLogger(injectionPoint, serviceInterface))
                    .connectionMetadata(connectionMetadata).build(serviceInterface));
        } catch (IllegalArgumentException e) {
            LOGGER.error(e, RestClientLogMessages.ERROR.INITIALIZATION_FAILED);
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
                LOGGER.debug("Not using class %s as it seems to be a Weld CDI proxy", clazz.getName());
                return Optional.empty();
            }
            return Optional.of(clazz);
        }
        return Optional.empty();
    }

    private CuiLogger resolveCuiLogger(InjectionPoint ip, Class<?> fallback) {
        final var clazz = resolveCallerClass(ip).orElse(fallback);
        LOGGER.debug("Using logger class: %s", clazz.getName());
        return new CuiLogger(clazz);
    }
}
