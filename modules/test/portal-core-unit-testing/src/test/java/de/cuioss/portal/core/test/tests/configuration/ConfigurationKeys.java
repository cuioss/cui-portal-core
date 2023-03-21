package de.cuioss.portal.core.test.tests.configuration;

import lombok.experimental.UtilityClass;

@SuppressWarnings("javadoc")
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
