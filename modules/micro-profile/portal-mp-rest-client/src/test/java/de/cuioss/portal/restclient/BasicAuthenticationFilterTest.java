package de.cuioss.portal.restclient;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BasicAuthenticationFilterTest {

    @Test
    void testNullValues() {
        assertThrows(NullPointerException.class, () -> new BasicAuthenticationFilter(null, null));
    }

}
