package de.cuioss.portal.configuration;

import de.cuioss.portal.configuration.cache.CacheConfig;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.portal.configuration.types.ConfigAsCacheConfig;
import de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata;
import lombok.experimental.UtilityClass;

/**
 * Provides the parameter keys used for configuring the value-set-client.
 *
 * In general three different sources for value sets can be used:
 * - remote svs service (with optional authentication)
 * - remote fhir service
 * - local file system
 *
 * These sources can be combined, so that multiple sources can be used at the same time (e.g. local
 * file and remote
 * svs service). The order is defined via a priority property.
 */
@UtilityClass
public class ValueSetClientConfigKeys {

    private static final String ENABLED = "enabled";
    private static final String CONNECTION = "connection";
    private static final String PRIORITY = "priority";

    /** "integration.value_set." */
    public static final String BASE = PortalConfigurationKeys.INTEGRATION_BASE + "value_set.";

    /**
     * Key within configuration-system with the name 'integration.value_set.enabled'
     * <p>
     * Defines whether the value-set-client is enabled at all, defaults to {@code false}
     * </p>
     */
    public static final String CODE_CLIENT_ENABLED = BASE + ENABLED;

    /**
     * Key within configuration-system with the name 'integration.value_set.type'
     * <p>
     * Defines which type of client should be used. Maybe either 'SVS', 'FILE' or 'FHIR'
     * </p>
     */
    @Deprecated // use {@link #FHIR_ENABLED}, {@link #SVS_ENABLED} and {@link #FILE_ENABLED} instead
    public static final String CODE_CLIENT_TYPE = BASE + "type";

    /**
     * Key within configuration-system with the name 'integration.value_set.atna.enabled'
     * <p>
     * Defines whether ATNA messages are logged for the value-set-client, defaults to {@code false}
     * </p>
     */
    public static final String CODE_CLIENT_ATNA_ENABLED = BASE + "atna." + ENABLED;

    private static final String LANGUAGES_BASE = BASE + "languages.";

    /**
     * Key within configuration-system with the name 'integration.value_set.languages.queried'
     * <p>
     * Expected is a comma separated list of languages to be used for querying the
     * remote code-service. If it is not set it will only queried / resolved the default language
     * from the remote code-repository
     * It defaults to "de,en"
     */
    public static final String CODE_CLIENT_LANGUAGES = LANGUAGES_BASE + "queried";

    /**
     * Key within configuration-system with the name
     * 'integration.value_set.languages.warn_on_missing'
     * <p>
     * Indicates whether the client should warn the user about missing language-versions, as of
     * {@link #CODE_CLIENT_LANGUAGES}. Expected is "true" or "false"; Defaults to {@code false}.
     */
    public static final String CODE_CLIENT_LANGUAGES_WARN_ON_MISSING = LANGUAGES_BASE + "warn_on_missing";

    /**
     * Basename for {@link ConnectionMetadata} for the svs-client-connection, see
     * {@link ConfigAsConnectionMetadata} for details
     */
    @Deprecated // use {@link SVS_CONNECTION} instead
    public static final String CONNECTION_BASE_NAME = BASE + CONNECTION;

    /**
     * Directory for svs files in case of type FILE.
     */
    @Deprecated // use {@link FILE_PATH} instead
    public static final String SVS_FILE_DIR = BASE + "path";

    /**
     * Basename for {@link CacheConfig} for the service, see
     * {@link ConfigAsCacheConfig} for details
     */
    public static final String CACHE_CONFIG_BASE_NAME = BASE + "cache";

    /**
     * Key within configuration-system with the name 'integration.value_set.cache.expiration'
     * <p>
     * Defines the expiration time for caching the calls at service-level in minutes, defaults to 5
     */
    public static final String CACHE_CONFIG_EXPIRATION = CACHE_CONFIG_BASE_NAME + "." + CacheConfig.EXPIRATION_KEY;

