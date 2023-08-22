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
package de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher;

import java.util.Optional;

import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import lombok.NonNull;
import mockwebserver3.MockResponse;
import mockwebserver3.RecordedRequest;

/**
 * The idea of an {@link ModuleDispatcherElement} is the reuse of answers in the
 * context of {@link EnableMockWebServer}. In essence calls to
 * {@link MockWebServerHolder#getDispatcher()} can be replaced with this
 * structure. The general idea is to return an {@link Optional}
 * {@link MockResponse} if the concrete handle can answer the call,
 * {@link Optional#empty()} otherwise.
 */

public interface ModuleDispatcherElement {

    /**
     * @return the base URl for this Dispatcher part. The runtime will ensure that
     *         only elements will called, where the current url starts with the one
     *         given here. If you want to filter yourself you may return '/'. The
     *         {@link Optional} contract is unaffected by this.
     */
    String getBaseUrl();

    /**
     * Handles Get-Requests
     *
     * @param request providing all necessary Information for answering the request
     * @return {@link MockResponse} if the concrete handle can answer the call,
     *         {@link Optional#empty()} otherwise.
     */
    default Optional<MockResponse> handleGet(@NonNull RecordedRequest request) {
        return Optional.empty();
    }

    /**
     * Handles Put-Requests
     *
     * @param request providing all necessary Information for answering the request
     * @return {@link MockResponse} if the concrete handle can answer the call,
     *         {@link Optional#empty()} otherwise.
     */
    default Optional<MockResponse> handlePut(@NonNull RecordedRequest request) {
        return Optional.empty();
    }

    /**
     * Handles Post-Requests
     *
     * @param request providing all necessary Information for answering the request
     * @return {@link MockResponse} if the concrete handle can answer the call,
     *         {@link Optional#empty()} otherwise.
     */
    default Optional<MockResponse> handlePost(@NonNull RecordedRequest request) {
        return Optional.empty();
    }

    /**
     * Handles Delete-Requests
     *
     * @param request providing all necessary Information for answering the request
     * @return {@link MockResponse} if the concrete handle can answer the call,
     *         {@link Optional#empty()} otherwise.
     */
    default Optional<MockResponse> handleDelete(@NonNull RecordedRequest request) {
        return Optional.empty();
    }
}
