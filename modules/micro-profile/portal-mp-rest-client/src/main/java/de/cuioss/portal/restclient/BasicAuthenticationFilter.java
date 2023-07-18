package de.cuioss.portal.restclient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

import lombok.NonNull;

/**
 * Filter to set basic authentication header.
 */
public class BasicAuthenticationFilter implements ClientRequestFilter {

    private final String headerValue;

    public BasicAuthenticationFilter(@NonNull String username, @NonNull String password) {
        headerValue = "Basic "
                + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.US_ASCII));
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, headerValue);

    }
}
