package de.cuioss.portal.configuration.impl.source;

import lombok.Getter;
import lombok.Setter;

public class LowReloadableConfigSource extends AbstractReloadableConfigSource {

    public static final String NAME = "LowReloadableConfigSource";

    @Getter
    @Setter
    private String path = "LowReloadableConfigSourcePath";

    @Getter
    @Setter
    private int ordinal = 133;

    @Override
    public String getName() {
        return NAME;
    }
}
