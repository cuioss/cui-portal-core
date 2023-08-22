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
package de.cuioss.portal.configuration;

/**
 * @author Sven Haag
 */
public interface Reloadable {

    /**
     * Callback method to initialize a reload of the perhaps cached data, basically
     * telling e.g a configuration source it should read from its source again as
     * the file content might have changed. Only makes sense for changeable files,
     * i.e. from the file system and not from the classpath.
     */
    default void reload() {
    }
}
