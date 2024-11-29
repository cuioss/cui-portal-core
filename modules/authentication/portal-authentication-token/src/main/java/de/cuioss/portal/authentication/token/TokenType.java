/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
