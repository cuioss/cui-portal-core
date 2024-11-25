package de.cuioss.portal.authentication.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Defines a specific token-type.
 * Currently, they are derived / work for keycloak
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum TokenType {

    ACCESS_TOKEN("Bearer"), ID_TOKEN("ID"), REFRESH_TOKEN("Refresh"), UNKNOWN("unknown");

    @Getter
    private final String typeClaimName;

    public static TokenType fromTypClaim(String typeClaimName) {
        for (TokenType tokenType : TokenType.values()) {
            if (tokenType.typeClaimName.equalsIgnoreCase(typeClaimName)) {
                return tokenType;
            }
        }
        return UNKNOWN;
    }
}
