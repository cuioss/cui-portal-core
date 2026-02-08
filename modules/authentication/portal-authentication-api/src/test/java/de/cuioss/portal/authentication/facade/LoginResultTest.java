/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.authentication.facade;

import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.uimodel.nameprovider.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResultTest {

    @Test
    void successShouldBeValid() {
        var user = BaseAuthenticatedUserInfo.builder().authenticated(true).identifier("test").build();
        var result = new LoginResult.Success(user);
        assertTrue(result.isValid());
        assertSame(user, result.authenticatedUserInfo());
    }

    @Test
    void failureShouldNotBeValid() {
        var result = new LoginResult.Failure(new DisplayName("error"), null, null);
        assertFalse(result.isValid());
    }

    @Test
    void successShouldRejectNullUser() {
        assertThrows(NullPointerException.class, () -> new LoginResult.Success(null));
    }

    @Test
    void failureShouldRejectNullErrorReason() {
        assertThrows(NullPointerException.class, () -> new LoginResult.Failure(null, null, null));
    }

    @Test
    void failureShouldAllowNullUsernameAndCause() {
        var result = new LoginResult.Failure(new DisplayName("error"), null, null);
        assertNull(result.username());
        assertNull(result.cause());
        assertEquals("error", result.errorReason().getContent().toString());
    }

    @Test
    void failureShouldRetainAllFields() {
        var cause = new RuntimeException("test cause");
        var result = new LoginResult.Failure(new DisplayName("error"), "testUser", cause);
        assertEquals("testUser", result.username());
        assertSame(cause, result.cause());
    }
}
