package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CUSTOMIZATION_DIR;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigProperty;

import java.io.Serializable;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.installationpaths.PortalInstallationPathChangedEvent;
import de.cuioss.portal.configuration.installationpaths.PortalInstallationPathChangedPayload;
import de.cuioss.portal.configuration.installationpaths.PortalInstallationPathType;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Lazy loading of the customization directory, because its position may be part of the
 * configured application installation configuration file, so it should be determined after
 * parsing the file.
 *
 * Fires an {@link Event} with {@link PortalInstallationPathChangedPayload}, if the configuration of
 * the
 * customization directory has changed.
 *
 * @author Oliver Wolff
 * @author Sven Haag
 */
@ApplicationScoped
class CustomizationDirChangeObserver implements Serializable {

    private static final long serialVersionUID = -6465816355064932849L;
    private static final CuiLogger log = new CuiLogger(CustomizationDirChangeObserver.class);

    private String customizationDir;

    @Inject
    @PortalInstallationPathChangedEvent
    private Event<PortalInstallationPathChangedPayload> pathChangedEvent;

    @PostConstruct
    void init() {
        initCustomizationDir();
    }

    private void initCustomizationDir() {
        customizationDir = resolveConfigProperty(PORTAL_CUSTOMIZATION_DIR).orElse(null);
    }

    /**
     * Listener for {@link PortalConfigurationChangeEvent}s. Reconfigures the location of the
     * customization dir.
     *
     * @param deltaMap configuration changes
     */
    @SuppressWarnings("squid:S2789")
    void configurationChangeEventListener(
            @Observes @PortalConfigurationChangeEvent final Map<String, String> deltaMap) {
        if (deltaMap.containsKey(PORTAL_CUSTOMIZATION_DIR)) {
            final Optional<String> oldCustomizationDirectory = Optional.ofNullable(customizationDir);
            initCustomizationDir();
            final Optional<String> newCustomizationDirectory = Optional.ofNullable(customizationDir);
            if (!oldCustomizationDirectory.equals(newCustomizationDirectory)) {

                final var before =
                    oldCustomizationDirectory.isPresent() ? Paths.get(oldCustomizationDirectory.get()) : null;

                final var after =
                    newCustomizationDirectory.isPresent() ? Paths.get(newCustomizationDirectory.get()) : null;

                final var payload =
                    PortalInstallationPathType.CUSTOMIZATION_DIRECTORY.ofPath(before, after);

                log.info("Firing PortalInstallationPathChangedEvent due to changed customization dir: {}",
                        payload);
                pathChangedEvent.fire(payload);
            }
        }
    }
}
