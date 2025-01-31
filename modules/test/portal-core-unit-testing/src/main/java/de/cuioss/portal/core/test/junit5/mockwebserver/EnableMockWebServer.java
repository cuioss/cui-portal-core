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
package de.cuioss.portal.core.test.junit5.mockwebserver;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Using this annotation at type-level of a junit 5 test will control an
 * instance of {@link MockWebServer}. The class where this annotation is used
 * must implement {@link MockWebServerHolder}.
 * <p>
 * As default the mock server will automatically be started before each test. To
 * disable this behavior and start the mock server manually please set
 * {@link #manualStart()} to true.
 *
 * @author Oliver Wolff
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@ExtendWith(MockWebServerExtension.class)
public @interface EnableMockWebServer {

    /**
     * @return boolean indicating whether the infrastructure should start the server
     *         automatically, default, or not
     */
    boolean manualStart() default false;

}
