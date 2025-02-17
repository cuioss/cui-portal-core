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
package de.cuioss.portal.common.locale;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * CDI qualifier for injecting the client-specific {@link java.util.Locale}.
 * 
 * <p>This qualifier enables the injection of the current user's locale, which is
 * dynamically determined based on client preferences and portal configuration.
 * 
 * <h2>Usage</h2>
 * <pre>
 * &#064;Inject
 * &#064;PortalLocale
 * private Locale userLocale;
 * </pre>
 * 
 * <p>The injected locale will automatically update when the user changes their
 * language preference.
 * To be notified of such changes, observe the {@link LocaleChangeEvent}.
 *
 * @author Oliver Wolff
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface PortalLocale {

}
