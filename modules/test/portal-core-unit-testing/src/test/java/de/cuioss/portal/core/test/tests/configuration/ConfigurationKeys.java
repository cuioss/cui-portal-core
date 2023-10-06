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
package de.cuioss.portal.core.test.tests.configuration;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConfigurationKeys {

    private static final String DE_CUI_TEST = "de.cui.test.";
    public static final String KEY_1 = DE_CUI_TEST + "key1";
    public static final String KEY_2 = DE_CUI_TEST + "key2";
    public static final String KEY_3 = DE_CUI_TEST + "key3";

    public static final String NOT_PREFIXED = "not-prefixed";

    public static final Integer INTEGER_KEY = 1;

    static final String PACKAGE_PRIVATE = DE_CUI_TEST + "private";
}
