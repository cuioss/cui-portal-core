package de.cuioss.portal.authentication.oauth.impl;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.TypedGenerator;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        configBuilder.roleMapperClaims(Collections.singletonList(nonEmptyLetterStringGenerator.next()));
        configBuilder.tokenUri(nonEmptyLetterStringGenerator.next());
        configBuilder.externalContextPath(nonEmptyLetterStringGenerator.next());
        configBuilder.userInfoUri(nonEmptyLetterStringGenerator.next());
        return configBuilder.build();
    }
}
