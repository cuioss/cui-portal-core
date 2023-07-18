package de.cuioss.portal.configuration.yaml;

import de.cuioss.portal.configuration.FileConfigurationSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class TestResourcesRepository implements FileConfigurationSource {

    static final FileConfigurationSource EXISTING_PROPERTIES = new TestResourcesRepository(
            "classpath:/META-INF/test.properties", true);

    static final FileConfigurationSource NOT_EXISTING_PROPERTIES = new TestResourcesRepository(
            "classpath:/META-INF/not-there.properties", false);

    static final FileConfigurationSource EXISTING_YML = new TestResourcesRepository("classpath:test.yml", true);

    static final FileConfigurationSource NOT_EXISTING_YML = new TestResourcesRepository("classpath:not-there.yml",
            false);

    static final FileConfigurationSource EMPTY_PATH = new TestResourcesRepository("", false);

    @Getter
    private final String path;

    @Getter
    private final boolean readable;
}
