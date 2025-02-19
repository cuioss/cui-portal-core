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
import de.cuioss.test.generator.junit.EnableGeneratorController;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;

/**
 * Tests for the {@link LogClientResponseFilter} class focusing on response logging functionality
 */
@EnableTestLogger
@EnableGeneratorController
@DisplayName("Response Logger Tests")
class ResponseLoggerTest {

    private static final CuiLogger LOGGER = new CuiLogger(ResponseLoggerTest.class);
    private static final String TEST_BODY = Generators.strings().next();
    private static final int STATUS = Generators.integers(200, 599).next();
    private static final String REASON = Generators.strings().next();

    private LogClientResponseFilter underTest;

    @BeforeEach
    void setUp() {
        underTest = new LogClientResponseFilter(LOGGER) {
        };
        LOGGER.info("Testing with status={}, reason={}, body={}", STATUS, REASON, TEST_BODY);
    }

    @Test
    @DisplayName("Should properly log response details")
    void shouldLogResponseDetails() throws IOException {
        underTest.filter(null, new ClientResponseContext() {
            @Override
            public int getStatus() {
                return STATUS;
            }

            @Override
            public void setStatus(int code) {
                // Not needed for test
            }

            @Override
            public Response.StatusType getStatusInfo() {
                return Response.Status.fromStatusCode(STATUS);
            }

            @Override
            public void setStatusInfo(Response.StatusType statusInfo) {
                // Not needed for test
            }

            @Override
            public InputStream getEntityStream() {
                return new ByteArrayInputStream(TEST_BODY.getBytes());
            }

            @Override
            public void setEntityStream(InputStream entityStream) {
                // Not needed for test
            }

            @Override
            public boolean hasEntity() {
                return true;
            }

            @Override
            public MediaType getMediaType() {
                return MediaType.TEXT_PLAIN_TYPE;
            }

            public void setMediaType(MediaType mediaType) {
                // Not needed for test
            }

            @Override
            public Locale getLanguage() {
                return Locale.ENGLISH;
            }

            public void setLanguage(Locale language) {
                // Not needed for test
            }

            @Override
            public int getLength() {
                return TEST_BODY.length();
            }

            public void setLength(int length) {
                // Not needed for test
            }

            @Override
            public Set<String> getAllowedMethods() {
                return Set.of("GET", "POST");
            }

            @Override
            public Map<String, NewCookie> getCookies() {
                return Map.of();
            }

            @Override
            public EntityTag getEntityTag() {
                return null;
            }

            @Override
            public Date getDate() {
                return null;
            }

            @Override
            public Date getLastModified() {
                return null;
            }

            @Override
            public URI getLocation() {
                return null;
            }

            @Override
            public Set<Link> getLinks() {
                return Set.of();
            }

            @Override
            public boolean hasLink(String relation) {
                return false;
            }

            @Override
            public Link getLink(String relation) {
                return null;
            }

            @Override
            public Link.Builder getLinkBuilder(String relation) {
                return null;
            }

            @Override
            public MultivaluedMap<String, String> getHeaders() {
                var headers = new MultivaluedHashMap<String, String>();
                headers.put("test", immutableList("test-value"));
                return headers;
            }

            @Override
            public String getHeaderString(String name) {
                return "test-value";
            }
        });

        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Status: " + STATUS);
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Headers: {test=[test-value]}");
    }
}
