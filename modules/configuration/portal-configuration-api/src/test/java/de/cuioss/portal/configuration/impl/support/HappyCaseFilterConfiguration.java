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
public class HappyCaseFilterConfiguration {

    public static final String PREFIX = "happy.case.filter.";
    public static final String FIRST_KEY = PREFIX + "element";
    public static final String SECOND_KEY = PREFIX + "element2";
    public static final String FILTER_VALUE_1 = "singleFilterHappyValue1";
    public static final String FILTER_VALUE_2 = "singleFilterHappyValue2";

    public static final Map<String, String> HAPPY_CASE_MAP =
        immutableMap(FIRST_KEY, FILTER_VALUE_1, SECOND_KEY, FILTER_VALUE_2);

    @Getter
    @Setter
    private Map<String, String> deltaMap = new HashMap<>();

    @PortalConfigurationChangeInterceptor(keyPrefix = PREFIX)
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
