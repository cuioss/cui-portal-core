package de.cuioss.portal.configuration.impl.producer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.application.PortalProjectStageProducer;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.PortalConfigurationMock;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import de.cuioss.uimodel.application.CuiProjectStage;
import lombok.Getter;

@EnableAutoWeld
@EnablePortalConfigurationLocal
@AddBeanClasses({ PortalProjectStageImpl.class })
class PortalProjectStageProducerImplTest implements ShouldHandleObjectContracts<PortalProjectStageImpl> {

    @Inject
    @Getter
    private PortalProjectStageImpl underTest;

    @Inject
    @PortalConfigurationSource
    private PortalConfigurationMock configuration;

    @Inject
    @PortalProjectStageProducer
    private Provider<CuiProjectStage> stageUnderTest;

    @Test
    void shouldDetermineProductionStageAsDefault() {

        configuration.fireEvent();
        assertTrue(underTest.getProjectStage().isProduction());
        assertFalse(stageUnderTest.get().isDevelopment());
        assertTrue(stageUnderTest.get().isProduction());

    }

    @Test
    void shouldChangeProductionStage() {
        assertFalse(stageUnderTest.get().isDevelopment());
        assertTrue(stageUnderTest.get().isProduction());
        assertTrue(underTest.getProjectStage().isProduction());

        // No changes on invalid parameter
        configuration.fireEvent();
        assertFalse(stageUnderTest.get().isDevelopment());
        assertTrue(stageUnderTest.get().isProduction());
        assertTrue(underTest.getProjectStage().isProduction());

        configuration.fireEvent(PortalConfigurationKeys.PORTAL_STAGE, "development");

        assertTrue(underTest.getProjectStage().isDevelopment());
        assertTrue(stageUnderTest.get().isDevelopment());
        assertFalse(stageUnderTest.get().isProduction());

    }

    @Test
    void shouldGracefullyHandleInvalidConfiguration() {
        configuration.fireEvent(PortalConfigurationKeys.PORTAL_STAGE, "heythere");

        assertFalse(stageUnderTest.get().isDevelopment());
        assertTrue(stageUnderTest.get().isProduction());
        assertTrue(underTest.getProjectStage().isProduction());
    }
}
