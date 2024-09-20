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
package de.cuioss.portal.common.bundle.support;

import java.io.Serial;
import java.util.Optional;

import de.cuioss.portal.common.bundle.ResourceBundleLocator;

public class InvalidBundlePath implements ResourceBundleLocator {

    @Serial
    private static final long serialVersionUID = 5363360633544306322L;

    @Override
    public Optional<String> getBundlePath() {
        return Optional.of("de.not.there");
    }

}
