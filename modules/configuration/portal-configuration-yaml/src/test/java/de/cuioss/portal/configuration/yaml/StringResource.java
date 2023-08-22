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
package de.cuioss.portal.configuration.yaml;

import static de.cuioss.tools.base.Preconditions.checkArgument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.StructuredFilename;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Implementation of {@linkplain FileLoader}
 * <p>
 * Useful for loading content from any given String, without having to resort to
 * a single-use {@link InputStream}.
 *
 * @author Sven Haag
 */
@EqualsAndHashCode(of = "value")
@ToString(of = "value")
class StringResource implements FileLoader {

    private static final long serialVersionUID = 7097229795010395904L;
    private final String value;

    /**
     * Create a new {@code ByteArrayResource} with a description.
     *
     * @param value the String to wrap
     */
    StringResource(final String value) {
        checkArgument(null != value, "value must not be null");
        this.value = value;
    }

    /**
     * This implementation always returns {@code true}.
     *
     * @return always true
     */
    public static boolean exists() {
        return true;
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public StructuredFilename getFileName() {
        return new StructuredFilename("ByteSource");
    }

    /**
     * This implementation returns a ByteArrayInputStream for the underlying byte
     * array.
     *
     * @see ByteArrayInputStream
     */
    @Override
    public InputStream inputStream() throws IOException {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
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
