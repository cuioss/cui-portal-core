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

import org.junit.jupiter.api.Test;

import static de.cuioss.portal.authentication.token.TestTokenProducer.REFRESH_TOKEN;
import static de.cuioss.portal.authentication.token.TestTokenProducer.validSignedJWTWithClaims;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ParsedRefreshTokenTest {

    @Test
    void shouldHandleHappyCase() {

        String initialToken = validSignedJWTWithClaims(REFRESH_TOKEN);
        var parsedRefreshToken = ParsedRefreshToken.fromTokenString(initialToken);

        assertEquals(initialToken, parsedRefreshToken.getTokenString());
        assertFalse(parsedRefreshToken.isEmpty());

        assertEquals(TokenType.REFRESH_TOKEN, parsedRefreshToken.getType() );
    }
}