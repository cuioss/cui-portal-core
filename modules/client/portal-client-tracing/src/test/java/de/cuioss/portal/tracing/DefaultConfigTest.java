package de.cuioss.portal.tracing;

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_REPORTER_URL;
import static de.cuioss.tools.collect.CollectionLiterals.immutableList;

import java.util.List;

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.TracingConfigKeys;
import de.cuioss.portal.core.test.tests.configuration.AbstractConfigurationKeyVerifierTest;
import de.cuioss.portal.core.test.tests.configuration.PropertiesDefaultConfigSource;
import lombok.Getter;

class DefaultConfigTest extends AbstractConfigurationKeyVerifierTest {

    @Getter
    private final FileConfigurationSource underTest = new PropertiesDefaultConfigSource();

    @Override
    public Class<?> getKeyHolder() {
        return TracingConfigKeys.class;
    }

    @Override
    public List<String> getKeysIgnoreList() {
        return immutableList(PORTAL_TRACING_REPORTER_URL);
    }
}
