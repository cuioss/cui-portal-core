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
package de.cuioss.portal.common.stage;

import de.cuioss.portal.common.PortalCommonLogMessages;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static de.cuioss.test.juli.LogAsserts.assertLogMessagePresentContaining;
import static org.junit.jupiter.api.Assertions.*;

@EnableTestLogger(error = ProjectStage.class)
@DisplayName("ProjectStage Tests")
class ProjectStageTest {

    @Nested
    @DisplayName("fromString resolution")
    class FromStringTests {

        @Test
        @DisplayName("Should resolve all stages by name (case-insensitive)")
        void shouldResolveAllStages() {
            assertEquals(ProjectStage.DEVELOPMENT, ProjectStage.fromString("DEVELOPMENT"));
            assertEquals(ProjectStage.DEVELOPMENT, ProjectStage.fromString("development"));
            assertEquals(ProjectStage.DEVELOPMENT, ProjectStage.fromString("Development"));

            assertEquals(ProjectStage.TEST, ProjectStage.fromString("TEST"));
            assertEquals(ProjectStage.TEST, ProjectStage.fromString("test"));
            assertEquals(ProjectStage.TEST, ProjectStage.fromString("Test"));

            assertEquals(ProjectStage.PRODUCTION, ProjectStage.fromString("PRODUCTION"));
            assertEquals(ProjectStage.PRODUCTION, ProjectStage.fromString("production"));
            assertEquals(ProjectStage.PRODUCTION, ProjectStage.fromString("Production"));
        }

        @Test
        @DisplayName("Should default to PRODUCTION for null input and log error")
        void shouldDefaultToProductionForNull() {
            assertEquals(ProjectStage.PRODUCTION, ProjectStage.fromString(null));
            assertLogMessagePresentContaining(TestLogLevel.ERROR,
                    PortalCommonLogMessages.ERROR.INVALID_STAGE.resolveIdentifierString());
        }

        @Test
        @DisplayName("Should default to PRODUCTION for unknown stage and log error")
        void shouldDefaultToProductionForUnknown() {
            assertEquals(ProjectStage.PRODUCTION, ProjectStage.fromString("INVALID"));
            assertLogMessagePresentContaining(TestLogLevel.ERROR,
                    PortalCommonLogMessages.ERROR.INVALID_STAGE.resolveIdentifierString());
        }
    }

    @Nested
    @DisplayName("Stage predicates")
    class StagePredicateTests {

        @Test
        @DisplayName("DEVELOPMENT should report correct predicates")
        void shouldReportDevelopmentPredicates() {
            assertTrue(ProjectStage.DEVELOPMENT.isDevelopment());
            assertFalse(ProjectStage.DEVELOPMENT.isTest());
            assertFalse(ProjectStage.DEVELOPMENT.isProduction());
        }

        @Test
        @DisplayName("TEST should report correct predicates")
        void shouldReportTestPredicates() {
            assertFalse(ProjectStage.TEST.isDevelopment());
            assertTrue(ProjectStage.TEST.isTest());
            assertFalse(ProjectStage.TEST.isProduction());
        }

        @Test
        @DisplayName("PRODUCTION should report correct predicates")
        void shouldReportProductionPredicates() {
            assertFalse(ProjectStage.PRODUCTION.isDevelopment());
            assertFalse(ProjectStage.PRODUCTION.isTest());
            assertTrue(ProjectStage.PRODUCTION.isProduction());
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTests {

        @Test
        @DisplayName("Should return lowercase stage name")
        void shouldReturnLowercaseToString() {
            assertEquals("development", ProjectStage.DEVELOPMENT.toString());
            assertEquals("test", ProjectStage.TEST.toString());
            assertEquals("production", ProjectStage.PRODUCTION.toString());
        }
    }
}
