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
    private Provider<CuiProjectStage> stageUnderTest;

    @Test
    void shouldDetermineProductionStageAsDefault() {

        configuration.fireEvent();
        assertTrue(underTest.getProjectStage().isProduction());
        assertFalse(stageUnderTest.get().isDevelopment());
        assertTrue(stageUnderTest.get().isProduction());

    }

    @Test
    void shouldGracefullyHandleInvalidConfiguration() {
        configuration.fireEvent(PortalConfigurationKeys.PORTAL_STAGE, "heythere");

        assertFalse(stageUnderTest.get().isDevelopment());
        assertTrue(stageUnderTest.get().isProduction());
        assertTrue(underTest.getProjectStage().isProduction());
    }
}
