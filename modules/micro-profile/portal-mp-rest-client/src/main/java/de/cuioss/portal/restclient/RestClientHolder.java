package de.cuioss.portal.restclient;

import de.cuioss.uimodel.service.OptionalService;
import de.cuioss.uimodel.service.ServiceState;
import lombok.RequiredArgsConstructor;

/**
 * Wraps a concrete rest client implementation of T and provides some
 * convenience methods.
 *
 * @author Matthias Walliczek
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
