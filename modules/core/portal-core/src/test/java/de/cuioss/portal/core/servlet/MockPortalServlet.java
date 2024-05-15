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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

@ApplicationScoped
public class MockPortalServlet extends AbstractPortalServlet {

    private static final long serialVersionUID = -6932935998287415457L;

    @Getter
    @Setter
    private boolean enabled = false;

    @Getter
    @Setter
    private boolean loggedInUserRequired = false;

    @Getter
    private final Collection<String> requiredRoles = new ArrayList<>();

    @Setter
    private IOException throwMe = null;

    @Override
    public void executeDoGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (null != throwMe) {
            throw throwMe;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    public void reset() {
        throwMe = null;
        enabled = false;
        loggedInUserRequired = false;
        requiredRoles.clear();
    }
}
