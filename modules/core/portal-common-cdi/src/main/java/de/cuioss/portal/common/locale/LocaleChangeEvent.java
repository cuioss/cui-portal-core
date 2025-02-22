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

import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI event qualifier for locale change notifications.
 * 
 * <p>This event is fired when a user's locale changes, carrying the new
 * {@link java.util.Locale} as its payload.
 * 
 * <h2>Usage</h2>
 * <pre>
 * &#064;Inject
 * private Event<Locale> localeChangeEvent;
 * 
 * public void changeLocale(Locale newLocale) {
 *     localeChangeEvent.fire(newLocale);
 * }
 * </pre>
 * 
 * <h2>Observing Changes</h2>
 * <pre>
 * public void onLocaleChange(&#064;Observes &#064;LocaleChangeEvent Locale newLocale) {
 *     // Handle locale change
 * }
 * </pre>
 * 
 * <p>Note: The event is fired synchronously to ensure all observers process the
 * change before continuing.
 *
 * @author Oliver Wolff
 * @see PortalLocale
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface LocaleChangeEvent {

}
