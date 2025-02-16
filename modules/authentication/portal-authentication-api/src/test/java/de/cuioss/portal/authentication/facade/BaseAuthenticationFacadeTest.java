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
package de.cuioss.portal.authentication.facade;

import static de.cuioss.test.generator.Generators.nonEmptyStrings;
import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.valueobjects.api.property.PropertyReflectionConfig;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import jakarta.inject.Inject;
import lombok.Getter;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

@EnableAutoWeld
@AddBeanClasses({MockBaseAuthenticationFacade.class, MockPortalUserEnricher.class})
@PropertyReflectionConfig(skip = true)
class BaseAuthenticationFacadeTest implements ShouldBeNotNull<MockBaseAuthenticationFacade> {

    private static final TypedGenerator<String> STRING_GENERATOR = nonEmptyStrings();

    @Inject
    @Getter
    private MockBaseAuthenticationFacade underTest;

    @Test
    void shouldEnrichWithSingleEnricher() {
        var displayName = STRING_GENERATOR.next();
        var identifier = STRING_GENERATOR.next();
        underTest.setAuthenticatedUserInfo(new BaseAuthenticatedUserInfo(true, displayName, identifier, null, null));

        var result = underTest.retrieveCurrentAuthenticationContext(null);

        assertNotNull(result);
        assertEquals(displayName, result.getDisplayName());
        assertEquals(identifier, result.getIdentifier());
        assertEquals(1, result.getRoles().size());
        assertEquals("testRole", result.getRoles().get(0));
    }

    @Test
    void shouldHandleNullUserInfo() {
        underTest.setAuthenticatedUserInfo(null);
        var result = underTest.retrieveCurrentAuthenticationContext(null);
        assertNull(result);
    }

    @Test
    void shouldHandleLogout() {
        var result = underTest.logout(null);
        assertFalse(result);
    }
}
