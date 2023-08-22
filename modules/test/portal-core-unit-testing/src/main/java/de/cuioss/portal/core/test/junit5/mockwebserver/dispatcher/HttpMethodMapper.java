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

import mockwebserver3.MockResponse;
import mockwebserver3.RecordedRequest;

/**
 * Simplifies the mapping from Http-Method to the actual method.
 */
public enum HttpMethodMapper {

    /**
     * Get
     */
    GET {

        @Override
        public Optional<MockResponse> handleMethod(ModuleDispatcherElement dispatcherElement, RecordedRequest request) {
            return dispatcherElement.handleGet(request);
        }
    },
    /**
     * Post
     */
    POST {

        @Override
        public Optional<MockResponse> handleMethod(ModuleDispatcherElement dispatcherElement, RecordedRequest request) {
            return dispatcherElement.handlePost(request);
        }
    },
    /**
     * Put
     */
    PUT {

        @Override
        public Optional<MockResponse> handleMethod(ModuleDispatcherElement dispatcherElement, RecordedRequest request) {
            return dispatcherElement.handlePut(request);
        }
    },
    /**
     * Delete
     */
    DELETE {

        @Override
        public Optional<MockResponse> handleMethod(ModuleDispatcherElement dispatcherElement, RecordedRequest request) {
            return dispatcherElement.handleDelete(request);
        }
    };

    /**
     * @param dispatcherElement must not be null
     * @param request           must not be null
     * @return see ModuleDispatcherElement
     */
    public abstract Optional<MockResponse> handleMethod(ModuleDispatcherElement dispatcherElement,
            RecordedRequest request);

    /**
     * @param request must not be null
     * @return the {@link HttpMethodMapper} representing the provided Http-Method
     */
    public static HttpMethodMapper of(RecordedRequest request) {
        return HttpMethodMapper.valueOf(request.getMethod().toUpperCase());
    }
}
