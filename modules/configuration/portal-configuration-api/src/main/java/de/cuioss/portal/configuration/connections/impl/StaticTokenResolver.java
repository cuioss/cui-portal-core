package de.cuioss.portal.configuration.connections.impl;

import de.cuioss.portal.configuration.connections.TokenResolver;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Basic Implementation of {@link TokenResolver} used for static tokens that do not change for a
 * specific user.
 *
 * @author Oliver Wolff
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString(of = "key") // Only key, because Token may be considered as sensitive data
public class StaticTokenResolver implements TokenResolver {

    private static final long serialVersionUID = 7523596484663692845L;

    @Getter
    private final String key;

    private final String token;

    @Override
    public String resolve() {
        return token;
    }

}
