package de.cuioss.portal.configuration.yaml;

import static de.cuioss.tools.base.Preconditions.checkArgument;
import static de.cuioss.tools.io.FileLoaderUtility.getLoaderForPath;
import static de.cuioss.tools.string.MoreStrings.isEmpty;

import java.util.Map;

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.source.AbstractPortalConfigSource;
import de.cuioss.tools.collect.CollectionLiterals;
import de.cuioss.tools.io.FileTypePrefix;
import de.cuioss.tools.logging.CuiLogger;
import lombok.Getter;

/**
 * Loads and provides YAML-based configuration under {@link #getProperties()}.
 * The file path must be provided via {@link #getPath()}.
 *
 * @author Sven Haag
 */
public class YamlConfigSource extends AbstractPortalConfigSource implements FileConfigurationSource {

    private static final CuiLogger log = new CuiLogger(YamlConfigSource.class);

    @Getter
    protected String path;

    @Getter
    protected final Map<String, String> properties;

    @Getter
    private final boolean readable;

    /**
     * Instantly loads the given file. The path might be prefixed with one of the {@link FileTypePrefix}s.
     *
     * @param path to be loaded
     *
     * @throws IllegalArgumentException if {@code path} is {@code null} or empty.
     */
    public YamlConfigSource(final String path) {
        this(path, false);
    }

    /**
     * Instantly loads the given file. The path might be prefixed with one of the {@link FileTypePrefix}s.
     *
     * @param path to be loaded
     * @param optional if true, doesn't throw an exception if the file isn't available.
     *
     * @throws IllegalArgumentException if {@code path} is {@code null} or empty.
     */
    public YamlConfigSource(final String path, final boolean optional) {
        checkArgument(!isEmpty(path), "path must not be null nor empty");
        this.path = path;

        final var fileLoader = getLoaderForPath(getPath());
        if (!optional || fileLoader.isReadable()) {
            this.properties = new YamlConfigurationProvider(fileLoader).getConfigurationMap();
            this.readable = true;
        } else {
            this.properties = CollectionLiterals.immutableMap();
            this.readable = false;
        }
        log.trace("Loaded data for {}: {}", getPath(), getProperties());
    }
}
