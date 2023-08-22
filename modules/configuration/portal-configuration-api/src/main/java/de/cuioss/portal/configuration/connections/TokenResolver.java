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
package de.cuioss.portal.configuration.connections;

import java.io.Serializable;

/**
 * Instances of this class are capable of resolving tokens. A token always
 * provides a corresponding key, usually to be used for the header, and a
 * concrete token value. Necessary pre-processing like encoding and or prefixing
 * / suffixing are assumed to be covered by the concrete implementations.
 *
 * @author Oliver Wolff
 *
 */
public interface TokenResolver extends Serializable {

    /**
     * @return a corresponding key, usually to be used for the header
     */
    String getKey();

    /**
     * @return a concrete token value. Necessary pre-processing like encoding and or
     *         prefixing / suffixing are assumed to be covered by the concrete
     *         implementations.
     */
    String resolve();
}
