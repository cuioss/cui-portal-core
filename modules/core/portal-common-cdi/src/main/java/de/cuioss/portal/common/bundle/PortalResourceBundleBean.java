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

import de.cuioss.portal.common.PortalCommonCDILogMessages;
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
 * <p>
 * Portal variant of {@link java.util.ResourceBundle}. It delegates to
 * {@link de.cuioss.portal.common.bundle.ResourceBundleWrapper} that does the actual heavy lifting.
 * </p>
 * <p>
 * Its usage is for cases where there is technically a {@link java.util.ResourceBundle}
 * needed.
 * Sadly, there is no corresponding interface, solely an Abstract-Class
 * that cannot be proxied by CDI.
 * Currently, its sole use is the context of the
 * PortalApplication, that it exposes it on
 * {@link jakarta.faces.application.Application#getResourceBundle(jakarta.faces.context.FacesContext, String)}
 * with the name "msgs"
 * </p><p>
 * It can be used directly in jsf views: {@code #{msgs['page.401.title']}}
 * </p>
 *
 * @author Oliver Wolff
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
            LOGGER.debug(PortalCommonCDILogMessages.RESOLVING_BUNDLE_BEAN.format());
            resourceBundleWrapper = PortalBeanManager.resolveRequiredBean(ResourceBundleWrapper.class);
        }
        return resourceBundleWrapper;
    }
}
