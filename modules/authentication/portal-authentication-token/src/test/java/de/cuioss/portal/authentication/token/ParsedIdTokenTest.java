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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ParsedIdTokenTest {

    @Test
    void shouldHandleValidToken() {
        String initialTokenString = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_ID_TOKEN);

        var parsedIdToken = ParsedIdToken.fromTokenString(initialTokenString, TestTokenProducer.DEFAULT_TOKEN_PARSER);

        assertTrue(parsedIdToken.isPresent());
        assertEquals(parsedIdToken.get().getTokenString(), initialTokenString);
    }

    @Test
    void shouldHandleEmail() {
        String initialTokenString = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_ID_TOKEN);

        var parsedIdToken = ParsedIdToken.fromTokenString(initialTokenString, TestTokenProducer.DEFAULT_TOKEN_PARSER);
        assertTrue(parsedIdToken.isPresent());
        assertEquals("hello@world.com", parsedIdToken.get().getEmail().get());
    }

}
