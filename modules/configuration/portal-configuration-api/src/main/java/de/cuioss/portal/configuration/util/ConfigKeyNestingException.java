package de.cuioss.portal.configuration.util;

/**
 * Exception for a configuration key whose default value contains a placeholder again for too many times.
 */
public final class ConfigKeyNestingException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    public ConfigKeyNestingException(String configKey) {
        super("Config key is nested too deep: " + configKey);
    }

    public ConfigKeyNestingException(String configKey, Throwable cause) {
        super("Config key is nested too deep: " + configKey, cause);
    }
}
