package de.cuioss.portal.authentication.facade;

import static de.cuioss.test.generator.Generators.nonEmptyStrings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.valueobjects.api.property.PropertyReflectionConfig;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
@AddBeanClasses({ MockBaseAuthenticationFacade.class, MockPortalUserEnricher.class, MockHighPriorityUserEnricher.class })
@PropertyReflectionConfig(skip = true)
class MultipleEnrichersTest implements ShouldBeNotNull<MockBaseAuthenticationFacade> {

    private static final TypedGenerator<String> STRING_GENERATOR = nonEmptyStrings();

    @Inject
    @Getter
    private MockBaseAuthenticationFacade underTest;

    @Test
    void shouldEnrichWithMultipleEnrichers() {
        var displayName = STRING_GENERATOR.next();
        var identifier = STRING_GENERATOR.next();
        underTest.setAuthenticatedUserInfo(new BaseAuthenticatedUserInfo(true, displayName, identifier, null, null));
        
        var result = underTest.retrieveCurrentAuthenticationContext(null);
        
        assertNotNull(result);
        assertEquals(displayName, result.getDisplayName());
        assertEquals(identifier, result.getIdentifier());
        assertEquals(2, result.getRoles().size());
        assertEquals("testRole", result.getRoles().get(0)); // Priority 200 runs first
        assertEquals("highPriorityRole", result.getRoles().get(1)); // Priority 50 runs second
    }
}
