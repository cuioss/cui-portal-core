package de.cuioss.portal.core.bundle;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ResourceBundle;

import javax.inject.Qualifier;

/**
 * Marker identifying instances of {@link ResourceBundle}s that are unified ResourceBundles,
 * analogous to CuiResourceBundle. Caution it should always be used as {@link ResourceBundle}
 *
 * @author Oliver Wolff
 */
@Qualifier
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
public @interface PortalResourceBundle {

}
