package de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher;

import java.util.Optional;

import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import lombok.NonNull;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * The idea of an {@link ModuleDispatcherElement} is the reuse of answers in the context of
 * {@link EnableMockWebServer}. In essence calls to {@link MockWebServerHolder#getDispatcher()} can
 * be replaced with this structure.
 * The general idea is to return an {@link Optional} {@link MockResponse} if the concrete handle can
 * answer the call, {@link Optional#empty()} otherwise.
 */

public interface ModuleDispatcherElement {

    /**
     * @return the base URl for this Dispatcher part. The runtime will ensure that only elements
     *         will called, where the current url starts with the one given here. If you want to
     *         filter yourself you may return '/'. The {@link Optional} contract is unaffected by
     *         this.
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
