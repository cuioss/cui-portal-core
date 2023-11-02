package de.cuioss.portal.common.bundle.support;

import java.util.Optional;

import de.cuioss.portal.common.bundle.ResourceBundleLocator;

public class InvalidBundelPath implements ResourceBundleLocator {

    private static final long serialVersionUID = 5363360633544306322L;

    @Override
    public Optional<String> getBundlePath() {
        return Optional.of("de.not.there");
    }

}
