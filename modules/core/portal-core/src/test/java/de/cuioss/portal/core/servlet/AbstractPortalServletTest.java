package de.cuioss.portal.core.servlet;

import static de.cuioss.portal.core.PortalCoreLogMessages.SERVLET;
import static de.cuioss.test.juli.LogAsserts.assertSingleLogMessagePresentContaining;
import static jakarta.servlet.http.HttpServletResponse.*;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.Serial;

import de.cuioss.portal.core.test.support.PortalUserProducerMock;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.collect.CollectionLiterals;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.easymock.EasyMock;
import org.jboss.weld.junit5.auto.EnableAlternatives;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@EnableTestLogger
@EnableAutoWeld
@EnableAlternatives(PortalUserProducerMock.class)
@DisplayName("Testing AbstractPortalServlet functionality")
class AbstractPortalServletTest {

    private static final String ROLE1 = "role1";
    private static final String ROLE2 = "role2";

    @Inject
    private MockPortalServlet underTest;

    @Inject
    private PortalUserProducerMock userProducer;

    @BeforeEach
    void beforeEach() {
        underTest.reset();
    }

    @Nested
    @DisplayName("Basic Servlet State Tests")
    class BasicServletStateTests {
        
        @Test
        @DisplayName("Should handle enabled/disabled state")
        void shouldHandleEnabled() throws Exception {
            underTest.setEnabled(false);
            verifyErrorCode(SC_SERVICE_UNAVAILABLE);
            underTest.setEnabled(true);
            verifyCallOk();
        }
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {
        
        @Test
        @DisplayName("Should require user authentication when configured")
        void shouldHandleAuthenticationRequired() throws Exception {
            underTest.setEnabled(true);
            underTest.setLoggedInUserRequired(true);
            userProducer.authenticated(false);
            verifyErrorCode(HttpServletResponse.SC_UNAUTHORIZED);
            assertSingleLogMessagePresentContaining(TestLogLevel.WARN, 
                SERVLET.WARN.USER_NOT_LOGGED_IN.resolveIdentifierString());
        }
    }

    @Nested
    @DisplayName("Role-Based Access Control Tests")
    class RoleBasedAccessTests {
        
        @BeforeEach
        void setup() {
            underTest.setEnabled(true);
            underTest.setLoggedInUserRequired(true);
            userProducer.authenticated(true);
        }

        @Test
        @DisplayName("Should allow access with correct single role")
        void shouldHandleRoleRequiredWithSingleRole() throws Exception {
            userProducer.roles(CollectionLiterals.immutableList(ROLE1));
            underTest.getRequiredRoles().add(ROLE1);
            verifyCallOk();
        }

        @Test
        @DisplayName("Should deny access with wrong role")
        void shouldHandleRoleRequiredWithWrongRole() throws Exception {
            userProducer.roles(CollectionLiterals.immutableList(ROLE1));
            underTest.getRequiredRoles().add(ROLE2);
            verifyErrorCode(SC_FORBIDDEN);
            assertSingleLogMessagePresentContaining(TestLogLevel.WARN, 
                SERVLET.WARN.USER_MISSING_ROLES.resolveIdentifierString());
        }

        @Test
        @DisplayName("Should deny access with no roles")
        void shouldHandleRoleRequiredWithNoRole() throws Exception {
            userProducer.roles(CollectionLiterals.immutableList());
            underTest.getRequiredRoles().add(ROLE1);
            verifyErrorCode(SC_FORBIDDEN);
            assertSingleLogMessagePresentContaining(TestLogLevel.WARN, 
                SERVLET.WARN.USER_MISSING_ROLES.resolveIdentifierString());
        }
    }

    @Nested
    @DisplayName("Exception Tests")
    class ExceptionTests {
        
        @Test
        @DisplayName("Should handle exception")
        void shouldHandleException() throws Exception {
            underTest.setEnabled(true);
            underTest.setThrowMe(new IOException("boom"));
            verifyErrorCode(SC_INTERNAL_SERVER_ERROR);
            assertSingleLogMessagePresentContaining(TestLogLevel.ERROR, 
                SERVLET.ERROR.REQUEST_PROCESSING_ERROR.resolveIdentifierString());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {
        
        @Test
        @DisplayName("Should provide sensible defaults")
        void shouldProvideSensibleDefaults() {
            AbstractPortalServlet servlet = new AbstractPortalServlet() {

                @Serial
                private static final long serialVersionUID = -3472474929349372042L;

                @Override
                public void executeDoGet(HttpServletRequest request, HttpServletResponse response) 
                        throws IOException {
                    response.setStatus(SC_OK);
                }
            };

            assertFalse(servlet.isLoggedInUserRequired());
            assertTrue(servlet.isEnabled());
            assertNotNull(servlet.getRequiredRoles());
            assertTrue(servlet.getRequiredRoles().isEmpty());
        }
    }

    private void verifyCallOk() throws Exception {
        verifyErrorCode(SC_OK);
    }

    private void verifyErrorCode(int expectedCode) throws Exception {
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);
        HttpServletResponse response = createNiceMock(HttpServletResponse.class);
        expect(request.getMethod()).andReturn("GET");
        response.setStatus(expectedCode);
        replay(request, response);
        underTest.service(request, response);
        verify(request, response);
    }
}
