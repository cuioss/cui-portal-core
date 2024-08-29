/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.authentication.oauth;

import de.cuioss.portal.core.test.tests.configuration.AbstractConfigurationKeyVerifierTest;
import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.List;

import static de.cuioss.portal.authentication.oauth.OAuthConfigKeys.*;
import static de.cuioss.tools.collect.CollectionLiterals.immutableList;

class DefaultConfigTest extends AbstractConfigurationKeyVerifierTest {

    @Override
    public Class<?> getKeyHolder() {
        return OAuthConfigKeys.class;
    }

    /**
     * @return the name of {@link ConfigSource} that will be checked against the
     * derived keys.
     */
    @Override
    public String getConfigSourceName() {
        return "portal-authentication-oauth";
    }

    @Override
    public List<String> getKeysIgnoreList() {
        return immutableList(OPEN_ID_CLIENT_ID, OPEN_ID_CLIENT_SECRET, OPEN_ID_SERVER_BASE_URL, EXTERNAL_HOSTNAME,
                OPEN_ID_SERVER_TOKEN_URL, OPEN_ID_SERVER_USER_INFO_URL, OPEN_ID_CLIENT_POST_LOGOUT_REDIRECT_URI, OPEN_ID_ROLE_MAPPER_CLAIM);
    }

    @Override
    public List<String> getConfigurationKeysIgnoreList() {
        return immutableList(OPEN_ID_CLIENT_LOGOUT_REDIRECT_PARAMETER);
    }
}
