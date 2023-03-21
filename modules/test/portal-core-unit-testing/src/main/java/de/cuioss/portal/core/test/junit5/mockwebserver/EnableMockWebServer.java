package de.cuioss.portal.core.test.junit5.mockwebserver;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

import okhttp3.mockwebserver.MockWebServer;

/**
 * Using this annotations at type-level of a junit 5 test will control an instance of
 * {@link MockWebServer}. The class where this annotation is used must implement
 * {@link MockWebServerHolder}.
 *
 * As default the mock server will automatically be started before each test.
 * To disable this behavior and start the mock server manually please set {@link #manualStart()} to
 * true.
 *
 * @author Oliver Wolff
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@ExtendWith(MockWebServerExtension.class)
public @interface EnableMockWebServer {

    /**
     * @return boolean indicating whether the infrastructure should start the server automatically,
     *         default, or not
     */
    boolean manualStart() default false;

}
