package de.cuioss.portal.configuration.connections.impl;

import de.cuioss.portal.configuration.connections.SystemConnection;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for creating connection objects.
 *
 * @author Oliver Wolff
 * @param <T>
 *            identifying the concrete type of the established connection that
 *            will be returned by openConnection()
 */
public abstract class AbstractSystemConnectionImpl<T> implements SystemConnection<T> {

    @Getter
    @Setter
    private ConnectionMetadata connectionMetadata;

}
