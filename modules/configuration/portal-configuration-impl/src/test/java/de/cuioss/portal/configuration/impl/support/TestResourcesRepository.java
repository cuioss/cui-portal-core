package de.cuioss.portal.configuration.impl.support;

import de.cuioss.portal.configuration.FileConfigurationSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("javadoc")
@RequiredArgsConstructor
public class TestResourcesRepository implements FileConfigurationSource {

    public static final FileConfigurationSource EXISTING_PROPERTIES = new TestResourcesRepository(
            "classpath:/META-INF/test.properties", true);

    public static final FileConfigurationSource NOT_EXISTING_PROPERTIES = new TestResourcesRepository(
            "classpath:/META-INF/not-there.properties", false);

    public static final FileConfigurationSource EXISTING_YML = new TestResourcesRepository("classpath:test.yml", true);

    public static final FileConfigurationSource NOT_EXISTING_YML = new TestResourcesRepository(
            "classpath:not-there.yml", false);

    public static final FileConfigurationSource EMPTY_PATH = new TestResourcesRepository("", false);

    @Getter
    private final String path;

    @Getter
    private final boolean readable;
}
