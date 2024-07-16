/**
 *
 */
package de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher;

import static de.cuioss.tools.collect.CollectionLiterals.mutableSet;
import static de.cuioss.tools.collect.CollectionLiterals.mutableSortedSet;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import mockwebserver3.MockResponse;
import mockwebserver3.RecordedRequest;

/**
 * Base class returning sensible return positive values for all supported
 * methods
 *
 * @author Oliver Wolff
 *
 */
@RequiredArgsConstructor
public class BaseAllAcceptDispatcher implements ModuleDispatcherElement {

    @Getter
    private final String baseUrl;

    @Getter
    private final EndpointAnswerHandler getResult = EndpointAnswerHandler.forPositiveGetRequest();

    @Getter
    private final EndpointAnswerHandler postResult = EndpointAnswerHandler.forPositivePostRequest();

    @Getter
    private final EndpointAnswerHandler putResult = EndpointAnswerHandler.forPositivePutRequest();

    @Getter
    private final EndpointAnswerHandler deleteResult = EndpointAnswerHandler.forPositiveDeleteRequest();

    /**
     * Resets all contained {@link EndpointAnswerHandler}
     */
    public void reset() {
        getResult.resetToDefaultResponse();
        postResult.resetToDefaultResponse();
        putResult.resetToDefaultResponse();
        deleteResult.resetToDefaultResponse();
    }

    @Override
    public Optional<MockResponse> handleGet(@NonNull RecordedRequest request) {
        return getResult.respond();
    }

    @Override
    public Optional<MockResponse> handlePost(@NonNull RecordedRequest request) {
        return postResult.respond();
    }

    @Override
    public Optional<MockResponse> handleDelete(@NonNull RecordedRequest request) {
        return deleteResult.respond();
    }

    @Override
    public Optional<MockResponse> handlePut(@NonNull RecordedRequest request) {
        return putResult.respond();
    }

    /**
     * Sets the result for a certain method
     *
     * @param mapper       One or more mapper to identify the corresponding
     *                     {@link HttpMethodMapper}
     * @param mockResponse maybe null
     */
    public void setMethodToResult(MockResponse mockResponse, HttpMethodMapper... mapper) {
        for (HttpMethodMapper element : mapper) {
            switch (element) {
            case GET:
                getResult.setResponse(mockResponse);
                break;
            case POST:
                postResult.setResponse(mockResponse);
                break;
            case PUT:
                putResult.setResponse(mockResponse);
                break;
            case DELETE:
                deleteResult.setResponse(mockResponse);
                break;
            default:
                throw new IllegalArgumentException("Unknown method: " + Arrays.toString(mapper));
            }
        }
    }

    /**
     * Sets the result for all but the given
     *
     * @param mapper       One or more mapper to identify the corresponding
     *                     {@link HttpMethodMapper}
     * @param mockResponse maybe null
     */
    public void setAllButGivenMethodToResult(MockResponse mockResponse, HttpMethodMapper... mapper) {
        Set<HttpMethodMapper> all = mutableSet(HttpMethodMapper.values());
        all.removeAll(mutableSortedSet(mapper));
        setMethodToResult(mockResponse, all.toArray(new HttpMethodMapper[0]));
    }
}
