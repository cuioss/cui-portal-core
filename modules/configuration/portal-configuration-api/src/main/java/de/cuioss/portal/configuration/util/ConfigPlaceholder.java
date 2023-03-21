package de.cuioss.portal.configuration.util;

import static de.cuioss.portal.configuration.util.ConfigurationPlaceholderHelper.PLACEHOLDER_PATTERN;

import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * DTO representing a placeholder in our configuration system.
 */
@ToString
@EqualsAndHashCode
class ConfigPlaceholder {

    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";
    private static final String PLACEHOLDER_DEFAULT_VALUE_SPLITTER = ":";

    /**
     * the placeholders key
     */
    @Getter
    private final @NonNull String configKey;

    /**
     * the placeholders default value (optional)
     */
    private final String defaultValue;

    /**
     * Constructor for placeholders with a key only.
     * Sets {@link #defaultValue} to {@code null}.
     */
    public ConfigPlaceholder(@NonNull final String configKey) {
        this(configKey, null);
    }

    /**
     * Constructor for placeholders with a key and default value.
     */
    public ConfigPlaceholder(@NonNull final String configKey, final String defaultValue) {
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    /**
     * Parse the given string.
     * <p>
     * Spring Framework will use an empty string, if there is nothing behind the double colon, see:
     * https://github.com/spring-projects/spring-framework/blob/dad0b1e98cacf417cc5c68b5884e7a1e47b85000/spring-core/src/main/java/org/springframework/util/PropertyPlaceholderHelper.java#L156
     *
     * @param placeholder must not be null
     * @throws IllegalArgumentException if the given string is not a placeholder
     */
    public static ConfigPlaceholder split(@NonNull final String placeholder) {
        if (!isPlaceholder(placeholder)) {
            throw new IllegalArgumentException("Not a valid config placeholder: " + placeholder);
        }

        final var placeholderContent = placeholder.substring(
                PLACEHOLDER_PREFIX.length(),
                placeholder.length() - PLACEHOLDER_SUFFIX.length());

        if (placeholderContent.contains(PLACEHOLDER_DEFAULT_VALUE_SPLITTER)) {
            final var start = placeholderContent.indexOf(PLACEHOLDER_DEFAULT_VALUE_SPLITTER);
            return new ConfigPlaceholder(
                    placeholderContent.substring(0, start),
                    placeholderContent.substring(start + PLACEHOLDER_DEFAULT_VALUE_SPLITTER.length()));
        }
        return new ConfigPlaceholder(placeholderContent);
    }

    /**
     * @return true, if the given string represents a placeholder.
     *         false, otherwise, or the given string is null.
     */
    public static boolean isPlaceholder(final String placeholder) {
        if (null == placeholder) {
            return false;
        }

        return PLACEHOLDER_PATTERN.matcher(placeholder).matches();
    }
}
