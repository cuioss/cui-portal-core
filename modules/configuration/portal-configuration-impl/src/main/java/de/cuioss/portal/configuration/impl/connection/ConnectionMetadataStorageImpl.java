package de.cuioss.portal.configuration.impl.connection;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import de.cuioss.portal.configuration.connections.ConnectionMetadataStorage;
import de.cuioss.portal.configuration.connections.PortalConnectionMetadataStorage;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.tools.collect.CollectionLiterals;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Base implementation storing all connections in application scope. More a mock
 * character.
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@PortalConnectionMetadataStorage
@EqualsAndHashCode
@ToString
public class ConnectionMetadataStorageImpl implements ConnectionMetadataStorage {

    private final Map<Serializable, ConnectionMetadata> map = new ConcurrentHashMap<>();

    @Override
    public ConnectionMetadata storeMetadata(final ConnectionMetadata connectionMetadata) {
        return map.put(connectionMetadata.getConnectionId(), connectionMetadata);
    }

    @Override
    public List<ConnectionMetadata> getAllConnectionsMetadata() {
        return CollectionLiterals.immutableList(map.values());
    }

    @Override
    public ConnectionMetadata getConnectionsMetadataById(final Serializable id) {
        if (!map.containsKey(id)) {
            throw new IllegalArgumentException("No connection stored with id " + id);
        }
        return map.get(id);
    }

    @Override
    public ConnectionMetadata remove(final Serializable id) {
        return map.remove(id);
    }

}
