package de.cuioss.portal.core.test.junit5.mockwebserver;

import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import lombok.Getter;
import lombok.Setter;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@EnableMockWebServer
@EnableTestLogger(debug = MockWebServerExtension.class, rootLevel = TestLogLevel.DEBUG)
class MockWebServerHolderNestingTests implements MockWebServerHolder {

    @Getter
    @Setter
    private MockWebServer mockWebServer;


    @Test
    void shouldProvideServerOnParent() {
        assertNotNull(mockWebServer);
    }

    @Nested
    @DisplayName("Nested Tests With MockWebServer")
    class NestedTestsWithMockWebServer {

        @Test
        void shouldHaveAccessToParentMockWebServer() {
            assertNull(mockWebServer, "Parent server is null. This is expected.");
        }
    }

    @Nested
    @EnableMockWebServer
    @DisplayName("Nested Tests With MockWebServer and a local annotation")
    class NestedTestsWithMockWebServerAndAnnotation {

        @Test
        void shouldHaveAccessToParentMockWebServer() {
            assertNull(mockWebServer, "Parent server is null. This is expected.");
        }
    }

    @Nested
    @EnableMockWebServer
    @DisplayName("Nested Tests With MockWebServer and a local annotation and acting as a MockWebServerHolder")
    class NestedTestsWithMockWebServerAndAnnotationAndHolder implements MockWebServerHolder {

        @Getter
        @Setter
        private MockWebServer mockWebServer;

        @Test
        void shouldHaveAccessToParentMockWebServer() {
            assertNotNull(this.mockWebServer, "Child server should not be null");
        }

        @Nested
        @DisplayName("Third level nested tests")
        class ThirdLevelNestedTests {

            @Test
            void shouldHaveAccessToParentMockWebServer() {
                assertNull(mockWebServer, "Parent server is null. This is expected.");
            }
        }
    }
}
