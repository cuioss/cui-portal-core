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
package de.cuioss.portal.core.servlet;

import de.cuioss.portal.core.test.support.PortalUserProducerMock;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.collect.CollectionLiterals;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.easymock.EasyMock;
import org.jboss.weld.junit5.auto.EnableAlternatives;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serial;
import java.util.Collections;

import static de.cuioss.portal.core.PortalCoreLogMessages.SERVLET;
import static de.cuioss.test.juli.LogAsserts.assertSingleLogMessagePresentContaining;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableTestLogger
@EnableAutoWeld
@EnableAlternatives(PortalUserProducerMock.class)
class AbstractPortalServletTest {

    private static final String ROLE1 = "role1";
    private static final String ROLE2 = "role2";

    @Inject
    private MockPortalServlet underTest;

    @Inject
    private PortalUserProducerMock userProducer;

    void beforeAll() {
        underTest.reset();
    }

    @Test
    void shouldHandleEnabled() throws Exception {
        underTest.setEnabled(false);
        verifyErrorCode(SC_SERVICE_UNAVAILABLE);
        underTest.setEnabled(true);
        verifyCallOk();
    }

    @Test
    void shouldHandleAuthenticationRequired() throws Exception {
        underTest.setEnabled(true);
        underTest.setLoggedInUserRequired(true);
        userProducer.authenticated(false);
        verifyErrorCode(HttpServletResponse.SC_UNAUTHORIZED);
        assertSingleLogMessagePresentContaining(TestLogLevel.WARN, SERVLET.WARN.USER_NOT_LOGGED_IN.resolveIdentifierString());
    }

    @Test
    void shouldHandleRoleRequiredWithSingleRole() throws Exception {
        underTest.setEnabled(true);

        underTest.setLoggedInUserRequired(true);
        userProducer.authenticated(true);
        userProducer.roles(CollectionLiterals.immutableList(ROLE1));
        underTest.getRequiredRoles().add(ROLE1);
        verifyCallOk();
    }

    @Test
    void shouldHandleRoleRequiredWithWrongRole() throws Exception {
        underTest.setEnabled(true);

        underTest.setLoggedInUserRequired(true);
        userProducer.authenticated(true);
        userProducer.roles(CollectionLiterals.immutableList(ROLE1));
        underTest.getRequiredRoles().add(ROLE2);
        verifyErrorCode(SC_FORBIDDEN);
        assertSingleLogMessagePresentContaining(TestLogLevel.WARN, SERVLET.WARN.USER_MISSING_ROLES.resolveIdentifierString());
    }

    @Test
    void shouldHandleRoleRequiredWithNoRole() throws Exception {
        underTest.setEnabled(true);

        underTest.setLoggedInUserRequired(true);
        userProducer.authenticated(true);
        userProducer.roles(Collections.emptyList());
        verifyCallOk();
    }

    @Test
    void shouldHandleException() throws Exception {
        underTest.setEnabled(true);
        underTest.setThrowMe(new IOException("boom"));
        verifyErrorCode(SC_INTERNAL_SERVER_ERROR);
        assertSingleLogMessagePresentContaining(TestLogLevel.ERROR, SERVLET.ERROR.REQUEST_PROCESSING_ERROR.resolveIdentifierString());
    }

    @Test
    void shouldProvideSensibleDefaults() {
        AbstractPortalServlet servlet = new AbstractPortalServlet() {

            @Serial
            private static final long serialVersionUID = -3472474929349372042L;

            @Override
            public void executeDoGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.setStatus(SC_OK);
            }
        };

        assertFalse(servlet.isLoggedInUserRequired());
        assertTrue(servlet.isEnabled());
        assertNotNull(servlet.getRequiredRoles());
        assertTrue(servlet.getRequiredRoles().isEmpty());
    }

    private void verifyErrorCode(int errorCode) throws Exception {
        HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
        response.setStatus(errorCode);
        replay(response);
        underTest.doGet(mock(HttpServletRequest.class), response);
        verify(response);
    }

    private void verifyCallOk() throws Exception {
        HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
        response.setStatus(SC_OK);
        replay(response);
        underTest.doGet(mock(HttpServletRequest.class), response);
        verify(response);
    }
}
