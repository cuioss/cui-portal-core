package de.cuioss.portal.restclient;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import de.cuioss.portal.configuration.connections.TokenResolver;
import lombok.RequiredArgsConstructor;

/**
 * Client filter that will do token authentication.  You must allocate it and then register it with the Client or
 * WebTarget.
 */
@RequiredArgsConstructor
public class TokenFilter implements ClientRequestFilter {

    private final TokenResolver tokenResolver;

    @Override
    public void filter(ClientRequestContext requestContext) {
        if (null != tokenResolver) {
            requestContext.getHeaders().putSingle(tokenResolver.getKey(), tokenResolver.resolve());
        }
    }
}
