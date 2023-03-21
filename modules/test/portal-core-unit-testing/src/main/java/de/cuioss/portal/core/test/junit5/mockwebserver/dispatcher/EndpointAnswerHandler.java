package de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;
import okhttp3.mockwebserver.MockResponse;

/**
 * Helper class simplifying the handling / resetting for responses.
 *
 * @author Oliver Wolff
 *
 */
public class EndpointAnswerHandler {

    /**
     * Empty response transporting {@link HttpServletResponse#SC_FORBIDDEN}
     */
    public static final MockResponse RESPONSE_FORBIDDEN =
        new MockResponse().setResponseCode(HttpServletResponse.SC_FORBIDDEN);

    /**
     * Empty response transporting {@link HttpServletResponse#SC_UNAUTHORIZED}
     */
    public static final MockResponse RESPONSE_UNAUTHORIZED =
        new MockResponse().setResponseCode(HttpServletResponse.SC_UNAUTHORIZED);

    /**
     * Empty response transporting {@link HttpServletResponse#SC_OK}
     */
    public static final MockResponse RESPONSE_OK =
        new MockResponse().setResponseCode(HttpServletResponse.SC_OK);

    /**
     * Empty response transporting {@link HttpServletResponse#SC_NO_CONTENT}
     */
    public static final MockResponse RESPONSE_NO_CONTENT =
        new MockResponse().setResponseCode(HttpServletResponse.SC_NO_CONTENT);

    /**
     * Empty response transporting {@link HttpServletResponse#SC_NOT_FOUND}
     */
    public static final MockResponse RESPONSE_NOT_FOUND =
        new MockResponse().setResponseCode(HttpServletResponse.SC_NOT_FOUND);

    /**
     * Empty response transporting {@link HttpServletResponse#SC_NOT_IMPLEMENTED}
     */
    public static final MockResponse RESPONSE_NOT_IMPLEMENTED =
        new MockResponse().setResponseCode(HttpServletResponse.SC_NOT_IMPLEMENTED);

    /**
     * Empty response transporting {@link HttpServletResponse#SC_CREATED}
     */
    public static final MockResponse RESPONSE_CREATED =
        new MockResponse().setResponseCode(HttpServletResponse.SC_CREATED);

    /**
     * Empty response transporting {@link HttpServletResponse#SC_MOVED_PERMANENTLY}
     */
    public static final MockResponse RESPONSE_MOVED_PERMANENTLY =
        new MockResponse().setResponseCode(HttpServletResponse.SC_MOVED_PERMANENTLY);

    /**
     * Empty response transporting {@link HttpServletResponse#SC_MOVED_TEMPORARILY}
     */
    public static final MockResponse RESPONSE_MOVED_TEMPORARILY =
        new MockResponse().setResponseCode(HttpServletResponse.SC_MOVED_TEMPORARILY);

    @Getter
    @Setter
    private MockResponse defaultResponse;

    @Getter
    private final HttpMethodMapper httpMethod;

    @Getter
    @Setter
    private MockResponse response;

    /**
     * Default Constructor
     *
     * @param defaultResponse
     * @param httpMethod
     */
    public EndpointAnswerHandler(MockResponse defaultResponse, HttpMethodMapper httpMethod) {
        this.httpMethod = httpMethod;
        this.defaultResponse = defaultResponse;
    }

    /**
     * @return the currently configured response
     */
    public Optional<MockResponse> respond() {
        return Optional.ofNullable(response);
    }

    /**
     * Resets the current answer to {@link #getDefaultResponse()}
     *
     * @return The current instance of this Handler
     */
    public EndpointAnswerHandler resetToDefaultResponse() {
        response = defaultResponse;
        return this;
    }

    /**
     * Sets the current answer to {@link #RESPONSE_FORBIDDEN}
     *
     * @return The current instance of this Handler
     */
    public EndpointAnswerHandler respondForbidden() {
        response = RESPONSE_FORBIDDEN;
        return this;
    }

