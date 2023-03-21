package de.cuioss.portal.configuration;

import lombok.experimental.UtilityClass;

/**
 * Defines the configuration keys for using within vault-client
 *
 * @author Oliver Wolff
 *
 */
@UtilityClass
public class VaultClientConfigKeys {

    /**
     * Location of the property file for the default-configuration
     */
    public static final String VAULT_DEFAULT_CONFIG_LOCATION =
        "classpath:/META-INF/vault_client_default_configuration.yml";

    private static final String VAULT_BASE = PortalConfigurationKeys.INTEGRATION_BASE + "vault.";
    private static final String VAULT_ENDPOINT_BASE = VAULT_BASE + "endpoint.";

    /** Base name for identifying a vault-connection. */
    public static final String VAULT_CONNECTION_BASE = VAULT_BASE + "connection";

    /** Base name for identifying a vault-connection. */
    public static final String VAULT_CLIENT_ENABLED = VAULT_BASE + "enabled";

    /** Default name for the Key-Value endpoint, default value is 'secret'. */
    public static final String VAULT_ENDPOINT_KEY_VALUE = VAULT_ENDPOINT_BASE + "key_value";

}
