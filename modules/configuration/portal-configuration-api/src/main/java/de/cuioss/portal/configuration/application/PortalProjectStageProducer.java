package de.cuioss.portal.configuration.application;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import de.cuioss.uimodel.application.CuiProjectStage;

/**
 * Marker identifying concrete instances of the {@link CuiProjectStage}.
 * The implementation must ensure that it never returns <code>null</code>.
 * Fallback must always be "production".
 *
 * @author Oliver Wolff
 */
@Qualifier
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
public @interface PortalProjectStageProducer {
}
