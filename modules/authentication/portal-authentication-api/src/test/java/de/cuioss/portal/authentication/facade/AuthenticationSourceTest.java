package de.cuioss.portal.authentication.facade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationSourceTest {

    @Test
    void shouldCreateAuthenticationSource() {
        for (AuthenticationSource authenticationSource : AuthenticationSource.values()) {
            assertEquals(authenticationSource, AuthenticationSource.valueOf(authenticationSource.name()));
        }
    }

}