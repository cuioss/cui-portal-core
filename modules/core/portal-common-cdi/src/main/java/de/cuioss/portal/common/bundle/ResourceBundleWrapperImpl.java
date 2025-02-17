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

import de.cuioss.portal.common.PortalCommonLogMessages;
import de.cuioss.portal.common.locale.LocaleChangeEvent;
import de.cuioss.portal.common.locale.PortalLocale;
import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.Joiner;
import de.cuioss.tools.string.MoreStrings;
import de.cuioss.uimodel.application.CuiProjectStage;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import lombok.EqualsAndHashCode;
import lombok.Synchronized;
import lombok.ToString;

import java.io.Serial;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static de.cuioss.tools.collect.CollectionLiterals.mutableSet;

/**
 * It can do the following tricks:
 * <ul>
 * <li>Define a working {@link java.io.Serializable} contract for
 * {@link java.util.ResourceBundle}s</li>
 * <li>Listens and acts on {@link de.cuioss.portal.common.locale.LocaleChangeEvent}s</li>
 * </ul>
 * <p>
 * Handling of Missing Keys: On {@link de.cuioss.portal.common.stage.ProjectStage#DEVELOPMENT} it will throw a
 * {@link java.util.MissingResourceException}. Otherwise, it will return the requested key,
 * surrounded with Question-Marks
 *
 * @author Oliver Wolff
 */
@SessionScoped
@EqualsAndHashCode(of = "keyList", doNotUseGetters = true)
@ToString(of = "keyList", doNotUseGetters = true)
public class ResourceBundleWrapperImpl implements ResourceBundleWrapper {

    @Serial
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

    /**
     * Resolves a message for the given key.
     * 
     * @param key to be looked up, must not be blank
     * @return the resolved message if key is defined, otherwise returns "??[key]??"
     *         and logs a warning. Returns null if key is blank.
     * @throws MissingResourceException in development stage if key is not found
     */
    @Override
    public String getString(final String key) {
        if (MoreStrings.isBlank(key)) {
            LOGGER.warn(PortalCommonLogMessages.WARN.BUNDLE_IGNORING_EMPTY_KEY::format);
            return null;
        }

        for (final ResourceBundle bundle : getResolvedBundles()) {
            if (bundle.containsKey(key)) {
                return bundle.getString(key);
            }
        }

        if (projectStage.get().isDevelopment()) {
            throw new MissingResourceException(
                    PortalCommonLogMessages.WARN.KEY_NOT_FOUND.format(key, resourceBundleRegistry.getResolvedPaths()),
                    getClass().getName(),
                    key);
        }

        LOGGER.warn(PortalCommonLogMessages.WARN.KEY_NOT_FOUND.format(key, resourceBundleRegistry.getResolvedPaths()));
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
        LOGGER.debug("Locale changed to '%s', clearing bundle cache", newLocale);
        resolvedBundles = null;
    }

    private List<ResourceBundle> getResolvedBundles() {
        if (null == resolvedBundles) {
            var builder = new CollectionBuilder<ResourceBundle>();
            var currentLocale = localeProvider.get();

            for (final ResourceBundleLocator path : resourceBundleRegistry.getResolvedPaths()) {
                path.getBundle(currentLocale).ifPresent(builder::add);
            }
            resolvedBundles = builder.toImmutableList();
            LOGGER.debug("Resolved %d resource bundles for locale '%s'", resolvedBundles.size(), currentLocale);
        }
        return resolvedBundles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(keySet());
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBundleContent() {
        return Joiner.on(", ").join(resourceBundleRegistry.getResolvedPaths());
    }
}
