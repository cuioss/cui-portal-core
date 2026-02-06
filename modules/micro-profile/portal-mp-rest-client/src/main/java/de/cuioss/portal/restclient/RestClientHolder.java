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
package de.cuioss.portal.restclient;

import de.cuioss.uimodel.service.OptionalService;
import de.cuioss.uimodel.service.ServiceState;
import lombok.RequiredArgsConstructor;

/**
 * Holder for REST client instances in the Portal environment.
 * Manages the lifecycle and configuration of client instances.
 *
 * @param <T> The type of the REST client interface
 *
 * @see PortalRestClient
 * @see RestClientProducer
 */
@RequiredArgsConstructor
public class RestClientHolder<T> implements OptionalService {

    private final T restClient;

    /**
     * @return the REST client
     * @throws IllegalStateException if service is not available
     */
    public T get() {
        if (isServiceAvailable()) {
            return restClient;
        }
        throw new IllegalStateException("Service not available");
    }

    @Override
    public ServiceState getServiceState() {
        return null != restClient ? ServiceState.ACTIVE : ServiceState.NOT_CONFIGURED;
    }
}
