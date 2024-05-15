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
package de.cuioss.portal.common.bundle;

import static de.cuioss.tools.collect.CollectionLiterals.mutableSet;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Provider;

import de.cuioss.portal.common.locale.LocaleChangeEvent;
import de.cuioss.portal.common.locale.PortalLocale;
import de.cuioss.portal.common.stage.ProjectStage;
import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.Joiner;
import de.cuioss.uimodel.application.CuiProjectStage;
import lombok.EqualsAndHashCode;
import lombok.Synchronized;
import lombok.ToString;

/**
 * It can do the following tricks:
 * <ul>
 * <li>Define a working {@link Serializable} contract for
 * {@link ResourceBundle}s</li>
 * <li>Listens and acts on {@link LocaleChangeEvent}s</li>
 * </ul>
 *
 * Handling of Missing Keys: On {@link ProjectStage#DEVELOPMENT} it will throw a
 * {@link MissingResourceException}. Otherwise it will return the requested key,
 * surrounded with Question-Marks
 *
 * @author Oliver Wolff
 */
@SessionScoped
@EqualsAndHashCode(of = "keyList", doNotUseGetters = true)
@ToString(of = "keyList", doNotUseGetters = true)
public class ResourceBundleWrapperImpl implements ResourceBundleWrapper {

    private static final long serialVersionUID = 9136037316650210138L;

    private static final CuiLogger LOGGER = new CuiLogger(ResourceBundleWrapperImpl.class);

    @Inject
    ResourceBundleRegistry resourceBundleRegistry;

    private transient List<ResourceBundle> resolvedBundles;

    @Inject
    Provider<CuiProjectStage> projectStage;

    @Inject
    @PortalLocale
    Provider<Locale> localeProvider;

    private final List<String> keyList = new CopyOnWriteArrayList<>();

    @Override
    public String getString(final String key) {

        for (final ResourceBundle bundle : getResolvedBundles()) {
            if (bundle.containsKey(key)) {
                return bundle.getString(key);
            }
        }

        final var errMsg = "Portal-003 : No key '" + key + "' defined within any of the configured bundles: "
                + resourceBundleRegistry.getResolvedPaths();

        if (projectStage.get().isDevelopment()) {
            throw new MissingResourceException(errMsg, "ResourceBundleWrapperImpl", key);
        }

        LOGGER.warn(errMsg);
        return "??" + key + "??";

    }

    /**
     * Listener for instances of {@link LocaleChangeEvent} Therefore it can react on
     * changes of the locale by disposing the loaded {@link ResourceBundle}s and
     * therefore forcing a reload of the newly set locale
     *
     * @param newLocale
     */
    void actOnLocaleChangeEven(@Observes @LocaleChangeEvent final Locale newLocale) {
        resolvedBundles = null;
    }

    private List<ResourceBundle> getResolvedBundles() {
        if (null == resolvedBundles) {
            var builder = new CollectionBuilder<ResourceBundle>();

            for (final ResourceBundleLocator path : resourceBundleRegistry.getResolvedPaths()) {
                path.getBundle(localeProvider.get()).ifPresent(builder::add);
            }
            resolvedBundles = builder.toImmutableList();
        }
        return resolvedBundles;
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(keySet());
    }

    @Override
    @Synchronized
    public Set<String> keySet() {
        if (keyList.isEmpty()) {
            final Set<String> builder = new HashSet<>();
            for (final ResourceBundle bundle : getResolvedBundles()) {
                builder.addAll(bundle.keySet());
            }
            keyList.addAll(builder);
        }
        return mutableSet(keyList);
    }

    @Override
    public String getBundleContent() {
        return Joiner.on(", ").join(resourceBundleRegistry.getResolvedPaths());
    }
}
