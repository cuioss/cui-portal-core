package de.cuioss.portal.configuration;

/**
 * Interface to provide a path to a configuration file.
 *
 * @author Sven Haag
 */
public interface FileConfigurationSource extends Reloadable {

    /**
     * The absolute or relative path to a configuration file. It can be prefixed
     * with a value of {@link de.cuioss.tools.io.FileTypePrefix}. The provided file
     * must be a YAML <code>(.yml|.yaml)</code> or Properties
     * <code>(.properties)</code> file.
     *
     * @return path to the configuration file
     */
    String getPath();

    boolean isReadable();
}
