package de.cuioss.portal.configuration.connections;

import java.io.Serializable;
import java.util.List;

import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;

/**
 * Describes methods to store, update and retrieve connection metadata.
 *
 * @author Oliver Wolff
 */
public interface ConnectionMetadataStorage {

    /**
     * Stores the given metadata. If metadata with the same id already exists it
     * will be replaced / updated
     *
     * @param connectionMetadata
     *            to be stored
     * @return the stored ConnectionMetadata
     */
    ConnectionMetadata storeMetadata(ConnectionMetadata connectionMetadata);

    /**
     * @return all {@link ConnectionMetadata} elements stored within this
     *         storage.
     */
    List<ConnectionMetadata> getAllConnectionsMetadata();

    /**
     * @param id
     *            must not be null
     * @return the corresponding ConnectionMetadata. in case non can be found it
     *         throws an {@link IllegalArgumentException}
     */
    ConnectionMetadata getConnectionsMetadataById(Serializable id);

    /**
     * Removes a metadata from the storage
     *
     * @param id
     *            identifying the {@link ConnectionMetadata}
     * @return the removed {@link ConnectionMetadata} or null if non could be
     *         removed
     */
    ConnectionMetadata remove(Serializable id);
}
