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
package de.cuioss.portal.restclient;

import static de.cuioss.test.juli.TestLogLevel.INFO;
import static de.cuioss.tools.collect.CollectionLiterals.immutableList;
import static de.cuioss.tools.collect.CollectionLiterals.immutableSet;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

import org.junit.jupiter.api.Test;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;

/**
 * @author Sven Haag
 */
@EnableTestLogger(trace = ResponseLoggerTest.class)
class ResponseLoggerTest {

    private static final CuiLogger log = new CuiLogger(ResponseLoggerTest.class);

    private static final String STRING = Generators.nonEmptyStrings().next();
    private static final boolean HAS_BODY = Generators.booleans().next();
    private static final MediaType MEDIA_TYPE = Generators.fixedValues(MediaType.WILDCARD_TYPE,
            MediaType.TEXT_PLAIN_TYPE, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE).next();
    private boolean inputStreamSet;
    private boolean proceedExecuted;

    @Test
    void responseLoggerTest() throws IOException {
        inputStreamSet = false;

        new LogReaderInterceptor(log).aroundReadFrom(new ReaderInterceptorContext() {

            @Override
            public Object proceed() throws IOException, WebApplicationException {
                proceedExecuted = true;
                return null;
            }

            @Override
            public InputStream getInputStream() {
                return new BufferedInputStream(
                        new ByteArrayInputStream((HAS_BODY ? STRING : "").getBytes(StandardCharsets.UTF_8)));
            }

            @Override
            public void setInputStream(final InputStream is) {
                assertNotNull(is, "InputStream must not be null after logging");
                if (HAS_BODY) {
                    assertDoesNotThrow(() -> {
                        assertTrue(is.available() > 0, "InputStream has no data");
                    }, "Could not read response input stream after logging");
                }
                inputStreamSet = true;
            }

            @Override
            public MultivaluedMap<String, String> getHeaders() {
                final var map = new MultivaluedHashMap<String, String>();
                map.putSingle("a-string", "foobar");
                map.put("a-list", immutableList("a", "b", "c"));
                return map;
            }

            @Override
            public Object getProperty(final String name) {
                Objects.requireNonNull(name);
                if ("P1".equals(name)) {
                    return "V1";
                }
                if ("P2".equals(name)) {
                    return "V2";
                }
                if ("P3".equals(name)) {
                    return 666L;
                }
                return null;
            }

            @Override
            public Collection<String> getPropertyNames() {
                return immutableSet("P1", "P2", "P3", "P4");
            }

            @Override
            public void setProperty(final String name, final Object object) {
                throw new UnsupportedOperationException("setProperty");
            }

            @Override
            public void removeProperty(final String name) {
                throw new UnsupportedOperationException("removeProperty");
            }

            @Override
            public Annotation[] getAnnotations() {
                return new Annotation[0];
            }

            @Override
            public void setAnnotations(final Annotation[] annotations) {
                throw new UnsupportedOperationException("setAnnotations");
            }

            @Override
            public Class<?> getType() {
                return String.class;
            }

            @Override
            public void setType(final Class<?> type) {
                throw new UnsupportedOperationException("setType");
            }

            @Override
            public Type getGenericType() {
                return getType();
            }

            @Override
            public void setGenericType(final Type genericType) {
                throw new UnsupportedOperationException("setGenericType(Type)");
            }

            @Override
            public MediaType getMediaType() {
                return MEDIA_TYPE;
            }

            @Override
            public void setMediaType(final MediaType mediaType) {
                throw new UnsupportedOperationException("setMediaType");
            }
        });

        assertTrue(inputStreamSet, "InputStream not set");
        assertTrue(proceedExecuted, "The proceed method was not executed");

        LogAsserts.assertLogMessagePresentContaining(INFO, "-- Client response info --");
        LogAsserts.assertLogMessagePresentContaining(INFO, "MediaType: " + MEDIA_TYPE);
        LogAsserts.assertLogMessagePresentContaining(INFO, "GenericType: class java.lang.String");
        LogAsserts.assertLogMessagePresentContaining(INFO, "Properties:");
        LogAsserts.assertLogMessagePresentContaining(INFO, "P1: V1");
        LogAsserts.assertLogMessagePresentContaining(INFO, "P2: V2");
        LogAsserts.assertLogMessagePresentContaining(INFO, "P3: 666");
        LogAsserts.assertLogMessagePresentContaining(INFO, "P4: null");
        LogAsserts.assertLogMessagePresentContaining(INFO, "Headers:");
        LogAsserts.assertLogMessagePresentContaining(INFO, "a-list: [a, b, c]");
        LogAsserts.assertLogMessagePresentContaining(INFO, "a-string: [foobar]");
        LogAsserts.assertLogMessagePresentContaining(INFO, "Body:");
        if (HAS_BODY) {
            LogAsserts.assertLogMessagePresentContaining(INFO, STRING);
        }
    }
}
