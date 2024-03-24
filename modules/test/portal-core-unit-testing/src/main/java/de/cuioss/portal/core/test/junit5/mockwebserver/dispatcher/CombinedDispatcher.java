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

import java.util.ArrayList;
import java.util.List;

import de.cuioss.tools.collect.CollectionLiterals;
import de.cuioss.tools.logging.CuiLogger;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.RecordedRequest;

/**
 * Aggregates a number of {@link ModuleDispatcherElement}s in order to reuse
 * functionality
 */
@NoArgsConstructor
public class CombinedDispatcher extends Dispatcher {

    public static final int HTTP_CODE_NOT_FOUND = 404;

    public static final int HTTP_CODE_TEAPOT = 418;

    private static final CuiLogger LOGGER = new CuiLogger(CombinedDispatcher.class);

    /**
     * If set to {@code true} on mismatch of the request will return a Http-Code
     * '418', '404' otherwise
     */
    private boolean endWithTeapot = true;

    private final List<ModuleDispatcherElement> singleDispatcher = new ArrayList<>();

    /**
     * @param dispatcherElement to be used
     */
    public CombinedDispatcher(ModuleDispatcherElement dispatcherElement) {
        singleDispatcher.add(dispatcherElement);
    }

    /**
     * @param dispatcherElement to be used
     */
    public CombinedDispatcher(ModuleDispatcherElement... dispatcherElement) {
        singleDispatcher.addAll(CollectionLiterals.mutableList(dispatcherElement));
    }

    /*
     * (non-Javadoc)
     *
     * @see okhttp3.mockwebserver.Dispatcher#dispatch(okhttp3.mockwebserver.
     * RecordedRequest)
     */
    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        var path = request.getPath();
        var mapper = HttpMethodMapper.of(request);
        LOGGER.info("Processing method '{}' with path '{}'", mapper, path);

        List<ModuleDispatcherElement> filtered = new ArrayList<>();

        for (ModuleDispatcherElement dispatcher : singleDispatcher) {
            if (path.startsWith(dispatcher.getBaseUrl())) {
                filtered.add(dispatcher);
            } else {
                LOGGER.info(dispatcher.getBaseUrl());
            }
        }

        for (ModuleDispatcherElement moduleDispatcherElement : filtered) {
            var result = mapper.handleMethod(moduleDispatcherElement, request);
            if (result.isPresent()) {
                return result.get();
            }
        }
        LOGGER.info(
                "Method '{}' with path '{}' could not be processed by the configured ModuleDispatcherElements. Going to default",
                mapper, path);
        var code = HTTP_CODE_TEAPOT;
        if (!endWithTeapot) {
            code = HTTP_CODE_NOT_FOUND;
        }
        return new MockResponse(code);
    }

    /**
     * @param endWithTeapot If set to {@code true} on mismatch of the request will
     *                      return a Http-Code '418', '404' otherwise
     * @return The instance itself in order to use it in a builder-style
     */
    public CombinedDispatcher endWithTeapot(boolean endWithTeapot) {
        this.endWithTeapot = endWithTeapot;
        return this;
    }

    /**
     * @param dispatcherElement must not be null
     * @return The instance itself in order to use it in a builder-style
     */
    public CombinedDispatcher addDispatcher(@NonNull ModuleDispatcherElement dispatcherElement) {
        singleDispatcher.add(dispatcherElement);
        return this;
    }

    /**
     * @param dispatcherElements must not be null
     * @return The instance itself in order to use it in a builder-style
     */
    public CombinedDispatcher addDispatcher(@NonNull List<ModuleDispatcherElement> dispatcherElements) {
        singleDispatcher.addAll(dispatcherElements);
        return this;
    }

    /**
     * @param dispatcherElements to be added
     * @return The instance itself in order to use it in a builder-style
     */
    public CombinedDispatcher addDispatcher(ModuleDispatcherElement... dispatcherElements) {
        singleDispatcher.addAll(CollectionLiterals.mutableList(dispatcherElements));
        return this;
    }

}
