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

import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.impl.URLGenerator;
import de.cuioss.test.generator.junit.EnableGeneratorController;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;

/**
 * Tests for the {@link LogClientRequestFilter} class focusing on request logging functionality
 */
@EnableTestLogger
@EnableGeneratorController
@DisplayName("Request Logger Tests")
class RequestLoggerTest {

    private static final CuiLogger LOGGER = new CuiLogger(RequestLoggerTest.class);
    private static final String URI = new URLGenerator().next().toString();
    private static final String METHOD = Generators.fixedValues("GET", "POST", "LIST", "PUT", null).next();
    private static final boolean HAS_BODY = Generators.booleans().next();
    private static final String STRING = Generators.strings().next();
    private static final int INT = Generators.integers().next();

    private TestPojo testPojo;
    private LogClientRequestFilter underTest;

    @BeforeEach
    void setUp() {
        testPojo = new TestPojo(STRING, INT);
        underTest = new LogClientRequestFilter(LOGGER);
        LOGGER.info("Testing with string={}, int={}, method={}, pojo={}", STRING, INT, METHOD, testPojo);
    }

    @Test
    @DisplayName("Should properly log request details")
    void shouldLogRequestDetails() throws IOException {
        underTest.filter(new ClientRequestContext() {
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
                var headers = new MultivaluedHashMap<String, Object>();
                headers.put("test", immutableList("test"));
                return headers;
            }

            @Override
            public MultivaluedMap<String, String> getStringHeaders() {
                var headers = new MultivaluedHashMap<String, String>();
                headers.put("test", immutableList("test"));
                return headers;
            }

            @Override
            public String getHeaderString(String name) {
                return "test";
            }

            @Override
            public Date getDate() {
                return null;
            }

            @Override
            public Locale getLanguage() {
                return null;
            }

            @Override
            public MediaType getMediaType() {
                return null;
            }

            @Override
            public List<MediaType> getAcceptableMediaTypes() {
                return immutableList();
            }

            @Override
            public List<Locale> getAcceptableLanguages() {
                return immutableList();
            }

            @Override
            public Map<String, Cookie> getCookies() {
                return Map.of();
            }

            @Override
            public boolean hasEntity() {
                return HAS_BODY;
            }

            @Override
            public Object getEntity() {
                return testPojo;
            }

            @Override
            public Class<?> getEntityClass() {
                return TestPojo.class;
            }

            @Override
            public Type getEntityType() {
                return TestPojo.class;
            }

            @Override
            public void setEntity(Object entity) {
                throw new UnsupportedOperationException("setEntity");
            }

            @Override
            public void setEntity(Object entity, Annotation[] annotations, MediaType mediaType) {
                throw new UnsupportedOperationException("setEntity");
            }

            @Override
            public Annotation[] getEntityAnnotations() {
                return new Annotation[0];
            }

            @Override
            public OutputStream getEntityStream() {
                return new ByteArrayOutputStream();
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

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Request URI: " + URI);
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Headers: test: [test]");
        if (HAS_BODY) {
            LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Body: " + testPojo);
        }
    }

    @Data
    @AllArgsConstructor
    static class TestPojo {
        private String string;
        private int integer;
    }
}
