package de.cuioss.portal.configuration.impl.connection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import de.cuioss.portal.configuration.connections.exception.ConnectionException;
import de.cuioss.portal.configuration.connections.exception.ErrorReason;
import de.cuioss.portal.configuration.connections.impl.AbstractSystemConnectionImpl;

/**
 * Simple implementation of {@link URL} based connections. Currently, it does not
 * authenticate. If we need this for production we should consider using
 * http://hc.apache.org/
 *
 * @author Oliver Wolff
 */
public class UrlConnectionImpl extends AbstractSystemConnectionImpl<URLConnection> {

    private URL url;

    private URLConnection urlConnection;

    @Override
    public void checkConnection() throws ConnectionException {
        getConnectionMetadata().validate();

        try {
            getURLConnection().connect();
            try (var input = getURLConnection().getInputStream()) {
                if (null == input) {
                    throw new ConnectionException(ErrorReason.CHECK_CONNECTION_FAILED);
                }
            }
        } catch (final IOException e) {
            throw new ConnectionException(e, ErrorReason.IO_ERROR);
        }
    }

    @Override
    public URLConnection openConnection() throws ConnectionException {
        checkConnection();
        return getURLConnection();
    }

    private URLConnection getURLConnection() throws ConnectionException {
        if (null == urlConnection) {
            try {
                urlConnection = getUrl().openConnection();
            } catch (final IOException e) {
                throw new ConnectionException(e, ErrorReason.IO_ERROR);
            }
        }
        return urlConnection;
    }

    private URL getUrl() throws ConnectionException {
        if (null == url) {
            try {
                url = new URL(getConnectionMetadata().getServiceUrl());
            } catch (final MalformedURLException e) {
                throw new ConnectionException(e, ErrorReason.INVALID_URL);
            }
        }
        return url;
    }

    @Override
    public void close() {
        urlConnection = null;
    }
}