    /**
     * Url of the fhir server to retrieve codes from.
     */
    @Deprecated // use {@link #FHIR_CONNECTION} instead
    public static final String CODES_FHIR_URL = BASE + "codesFhirUrl";

    private static final String FHIR_BASE = BASE + "fhir.";

    /**
     * Key within configuration-system with the name 'integration.value_set.fhir.enabled'
     * <p>
     * Defines whether the fhir based value-set-client is enabled, defaults to {@code false}
     * </p>
     */
    public static final String FHIR_ENABLED = FHIR_BASE + ENABLED;

    /**
     * Defines which HAPI FHIR version should be used.
     * Valid values are (according to {@code ca.uhn.fhir.context.FhirVersionEnum#forVersionString}):
     * <ul>
     *     <li>DSTU2</li>
     *     <li>DSTU3</li>
     *     <li>STU3</li>
     *     <li>R4</li>
     *     <li>R5</li>
     * </ul>
     */
    public static final String FHIR_VERSION = FHIR_BASE + "version";

    /**
     * Key within configuration-system with the name 'integration.value_set.fhir.priority'
     * <p>
     * Defines the priority of the fhir based value-set-client when other clients are configured at
     * the same time.
     * </p>
     */
    public static final String FHIR_PRIORITY = FHIR_BASE + PRIORITY;

    /**
     * Key within configuration-system with the name 'integration.value_set.fhir.connection'
     * <p>
     * Defines the url of the fhir server to read the value sets from if {@link #FHIR_ENABLED} is
     * "true".
     */
    public static final String FHIR_CONNECTION = FHIR_BASE + CONNECTION;

    private static final String SVS_BASE = BASE + "svs.";

    /**
     * Key within configuration-system with the name 'integration.value_set.svs.enabled'
     * <p>
     * Defines whether the svs based value-set-client is enabled, defaults to {@code false}
     * </p>
     */
    public static final String SVS_ENABLED = SVS_BASE + ENABLED;

    /**
     * Key within configuration-system with the name 'integration.value_set.svs.priority'
     * <p>
     * Defines the priority of the svs based value-set-client when other clients are configured at
     * the same time.
     * </p>
     */
    public static final String SVS_PRIORITY = SVS_BASE + PRIORITY;

    /**
     * Key within configuration-system with the name 'integration.value_set.svs.connection'
     * <p>
     * Defines the connection to a svs service to read the value sets from if {@link #SVS_ENABLED}
     * is "true".
     * </p>
     * Basename for {@link ConnectionMetadata} for the service, see
     * {@link ConfigAsConnectionMetadata} for details
     */
    public static final String SVS_CONNECTION = SVS_BASE + CONNECTION;

    private static final String FILE_BASE = BASE + "file.";

    /**
     * Key within configuration-system with the name 'integration.value_set.file.enabled'
     * <p>
     * Defines whether the file based value-set-client is enabled, defaults to {@code false}
     * </p>
     */
    public static final String FILE_ENABLED = FILE_BASE + ENABLED;

    /**
     * Key within configuration-system with the name 'integration.value_set.file.priority'
     * <p>
     * Defines the priority of the file based value-set-client when other clients are configured at
     * the same time.
     * </p>
     */
    public static final String FILE_PRIORITY = FILE_BASE + PRIORITY;

    /**
     * Directory for svs files.
     * <p>
     * It is expected to contain for each category a file with the name of the category + ".xml".
     * This file may contain
     * all relevant languages. Additional files with the name of the category + "_" + locale +
     * ".xml" can be added for
     * additional locales.
     * </p>
     * <p>
     * So the directory should look like:
     *
     * <pre>
     *  - /svs
     *  - /svs/category.xml
     *  - /svs/category_de.xml
     *  - /svs/category_en.xml
     * </pre>
     * </p>
     */
    public static final String FILE_PATH = FILE_BASE + "path";

}
