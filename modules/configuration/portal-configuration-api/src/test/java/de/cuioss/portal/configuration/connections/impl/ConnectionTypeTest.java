package de.cuioss.portal.configuration.connections.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ConnectionTypeTest {

    @Test
    void shouldResolveExistingValues() {
        assertEquals(ConnectionType.JMX, ConnectionType.resolveFrom(ConnectionType.JMX.name()));
        assertEquals(ConnectionType.REST, ConnectionType.resolveFrom(ConnectionType.REST.name()));
        assertEquals(ConnectionType.UNDEFINED, ConnectionType.resolveFrom(ConnectionType.UNDEFINED.name()));

        assertEquals(ConnectionType.JMX, ConnectionType.resolveFrom(ConnectionType.JMX.name().toLowerCase()));
        assertEquals(ConnectionType.REST, ConnectionType.resolveFrom(ConnectionType.REST.name().toLowerCase()));
        assertEquals(ConnectionType.UNDEFINED,
                ConnectionType.resolveFrom(ConnectionType.UNDEFINED.name().toLowerCase()));
    }

    @Test
    void shouldDefaultToUndefined() {
        assertEquals(ConnectionType.UNDEFINED, ConnectionType.resolveFrom(null));
        assertEquals(ConnectionType.UNDEFINED, ConnectionType.resolveFrom("notThere"));
    }
}
