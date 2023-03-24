package de.cuioss.portal.authentication.oauth;

import static de.cuioss.test.generator.Generators.nonEmptyStrings;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OidcRpInitiatedLogoutParamsTest {

    @Test
    void shouldHandleParameter() {
        assertTrue(OidcRpInitiatedLogoutParams.getClientIdUrlParam(nonEmptyStrings().next()).isPresent());
        assertTrue(OidcRpInitiatedLogoutParams.getIdTokenHintUrlParam(nonEmptyStrings().next()).isPresent());
        assertTrue(OidcRpInitiatedLogoutParams.getLogoutHintUrlParam(nonEmptyStrings().next()).isPresent());
        assertTrue(OidcRpInitiatedLogoutParams.getPostLogoutRedirectUriUrlParam(nonEmptyStrings().next()).isPresent());
        assertTrue(OidcRpInitiatedLogoutParams.getStateUrlParam(nonEmptyStrings().next()).isPresent());
        assertTrue(OidcRpInitiatedLogoutParams.getUiLocalesUrlParam(nonEmptyStrings().next()).isPresent());
    }

}
