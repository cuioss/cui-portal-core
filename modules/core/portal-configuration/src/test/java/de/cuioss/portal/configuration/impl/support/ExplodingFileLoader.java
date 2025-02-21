/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.configuration.impl.support;

import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.StructuredFilename;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.net.URL;

public class ExplodingFileLoader implements FileLoader {

    @Serial
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
