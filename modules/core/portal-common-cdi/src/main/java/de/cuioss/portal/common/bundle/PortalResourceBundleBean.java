/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.common.bundle;

import de.cuioss.portal.common.cdi.PortalBeanManager;
import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Portal-specific implementation of {@link ResourceBundle} that delegates to
 * {@link ResourceBundleWrapper} for actual resource resolution.
 *
 * <h2>Overview</h2>
 * This class bridges the gap between JSF's resource bundle requirements and
 * Portal's unified bundle management system.
 * It is primarily used to expose Portal's resource bundles to JSF views under the name "msgs".
 *
 * <h2>Technical Background</h2>
 * Since {@link ResourceBundle} is an abstract class and not an interface, it
 * cannot be directly proxied by CDI.
 * This implementation provides the necessary bridge while maintaining CDI compatibility.
 *
 * <h2>Usage in JSF Views</h2>
 * Access bundle messages directly in JSF views:
 * <pre>
 * // Basic message access
 * #{msgs['page.title']}
 *
 * // With parameters
 * #{msgs['welcome.message'].format(user.name)}
 * </pre>
 *
 * <h2>Thread Safety</h2>
 * This implementation is thread-safe.
 * All resource resolution is delegated to the thread-safe {@link ResourceBundleWrapper}.
 *
 * @author Oliver Wolff
 * @see ResourceBundleWrapper
 * @see jakarta.faces.application.Application#getResourceBundle
 */
@EqualsAndHashCode(callSuper = false)
@ToString
public class PortalResourceBundleBean extends ResourceBundle implements Serializable {

    @Serial
    private static final long serialVersionUID = -4996798647652125L;

    private static final CuiLogger LOGGER = new CuiLogger(PortalResourceBundleBean.class);

    /**
     * Lookup name for el-expression within views: "msgs"
     */
    public static final String BUNDLE_NAME = "msgs";

    private ResourceBundleWrapper resourceBundleWrapper;

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the given key is {@code null}
     */
    @Override
    protected Object handleGetObject(@NonNull final String key) {
        return getResourceBundleWrapper().getString(key);
    }

    /**
     * {@inheritDoc}
     *
     * @return a non-null enumeration of the keys in this resource bundle
     */
    @Override
    @NonNull
    public Enumeration<String> getKeys() {
        return Collections.enumeration(getResourceBundleWrapper().keySet());
    }

    /**
     * {@inheritDoc}
     *
     * @return a non-null set of the keys in this resource bundle
     */
    @Override
    @NonNull
    public Set<String> keySet() {
        return getResourceBundleWrapper().keySet();
    }

    /**
     * Retrieves the wrapped ResourceBundleWrapper instance, creating it if necessary.
     *
     * @return the non-null ResourceBundleWrapper instance
     */
    @NonNull
    private ResourceBundleWrapper getResourceBundleWrapper() {
        if (null == resourceBundleWrapper) {
            LOGGER.debug("Resolving ResourceBundleWrapper");
            resourceBundleWrapper = PortalBeanManager.resolveRequiredBean(ResourceBundleWrapper.class);
        }
        return resourceBundleWrapper;
    }
}
