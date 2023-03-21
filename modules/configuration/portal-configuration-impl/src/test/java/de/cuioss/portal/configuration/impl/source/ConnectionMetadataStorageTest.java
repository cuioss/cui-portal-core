package de.cuioss.portal.configuration.impl.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.connections.PortalConnectionMetadataStorage;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.portal.configuration.impl.connection.ConnectionMetadataStorageImpl;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfiguration;
import lombok.Getter;

@EnablePortalConfiguration
@EnableAutoWeld
class ConnectionMetadataStorageTest {

    @Inject
    @PortalConnectionMetadataStorage
    @Getter
    private ConnectionMetadataStorageImpl underTest;

    @Test
    void canStoreAndDeliver() {
        var connection = ConnectionMetadata.builder().build();
        connection.setConnectionId("1");
        underTest.storeMetadata(connection);

        assertNotNull(underTest.getConnectionsMetadataById("1"));

        connection = ConnectionMetadata.builder().build();
        connection.setConnectionId("2");
        underTest.storeMetadata(connection);

        assertEquals(2, underTest.getAllConnectionsMetadata().size());
    }
}
