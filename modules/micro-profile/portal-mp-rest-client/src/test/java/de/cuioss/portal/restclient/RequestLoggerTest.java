/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
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
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
        LOGGER.info("Testing with string=%s, int=%s, method=%s, pojo=%s", STRING, INT, METHOD, testPojo);
    }

    @Test
    @DisplayName("Should properly log request details")
    void shouldLogRequestDetails() throws IOException, URISyntaxException {
        // Create mock
        ClientRequestContext requestContext = EasyMock.createNiceMock(ClientRequestContext.class);

        // Set up expectations
        MultivaluedMap<String, String> stringHeaders = new MultivaluedHashMap<>();
        stringHeaders.put("test", immutableList("test-value"));

        EasyMock.expect(requestContext.getMethod()).andReturn(METHOD).anyTimes();
        EasyMock.expect(requestContext.getUri()).andReturn(new URI(URI)).anyTimes();
        EasyMock.expect(requestContext.getStringHeaders()).andReturn(stringHeaders).anyTimes();
        EasyMock.expect(requestContext.hasEntity()).andReturn(HAS_BODY).anyTimes();
        if (HAS_BODY) {
            EasyMock.expect(requestContext.getEntity()).andReturn(testPojo).anyTimes();
        }

        // Replay
        EasyMock.replay(requestContext);

        // Execute
        underTest.filter(requestContext);

        // Verify
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "-- Client request info --");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Request URI: " + URI);
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Method: " + nullToEmpty(METHOD));
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "test: [test-value]");
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

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
