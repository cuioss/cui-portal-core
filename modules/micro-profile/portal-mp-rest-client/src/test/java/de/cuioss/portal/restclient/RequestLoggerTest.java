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

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;
import static de.cuioss.tools.string.MoreStrings.nullToEmpty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;
import lombok.AllArgsConstructor;
import lombok.Data;

@EnableTestLogger(trace = RequestLoggerTest.class)
class RequestLoggerTest {

    private static final CuiLogger log = new CuiLogger(RequestLoggerTest.class);

    private static final String URI = "https://icw-global.com/company/careers/";
    private static final String METHOD = Generators.fixedValues("GET", "POST", "LIST", "PUT", null).next();
    private static final boolean HAS_BODY = Generators.booleans().next();
    private static final String STRING = Generators.strings().next();
    private static final int INT = Generators.integers().next();

    @Test
    void requestLoggingTest() throws IOException {
        final var testPojo = new TestPojo(STRING, INT);

        log.info("Testing with string={}, int={}, method={}, pojo={}", STRING, INT, METHOD, testPojo);

        new LogClientRequestFilter(log).filter(new ClientRequestContext() {

            @Override
            public Object getProperty(String name) {
                throw new UnsupportedOperationException("getProperty");
            }

            @Override
            public Collection<String> getPropertyNames() {
                throw new UnsupportedOperationException("getPropertyNames");
            }

            @Override
            public void setProperty(String name, Object object) {
                throw new UnsupportedOperationException("setProperty");
            }

            @Override
            public void removeProperty(String name) {
                throw new UnsupportedOperationException("removeProperty");
            }

            @Override
            public URI getUri() {
                try {
                    return new URI(URI);
                } catch (URISyntaxException e) {
                    throw new RuntimeException("uri could not be set");
                }
            }

            @Override
            public void setUri(URI uri) {
                throw new UnsupportedOperationException("setUri");
            }

            @Override
            public String getMethod() {
                return METHOD;
            }

            @Override
            public void setMethod(String method) {
                throw new UnsupportedOperationException("setMethod");
            }

            @Override
            public MultivaluedMap<String, Object> getHeaders() {
                throw new UnsupportedOperationException("getHeaders");
            }

            @Override
            public MultivaluedMap<String, String> getStringHeaders() {
                final var map = new MultivaluedHashMap<String, String>();
                map.putSingle("a-string", "foobar");
                map.put("a-list", immutableList("a", "b", "c"));
                return map;
            }

            @Override
            public String getHeaderString(String name) {
                throw new UnsupportedOperationException("getHeaderString");
            }

            @Override
            public Date getDate() {
                throw new UnsupportedOperationException("getDate");
            }

            @Override
            public Locale getLanguage() {
                throw new UnsupportedOperationException("getLanguage");
            }

            @Override
            public MediaType getMediaType() {
                throw new UnsupportedOperationException("getMediaType");
            }

            @Override
            public List<MediaType> getAcceptableMediaTypes() {
                throw new UnsupportedOperationException("getAcceptableMediaTypes");
            }

            @Override
            public List<Locale> getAcceptableLanguages() {
                throw new UnsupportedOperationException("getAcceptableLanguages");
            }

            @Override
            public Map<String, Cookie> getCookies() {
                throw new UnsupportedOperationException("getCookies");
            }

            @Override
            public boolean hasEntity() {
                return HAS_BODY;
            }

            @Override
            public Object getEntity() {
                if (HAS_BODY) {
                    return testPojo;
                }
                return null;
            }

            @Override
            public Class<?> getEntityClass() {
                if (HAS_BODY) {
                    return testPojo.getClass();
                }
                return null;
            }

            @Override
            public Type getEntityType() {
                return getEntityClass();
            }

            @Override
            public void setEntity(Object entity) {
                throw new UnsupportedOperationException("setEntity(Object)");
            }

            @Override
            public void setEntity(Object entity, Annotation[] annotations, MediaType mediaType) {
                throw new UnsupportedOperationException("setEntity(Object, Annotation, MediaType)");
            }

            @Override
            public Annotation[] getEntityAnnotations() {
                if (HAS_BODY) {
                    return new Annotation[0];
                }
                return null;
            }

            @Override
            public OutputStream getEntityStream() {
                if (HAS_BODY) {
                    // Per API, the JAX-RS runtime is responsible for closing this stream.
                    final var bas = new ByteArrayOutputStream();
                    try {
                        bas.write(STRING.getBytes(StandardCharsets.ISO_8859_1));
                        return bas;
                    } catch (final IOException e) {
                        throw new RuntimeException("Could not write request body");
                    }
                }
                return null;
            }

            @Override
            public void setEntityStream(OutputStream outputStream) {
                throw new UnsupportedOperationException("setEntityStream");
            }

            @Override
            public Client getClient() {
                throw new UnsupportedOperationException("getClient");
            }

            @Override
            public Configuration getConfiguration() {
                throw new UnsupportedOperationException("getConfiguration");
            }

            @Override
            public void abortWith(Response response) {
                throw new UnsupportedOperationException("abortWith");
            }
        });

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "-- Client request info --");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Request URI: " + URI);
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Method: " + nullToEmpty(METHOD));
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Headers:");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "a-list: [a, b, c]");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "a-string: [foobar]");

        if (HAS_BODY) {
            LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Body:");
            LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, testPojo.toString());
        }
    }

    @Data
    @AllArgsConstructor
    public static class TestPojo {

        private final String string;
        private final int integer;
    }
}
