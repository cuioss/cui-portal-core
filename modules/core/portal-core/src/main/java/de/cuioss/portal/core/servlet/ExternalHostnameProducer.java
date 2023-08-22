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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import de.cuioss.tools.logging.CuiLogger;

/**
 * To retrieve the external hostname including port of the request
 */
@ApplicationScoped
public class ExternalHostnameProducer {

    private static final CuiLogger log = new CuiLogger(ExternalHostnameProducer.class);

    @Inject
    private Provider<HttpServletRequest> httpServletRequest;

    @Produces
    @Dependent
    @Named
    @CuiExternalHostname
    String getExternalHostname() {
        var request = httpServletRequest.get();
        var url = request.getRequestURL();
        var uri = request.getRequestURI();
        var hostname = url.substring(0, url.indexOf(uri));
        log.debug("Resolved hostname: {}", hostname);
        return hostname;
    }

}
