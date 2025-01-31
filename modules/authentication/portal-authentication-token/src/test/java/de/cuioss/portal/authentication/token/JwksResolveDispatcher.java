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
package de.cuioss.portal.authentication.token;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.ModuleDispatcherElement;
import de.cuioss.tools.io.FileLoaderUtility;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import mockwebserver3.MockResponse;
import mockwebserver3.RecordedRequest;
import okhttp3.Headers;

import java.util.Optional;

/**
 * Handles the Resolving of JWKS Files from the Mocked oauth-Server. In essence, it returns the file
 * "src/test/resources/token/test-public-key.jwks"
 */
public class JwksResolveDispatcher implements ModuleDispatcherElement {

    /**
     * "/oidc/jwks.json"
     */
    public static final String LOCAL_PATH = "/oidc/jwks.json";
    public static final String PUBLIC_KEY_JWKS = TestTokenProducer.BASE_PATH + "test-public-key.jwks";
    public static final String PUBLIC_KEY_OTHER = TestTokenProducer.BASE_PATH + "other-public-key.pub";

    public String currentKey;

    public JwksResolveDispatcher() {
        currentKey = PUBLIC_KEY_JWKS;
    }

    @Getter
    @Setter
    private int callCounter = 0;

    @Override
    public Optional<MockResponse> handleGet(@NonNull RecordedRequest request) {
        callCounter++;
        return Optional.of(new MockResponse(SC_OK, Headers.of("Content-Type", "application/json"), FileLoaderUtility
                .toStringUnchecked(FileLoaderUtility.getLoaderForPath(currentKey))));
    }

    void switchToOtherPublicKey() {
        currentKey = PUBLIC_KEY_OTHER;
    }

    @Override
    public String getBaseUrl() {
        return LOCAL_PATH;
    }

    /**
     * Verifies whether this endpoint was called the given times
     *
     * @param expected count of calls
     */
    public void assertCallsAnswered(int expected) {
        assertEquals(expected, callCounter);
    }
}
