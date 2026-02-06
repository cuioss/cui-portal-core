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
import de.cuioss.test.generator.junit.EnableGeneratorController;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Locale;
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
        LOGGER.info("Testing with status=%s, reason=%s, body=%s", STATUS, REASON, TEST_BODY);
    }

    @Test
    @DisplayName("Should properly log response details")
    void shouldLogResponseDetails() throws IOException {
        // Create mock
        ClientResponseContext responseContext = EasyMock.createNiceMock(ClientResponseContext.class);

        // Set up expectations
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.put("test", immutableList("test-value"));

        EasyMock.expect(responseContext.getStatus()).andReturn(STATUS).anyTimes();
        EasyMock.expect(responseContext.getStatusInfo()).andReturn(Response.Status.fromStatusCode(STATUS)).anyTimes();
        EasyMock.expect(responseContext.getHeaders()).andReturn(headers).anyTimes();
        EasyMock.expect(responseContext.getAllowedMethods()).andReturn(Set.of("GET", "POST")).anyTimes();
        EasyMock.expect(responseContext.getLanguage()).andReturn(Locale.ENGLISH).anyTimes();
        EasyMock.expect(responseContext.getMediaType()).andReturn(MediaType.TEXT_PLAIN_TYPE).anyTimes();

        // Replay
        EasyMock.replay(responseContext);

        // Execute
        underTest.filter(null, responseContext);

        // Verify
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Status: " + STATUS);
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Headers: {test=[test-value]}");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "MediaType: text/plain");
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Language: en");
    }
}
