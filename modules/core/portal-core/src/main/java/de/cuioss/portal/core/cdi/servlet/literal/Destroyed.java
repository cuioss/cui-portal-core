package de.cuioss.portal.core.cdi.servlet.literal;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Qualifier for events which are fired when servlet objects are deleted.
 * 
 * @author https://github.com/apache/deltaspike/blob/deltaspike-1.9.6/deltaspike/core/api/src/main/java/org/apache/deltaspike/core/api/lifecycle/Destroyed.java
 */
@Qualifier
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
public @interface Destroyed {

}
