package de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher;

import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.util.Optional;

import lombok.NonNull;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

@SuppressWarnings("javadoc")
public class AllOkDispatcher implements ModuleDispatcherElement {

    public static final String BASE = "/allOk";

    private static final MockResponse OK_RESPONSE = new MockResponse().addHeader("Content-Type", "application/json")
            .setResponseCode(SC_OK);

    @Override
    public String getBaseUrl() {
        return BASE;
    }

    @Override
    public Optional<MockResponse> handleDelete(@NonNull RecordedRequest request) {
        return Optional.of(OK_RESPONSE);
    }

    @Override
    public Optional<MockResponse> handleGet(@NonNull RecordedRequest request) {
        return Optional.of(OK_RESPONSE);
    }

    @Override
    public Optional<MockResponse> handlePost(@NonNull RecordedRequest request) {
        return Optional.of(OK_RESPONSE);
    }

    @Override
    public Optional<MockResponse> handlePut(@NonNull RecordedRequest request) {
        return Optional.of(OK_RESPONSE);
    }

}