    /**
     * Sets the current answer to {@link #RESPONSE_UNAUTHORIZED}
     *
     * @return The current instance of this Handler
     */
    public EndpointAnswerHandler respondUnauthorized() {
        response = RESPONSE_UNAUTHORIZED;
        return this;
    }

    /**
     * Sets the current answer to {@link #RESPONSE_OK}
     *
     * @return The current instance of this Handler
     */
    public EndpointAnswerHandler respondOk() {
        response = RESPONSE_OK;
        return this;
    }

    /**
     * Sets the current answer to {@link #RESPONSE_NO_CONTENT}
     *
     * @return The current instance of this Handler
     */
    public EndpointAnswerHandler respondNoContent() {
        response = RESPONSE_NO_CONTENT;
        return this;
    }

    /**
     * Sets the current answer to {@link #RESPONSE_NOT_FOUND}
     *
     * @return The current instance of this Handler
     */
    public EndpointAnswerHandler respondNotFound() {
        response = RESPONSE_NOT_FOUND;
        return this;
    }

    /**
     * Sets the current answer to {@link #RESPONSE_NOT_IMPLEMENTED}
     *
     * @return The current instance of this Handler
     */
    public EndpointAnswerHandler respondNotImplemented() {
        response = RESPONSE_NOT_IMPLEMENTED;
        return this;
    }

    /**
     * Sets the current answer to {@link #RESPONSE_CREATED}
     *
     * @return The current instance of this Handler
     */
    public EndpointAnswerHandler respondCreated() {
        response = RESPONSE_CREATED;
        return this;
    }

    /**
     * Sets the current answer to {@link #RESPONSE_MOVED_PERMANENTLY}
     *
     * @return The current instance of this Handler
     */
    public EndpointAnswerHandler respondMovedPermanently() {
        response = RESPONSE_MOVED_PERMANENTLY;
        return this;
    }

    /**
     * Sets the current answer to {@link #RESPONSE_MOVED_TEMPORARILY}
     *
     * @return The current instance of this Handler
     */
    public EndpointAnswerHandler respondMovedTemporarily() {
        response = RESPONSE_MOVED_TEMPORARILY;
        return this;
    }

    /**
     * @return An {@link EndpointAnswerHandler} initialized with {@link #RESPONSE_OK}
     */
    public static EndpointAnswerHandler forPositiveGetRequest() {
        return new EndpointAnswerHandler(RESPONSE_OK, HttpMethodMapper.GET).resetToDefaultResponse();
    }

    /**
     * @return An {@link EndpointAnswerHandler} initialized with {@link #RESPONSE_OK}
     */
    public static EndpointAnswerHandler forPositivePostRequest() {
        return new EndpointAnswerHandler(RESPONSE_OK, HttpMethodMapper.POST).resetToDefaultResponse();
    }

    /**
     * @return An {@link EndpointAnswerHandler} initialized with {@link #RESPONSE_CREATED}
     */
    public static EndpointAnswerHandler forPositivePutRequest() {
        return new EndpointAnswerHandler(RESPONSE_CREATED, HttpMethodMapper.PUT).resetToDefaultResponse();
    }

    /**
     * @return An {@link EndpointAnswerHandler} initialized with {@link #RESPONSE_NO_CONTENT}
     */
    public static EndpointAnswerHandler forPositiveDeleteRequest() {
        return new EndpointAnswerHandler(RESPONSE_NO_CONTENT, HttpMethodMapper.DELETE).resetToDefaultResponse();
    }

    /**
     * @return An {@link EndpointAnswerHandler} resulting {@link #respond()} to return
     *         {@link Optional#empty()}
     */
    public static EndpointAnswerHandler noContent(HttpMethodMapper httpMethod) {
        return new EndpointAnswerHandler(null, httpMethod);
    }
}
