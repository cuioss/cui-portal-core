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
package de.cuioss.portal.authentication.oauth.impl;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.TypedGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("LombokBuilder")
class Oauth2ConfigurationImplTest {

    private static final TypedGenerator<String> nonEmptyLetterStringGenerator = Generators.letterStrings(1, 5);

    @Test
    void validConfig() {
        Oauth2ConfigurationImpl config = createConfig();

        assertDoesNotThrow(config::validate);
    }

    @Test
    void missingTokenUri() {
        Oauth2ConfigurationImpl config = createConfig();
        config.setTokenUri(null);

        assertThrows(IllegalStateException.class, config::validate);
    }

    @Test
    void missingClientId() {
        Oauth2ConfigurationImpl config = createConfig();
        config.setClientId(null);

        assertThrows(IllegalStateException.class, config::validate);
    }

    @Test
    void missingAuthorizeUri() {
        Oauth2ConfigurationImpl config = createConfig();
        config.setAuthorizeUri(null);

        assertThrows(IllegalStateException.class, config::validate);
    }

    private Oauth2ConfigurationImpl createConfig() {
        Oauth2ConfigurationImpl.Oauth2ConfigurationImplBuilder configBuilder = Oauth2ConfigurationImpl.builder();
        configBuilder.clientId(nonEmptyLetterStringGenerator.next());
        configBuilder.clientSecret(nonEmptyLetterStringGenerator.next());
        configBuilder.initialScopes(nonEmptyLetterStringGenerator.next());
        configBuilder.logoutUri(nonEmptyLetterStringGenerator.next());
        configBuilder.externalContextPath(nonEmptyLetterStringGenerator.next());
        configBuilder.authorizeUri(nonEmptyLetterStringGenerator.next());
        configBuilder.logoutRedirectParamName(nonEmptyLetterStringGenerator.next());
        configBuilder.postLogoutRedirectUri(nonEmptyLetterStringGenerator.next());
        configBuilder.roleMapperClaims(List.of(nonEmptyLetterStringGenerator.next()));
        configBuilder.tokenUri(nonEmptyLetterStringGenerator.next());
        configBuilder.externalContextPath(nonEmptyLetterStringGenerator.next());
        configBuilder.userInfoUri(nonEmptyLetterStringGenerator.next());
        return configBuilder.build();
    }
}
