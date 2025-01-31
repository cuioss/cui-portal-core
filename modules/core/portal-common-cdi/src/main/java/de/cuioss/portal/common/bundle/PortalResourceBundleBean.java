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

import de.cuioss.portal.common.cdi.PortalBeanManager;
import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
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
 * It usage is for cases where there is technically a {@link java.util.ResourceBundle}
 * needed. Sadly there is no corresponding interface, solely an Abstract-Class
 * that can not be proxied by CDI. Currently it's sole use is the context of the
 * PortalApplication, that it exposes it on
 * {@link jakarta.faces.application.Application#getResourceBundle(jakarta.faces.context.FacesContext, String)}
 * with the name "msgs"
 * </p><p>
 * It can be used directly in jsf views: {@code #{msgs['page.401.title']}}
 * </p>
 *
 * @author Oliver Wolff
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PortalResourceBundleBean extends ResourceBundle implements Serializable {

    @Serial
    private static final long serialVersionUID = 3953649686127640297L;

    private static final CuiLogger LOGGER = new CuiLogger(PortalResourceBundleBean.class);

    /** Lookup name for el-expression within views: "msgs" */
    public static final String BUNDLE_NAME = "msgs";

    private final ResourceBundleWrapper resourceBundleWrapper;

    private String allBundleNames;

    /** {@inheritDoc} */
    @Override
    protected Object handleGetObject(final String key) {
        return resourceBundleWrapper.getString(key);
    }

    /** {@inheritDoc} */
    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(keySet());
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> keySet() {
        return resourceBundleWrapper.keySet();
    }

    /**
     * Factory Method creating a new instance with loading the contained
     * {@link de.cuioss.portal.common.bundle.ResourceBundleWrapper}, {@link jakarta.enterprise.context.SessionScoped} from the CDR-COntext
     *
     * @return a {@link de.cuioss.portal.common.bundle.PortalResourceBundleBean} object
     */
    public static PortalResourceBundleBean resolveFromCDIContext() {
        LOGGER.debug("Resolving PortalResourceBundleBean from CDI-Context");
        var resourceBundleWrapper = PortalBeanManager.resolveRequiredBean(ResourceBundleWrapper.class);
        return new PortalResourceBundleBean(resourceBundleWrapper);
    }

}
