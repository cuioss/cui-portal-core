package de.cuioss.portal.authentication.oauth;

import static de.cuioss.portal.configuration.OAuthConfigKeys.EXTERNAL_HOSTNAME;
import static de.cuioss.portal.configuration.OAuthConfigKeys.OPEN_ID_CLIENT_ID;
import static de.cuioss.portal.configuration.OAuthConfigKeys.OPEN_ID_CLIENT_LOGOUT_REDIRECT_PARAMETER;
import static de.cuioss.portal.configuration.OAuthConfigKeys.OPEN_ID_CLIENT_POST_LOGOUT_REDIRECT_URI;
import static de.cuioss.portal.configuration.OAuthConfigKeys.OPEN_ID_CLIENT_SECRET;
import static de.cuioss.portal.configuration.OAuthConfigKeys.OPEN_ID_SERVER_BASE_URL;
import static de.cuioss.portal.configuration.OAuthConfigKeys.OPEN_ID_SERVER_TOKEN_URL;
import static de.cuioss.portal.configuration.OAuthConfigKeys.OPEN_ID_SERVER_USER_INFO_URL;
import static de.cuioss.tools.collect.CollectionLiterals.immutableList;

import java.util.List;

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.OAuthConfigKeys;
import de.cuioss.portal.core.test.tests.configuration.AbstractConfigurationKeyVerifierTest;
import de.cuioss.portal.core.test.tests.configuration.PropertiesDefaultConfigSource;
import lombok.Getter;

class DefaultConfigTest extends AbstractConfigurationKeyVerifierTest {

    @Getter
    private final FileConfigurationSource underTest = new PropertiesDefaultConfigSource();

    @Override
    public Class<?> getKeyHolder() {
        return OAuthConfigKeys.class;
    }

    @Override
    public List<String> getKeysIgnoreList() {
        return immutableList(OPEN_ID_CLIENT_ID, OPEN_ID_CLIENT_SECRET, OPEN_ID_SERVER_BASE_URL, EXTERNAL_HOSTNAME,
                OPEN_ID_SERVER_TOKEN_URL, OPEN_ID_SERVER_USER_INFO_URL, OPEN_ID_CLIENT_POST_LOGOUT_REDIRECT_URI);
    }

    @Override
    public List<String> getConfigurationKeysIgnoreList() {
        return immutableList(OPEN_ID_CLIENT_LOGOUT_REDIRECT_PARAMETER);
    }
}
