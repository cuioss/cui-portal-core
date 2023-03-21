package de.cuioss.portal.core.test.tests.configuration;

import de.cuioss.portal.configuration.FileConfigurationSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class MockConfigurationSource implements FileConfigurationSource {

    @Getter
    private final String path;

    @Override
    public boolean isReadable() {
        return true;
    }
}
