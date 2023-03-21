package de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher;

import java.util.Optional;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

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
     * @param request must not be null
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
