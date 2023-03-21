package de.cuioss.portal.configuration.impl.source;

import lombok.Getter;
import lombok.Setter;

public class HighReloadableConfigSource extends AbstractReloadableConfigSource {

    public static final String NAME = "HighReloadableConfigSource";

    @Getter
    @Setter
    private String path = "HighReloadableConfigSourcePath";

    @Getter
    @Setter
    private int ordinal = 166;

    @Override
    public String getName() {
        return NAME;
    }
}
