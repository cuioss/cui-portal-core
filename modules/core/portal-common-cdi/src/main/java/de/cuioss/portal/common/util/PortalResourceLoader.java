/**
 *
 */
package de.cuioss.portal.common.util;

import static de.cuioss.tools.collect.MoreCollections.requireNotEmpty;
import static java.util.Objects.requireNonNull;

import de.cuioss.portal.common.PortalCommonCDILogMessages;
import de.cuioss.tools.logging.CuiLogger;
import lombok.experimental.UtilityClass;

import java.net.URL;
import java.util.Optional;

/**
 * Helper class that streamlines loading Resources from the classpath. This is
 * needed because Quarkus modules behave differently.
 */
@UtilityClass
public class PortalResourceLoader {

    private static final CuiLogger LOGGER = new CuiLogger(PortalResourceLoader.class);

    /**
     * Loads a certain resource from the classpath.
     *
     * @param resourcePath identifying the concrete resource, must not be null
     * @param callingClass The class to use as context for resource loading, must not be null
     * @return The {@link Optional} {@link URL} identifying the resource
     * @deprecated Use {@link #getResource(String, Class)} instead. This method will be removed in a future version.
     */
    @Deprecated(since = "2.0", forRemoval = true)
    public static Optional<URL> getRessource(String resourcePath, Class<?> callingClass) {
        return getResource(resourcePath, callingClass);
    }

    /**
     * Loads a certain resource from the classpath.
     *
     * @param resourcePath identifying the concrete resource, must not be null
     * @param callingClass The class to use as context for resource loading, must not be null
     * @return The {@link Optional} {@link URL} identifying the resource. Will be empty if the resource cannot be found.
     */
    public static Optional<URL> getResource(String resourcePath, Class<?> callingClass) {
        requireNotEmpty(resourcePath);
        requireNonNull(callingClass);
        var result = Optional.ofNullable(callingClass.getResource(resourcePath));
        if (result.isEmpty()) {
            LOGGER.debug(PortalCommonCDILogMessages.RESOURCE.DEBUG.LOADER_FALLBACK.format(
                    resourcePath, callingClass));
            result = Optional.ofNullable(Thread.currentThread().getContextClassLoader().getResource(resourcePath));
        }
        return result;
    }
}
