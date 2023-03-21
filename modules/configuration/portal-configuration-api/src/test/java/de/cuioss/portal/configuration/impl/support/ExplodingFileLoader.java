package de.cuioss.portal.configuration.impl.support;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.StructuredFilename;

@SuppressWarnings("javadoc")
public class ExplodingFileLoader implements FileLoader {

    private static final long serialVersionUID = -2232945095983152163L;

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public StructuredFilename getFileName() {
        return null;
    }

    @Override
    public InputStream inputStream() throws IOException {
        return new InputStream() {

            @Override
            public int read() throws IOException {
                throw new IOException("boom");
            }
        };
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public boolean isFilesystemLoader() {
        return false;
    }

}
