package de.cuioss.portal.configuration.impl.source;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import de.cuioss.portal.configuration.util.ConfigurationHelper;

/**
 * Expose the application configuration as resource bundle to allow EL
 * expressions like
 *
 * <pre>
 * #{configuration['xzy']}
 * </pre>
 *
 * .
 *
 * @author Matthias Walliczek
 *
 */
@Named("configuration")
@Dependent
public class ConfigurationResolver extends ResourceBundle {

    @Override
    protected Object handleGetObject(final String key) {
        return ConfigurationHelper.resolveConfigProperty(key).orElse("Undefined");
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(ConfigurationHelper.resolveConfigPropertyNames());
    }

}
