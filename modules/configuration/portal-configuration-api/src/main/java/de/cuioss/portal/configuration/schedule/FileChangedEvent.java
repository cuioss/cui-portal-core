package de.cuioss.portal.configuration.schedule;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.nio.file.Path;

import javax.inject.Qualifier;

/**
 * Defines events that will be fired from implementations of {@link FileWatcherService} in case a
 * {@link Path} was modified. The payload is the actual {@link Path} representing the file being
 * changed
 *
 * @author Oliver Wolff
 */
@Qualifier
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
public @interface FileChangedEvent {

}
