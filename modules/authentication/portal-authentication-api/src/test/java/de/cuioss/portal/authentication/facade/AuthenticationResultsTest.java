package de.cuioss.portal.authentication.facade;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AuthenticationResultsTest {

    @Test
    void shouldProvideResults() {
        assertNotNull(AuthenticationResults.validResult(AuthenticationResults.NOT_LOGGED_IN));
        assertNotNull(AuthenticationResults.invalidResult("reaon", "testuser", null));
        assertNotNull(AuthenticationResults.invalidResultKey("reaonKey", "testuser", null));
    }

}
