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
     * Loads a certain resource
     *
     * @param resourcePath identifying the concrete resource, must nor be null
     * @param callingClass must not be null
     * @return The {@link Optional} {@link URL} identifying the resource
     */
    public static Optional<URL> getRessource(String resourcePath, Class<?> callingClass) {
        requireNotEmpty(resourcePath);
        requireNonNull(callingClass);
        var result = Optional.ofNullable(callingClass.getResource(resourcePath));
        if (result.isEmpty()) {
            LOGGER.debug(PortalCommonCDILogMessages.RESOURCE_LOADER_FALLBACK.format(
                    resourcePath, callingClass));
            result = Optional.ofNullable(Thread.currentThread().getContextClassLoader().getResource(resourcePath));
        }
        return result;
    }
}
