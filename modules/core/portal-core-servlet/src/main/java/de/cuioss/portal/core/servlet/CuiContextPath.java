package de.cuioss.portal.core.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;
import javax.servlet.http.HttpServletRequest;

/**
 * Identifier for the Context Path. Provides the context path from the {@link HttpServletRequest}.
 *
 * @author Matthias Walliczek
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface CuiContextPath {

}
