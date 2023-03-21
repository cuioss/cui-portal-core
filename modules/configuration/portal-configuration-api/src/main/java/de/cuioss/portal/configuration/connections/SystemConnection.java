package de.cuioss.portal.configuration.connections;

import java.net.URLConnection;

import de.cuioss.portal.configuration.connections.exception.ConnectionException;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.uimodel.service.OptionalService;

/**
 * A class to hold the details of how to connect to a back end.
 *
 * @author Oliver Wolff
 * @param <T>
 *            identifying the concrete type of the established connection that
 *            will be returned by {@link #openConnection()}
 */
public interface SystemConnection<T> extends AutoCloseable, OptionalService {

    /**
     * Checks the connection
     *
     * @throws ConnectionException providing information what actually went wrong
     */
    void checkConnection() throws ConnectionException;

    /**
     * @return the metadata for this connection.
     */
    ConnectionMetadata getConnectionMetadata();

    /**
     * Opens the connection encapsulated by this object. The connections may be stored within the
     * object.
     * Therefore the close method should always be called, even if it has no effect on some
     * connection-types like {@link URLConnection}
     *
     * @return an object representing the established connection. Depending on the concrete type
     *         further methods of
     *         open / instantiating may be necessary.
     * @throws ConnectionException providing information what actually went wrong
     */
    T openConnection() throws ConnectionException;

}
