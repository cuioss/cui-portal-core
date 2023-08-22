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
package de.cuioss.portal.core.listener;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.test.mock.MockHttpServletRequest;
import org.apache.myfaces.test.mock.MockHttpServletResponse;
import org.easymock.EasyMock;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.core.listener.literal.ServletInitialized;

@EnableAutoWeld
class RequestResponseEventFilterTest {

    private boolean initRequest;
    private boolean destroyRequest;

    private boolean initResponse;
    private boolean destroyResponse;

    @BeforeEach
    void restEventResults() {
        initRequest = false;
        destroyRequest = false;
        initResponse = false;
        destroyResponse = false;
    }

    @Test
    void shouldFilter() throws IOException, ServletException {
        var filter = new RequestResponseLifecycleFilter();
        FilterChain chain = EasyMock.createNiceMock(FilterChain.class);

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), chain);
        assertTrue(destroyRequest);
        assertTrue(destroyResponse);
        assertTrue(initRequest);
        assertTrue(initResponse);
    }

    void initRequest(@Observes @ServletInitialized HttpServletRequest request) {
        initRequest = true;
    }

    void destroyRequest(@Observes @ServletInitialized HttpServletRequest request) {
        destroyRequest = true;
    }

    void initResponse(@Observes @ServletInitialized HttpServletResponse request) {
        initResponse = true;
    }

    void destroyResponse(@Observes @ServletInitialized HttpServletResponse request) {
        destroyResponse = true;
    }

}
