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
package de.cuioss.portal.configuration.impl.source;

import lombok.Getter;
import lombok.Setter;

public class HighReloadableConfigSource extends AbstractReloadableConfigSource {

    public static final String NAME = "HighReloadableConfigSource";

    @Getter
    @Setter
    private String path = "HighReloadableConfigSourcePath";

    @Getter
    @Setter
    private int ordinal = 166;

    @Override
    public String getName() {
        return NAME;
    }
}
