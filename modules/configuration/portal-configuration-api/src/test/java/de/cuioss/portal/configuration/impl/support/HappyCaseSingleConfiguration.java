package de.cuioss.portal.configuration.impl.support;

import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;

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
public class HappyCaseSingleConfiguration {

    public static final String PREFIX = "happy.case.configuration.";
    public static final String SINGLE_KEY = PREFIX + "single";
    public static final String SINGLE_VALUE = "singleHappyValue";

    public static final Map<String, String> HAPPY_CASE_MAP =
        immutableMap(SINGLE_KEY, SINGLE_VALUE);

    @Getter
    @Setter
    private Map<String, String> deltaMap = new HashMap<>();

    @PortalConfigurationChangeInterceptor(key = SINGLE_KEY)
    void configurationChangeEventListener(
            @Observes @PortalConfigurationChangeEvent final Map<String, String> deltaMap) {
        this.deltaMap = deltaMap;
    }

    public void reset() {
        deltaMap = new HashMap<>();
    }

    public boolean isEmpty() {
        return deltaMap.isEmpty();
    }
}
