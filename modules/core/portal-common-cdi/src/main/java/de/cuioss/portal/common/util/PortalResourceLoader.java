/**
 *
 */
package de.cuioss.portal.common.util;

import de.cuioss.tools.logging.CuiLogger;
import lombok.experimental.UtilityClass;

import java.net.URL;
import java.util.Optional;

import static de.cuioss.tools.collect.MoreCollections.requireNotEmpty;
import static java.util.Objects.requireNonNull;

/**
 * Helper class that streamlines loading Resources from the classpath. This is
 * needed because Quarkus modules behave differently.
 * 
 * <h2>Usage</h2>
 * <pre>
 * // Load a resource using a specific class context
 * Optional&lt;URL&gt; resource = PortalResourceLoader.getResource("/path/to/resource.txt", MyClass.class);
 * 
 * // Process the resource if found
 * resource.ifPresent(url -> {
 *     // Process the resource
 * });
 * </pre>
 */
@UtilityClass
public class PortalResourceLoader {

    private static final CuiLogger LOGGER = new CuiLogger(PortalResourceLoader.class);

    /**
     * Loads a certain resource from the classpath.
     *
     * @param resourcePath identifying the concrete resource, must not be null
     * @param callingClass The class to use as context for resource loading, must not be null
     * @param <T> the type of the calling class
     * @return The {@link Optional} {@link URL} identifying the resource
     * @deprecated Use {@link #getResource(String, Class)} instead. This method will be removed in a future version.
     */
    @Deprecated(since = "1.2", forRemoval = true)
    public static <T> Optional<URL> getRessource(String resourcePath, Class<T> callingClass) {
        return getResource(resourcePath, callingClass);
    }

    /**
     * Loads a certain resource from the classpath.
     *
     * @param resourcePath identifying the concrete resource, must not be null
     * @param callingClass The class to use as context for resource loading, must not be null
     * @param <T> the type of the calling class
     * @return The {@link Optional} {@link URL} identifying the resource. Will be empty if the resource cannot be found.
     * @throws IllegalArgumentException if resourcePath is empty
     * @throws NullPointerException if resourcePath or callingClass is null
     */
    public static <T> Optional<URL> getResource(String resourcePath, Class<T> callingClass) {
        requireNotEmpty(resourcePath);
        requireNonNull(callingClass);
        var result = Optional.ofNullable(callingClass.getResource(resourcePath));
        if (result.isEmpty()) {
            LOGGER.debug("Resource '%s' not found using class '%s', falling back to context class loader",
                    resourcePath, callingClass.getName());
            result = Optional.ofNullable(Thread.currentThread().getContextClassLoader().getResource(resourcePath));
        }
        return result;
    }
}
