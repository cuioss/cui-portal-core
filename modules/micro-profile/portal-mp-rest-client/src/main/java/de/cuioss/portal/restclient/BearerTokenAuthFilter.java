package de.cuioss.portal.restclient;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

import de.cuioss.tools.string.MoreStrings;
import lombok.RequiredArgsConstructor;

/**
 * Client filter that will do token authentication.  You must allocate it and then register it with the Client or
 * WebTarget.
 */
@RequiredArgsConstructor
public class BearerTokenAuthFilter implements ClientRequestFilter {

    private final String token;

    @Override
    public void filter(ClientRequestContext requestContext) {
        if (!MoreStrings.isEmpty(token)) {
            requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
    }
}
