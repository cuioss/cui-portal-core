package de.cuioss.portal.configuration.impl.support;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.PortalConfigurationChangeInterceptor;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("javadoc")
@ApplicationScoped
public class InvalidFilterConfiguration {

    @Getter
    @Setter
    private Map<String, String> deltaMap = new HashMap<>();

    @PortalConfigurationChangeInterceptor(key = "NotThere")
    void configurationChangeEventListener(@Observes @PortalConfigurationChangeEvent final String invalidParameter) {
        System.out.println(invalidParameter);
    }

    public void reset() {
        deltaMap = new HashMap<>();
    }

    public boolean isEmpty() {
        return deltaMap.isEmpty();
    }
}
