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
package de.cuioss.portal.configuration.impl;

import de.cuioss.portal.configuration.PortalConfigurationChangeInterceptor;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Interceptor implementation for {@link PortalConfigurationChangeInterceptor}.
 * See the annotation for details.
 *
 * @author Oliver Wolff
 */
@Interceptor
@PortalConfigurationChangeInterceptor
public class PortalConfigurationChangeInterceptorImpl {

    private static final CuiLogger LOGGER = new CuiLogger(PortalConfigurationChangeInterceptorImpl.class);

    /**
     * @param invocationContext
     * @return the proceeded invocation context
     * @throws Exception
     */
    @AroundInvoke
    public Object around(final InvocationContext invocationContext) throws Exception {
        var method = invocationContext.getMethod();
        LOGGER.trace("around {}", method);
        var filterConfig = requireNonNull(method.getAnnotation(PortalConfigurationChangeInterceptor.class));
        List<String> filterKeys = Arrays.asList(filterConfig.key());
        List<String> startsWithKeys = Arrays.asList(filterConfig.keyPrefix());
        if (filterKeys.isEmpty() && startsWithKeys.isEmpty()) {
            throw new IllegalStateException(
                "No filterKey found, offending type is: " + invocationContext.getTarget().getClass());
        }
        var deltaMap = accessMap(invocationContext);
        for (String key : filterKeys) {
            if (deltaMap.containsKey(key)) {
                LOGGER.trace("deltaMap {} contains key {}", deltaMap, key);
                return invocationContext.proceed();
            }
        }
        for (String key : startsWithKeys) {
            if (!ConfigurationHelper.getFilteredPropertyMap(deltaMap, key, false).isEmpty()) {
                LOGGER.trace("deltaMap {} contains startsWithKeys {}", deltaMap, key);
                return invocationContext.proceed();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> accessMap(InvocationContext invocationContext) {
        for (Object param : invocationContext.getParameters()) {
            if (param instanceof Map) {
                return (Map<String, String>) param;
            }
        }
        throw new IllegalStateException(
            "Invalid Method-parameter found, expected at least one of type Map<String, String>, but was "
                + Arrays.toString(invocationContext.getParameters()));
    }
}
