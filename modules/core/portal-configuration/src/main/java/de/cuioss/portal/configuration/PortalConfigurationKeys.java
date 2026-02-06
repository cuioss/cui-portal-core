/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.configuration;

import de.cuioss.portal.common.stage.ProjectStage;
import de.cuioss.portal.configuration.schedule.FileWatcherService;
import lombok.experimental.UtilityClass;

/**
 * Central repository of configuration keys used throughout the Portal application.
 * This class defines the standard configuration key hierarchy and provides
 * constants for all configurable aspects of the Portal.
 * 
 * <h2>Key Hierarchy</h2>
 * The configuration keys follow a hierarchical structure:
 * <ul>
 *   <li>{@code portal.*} - Core portal configuration</li>
 *   <li>{@code portal.session.*} - Session-related settings</li>
 *   <li>{@code portal.configuration.*} - Configuration system settings</li>
 *   <li>{@code portal.resource.*} - Resource handling</li>
 *   <li>{@code portal.theme.*} - Theme configuration</li>
 *   <li>{@code portal.pages.*} - Page-specific settings</li>
 *   <li>{@code portal.locale.*} - Localization settings</li>
 *   <li>{@code integration.*} - Integration configurations</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * <pre>
 * // Basic configuration injection
 * &#64;Inject
 * &#64;ConfigProperty(name = PortalConfigurationKeys.PORTAL_BASE + "myKey")
 * private String myConfig;
 * 
 * // Theme configuration
 * &#64;Inject
 * &#64;ConfigProperty(name = PortalConfigurationKeys.THEME_BASE + "name")
 * private String themeName;
 * 
 * // Integration settings
 * &#64;Inject
 * &#64;ConfigProperty(name = PortalConfigurationKeys.INTEGRATION_BASE + "endpoint")
 * private String integrationEndpoint;
 * </pre>
 * 
 * <h2>Best Practices</h2>
 * <ul>
 *   <li>Always use these constants instead of hardcoding configuration keys</li>
 *   <li>Follow the established naming hierarchy when adding new keys</li>
 *   <li>Document the purpose and expected values for each key</li>
 *   <li>Consider providing default values in {@link PortalConfigurationDefaults}</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @see PortalConfigurationDefaults
 * @see de.cuioss.portal.configuration.types
 */
@UtilityClass
public class PortalConfigurationKeys {

    /**
     * Prefix, unifying naming.
     */
    public static final String ENABLED = "enabled";

    /**
     * The base path for all CUI Portal-based keys.
     */
    public static final String PORTAL_BASE = "portal.";

    private static final String SESSION_BASE = PORTAL_BASE + "session.";

    private static final String CONFIGURATION_BASE = PORTAL_BASE + "configuration.";

    private static final String SCHEDULER_BASE = CONFIGURATION_BASE + "scheduler.";

    private static final String RESOURCE_BASE = PORTAL_BASE + "resource.";

    private static final String THEME_BASE = PORTAL_BASE + "theme.";

    public static final String VIEW_BASE = PORTAL_BASE + "view.";

    private static final String PAGES_BASE = PORTAL_BASE + "pages.";

    private static final String LOCALE_BASE = PORTAL_BASE + "locale.";

    private static final String PAGES_LOGIN_BASE = PAGES_BASE + "login.";

    private static final String PAGES_ERROR_BASE = PAGES_BASE + "error.";

    private static final String HISTORY_BASE = PORTAL_BASE + "history.";

    private static final String STORAGE_BASE = PORTAL_BASE + "storage.";

    private static final String CUSTOMIZATION_BASE = PORTAL_BASE + "customization.";

    private static final String LISTENER_BASE = PORTAL_BASE + "listener.";

    private static final String LAZY_LOADING_BASE = PORTAL_BASE + "lazyLoading.";

    public static final String APPLICATION_CONTEXT_NAME = "application.context.name";

    /**
     * Context parameter prefix within configuration-subsystem with the name
     * portal.httpHeader.
     * <p>
     * Defines optional header parameters to be configured.
     */
    public static final String HTTP_HEADER_BASE = PORTAL_BASE + "httpHeader.";

    /**
     * Context parameter prefix within configuration-subsystem with the name
     * portal.httpHeader.enabled
     * <p>
     * Defines optional header parameters to be configured.
     */
    public static final String HTTP_HEADER_ENABLED = HTTP_HEADER_BASE + ENABLED;

    /**
     * Context parameter prefix within configuration-subsystem with the name
     * 'integration.'.
     * <p>
     * Base name for integration-related configuration
     */
    public static final String INTEGRATION_BASE = "integration.";

    /**
     * Context parameter prefix within configuration-subsystem with the name
     * portal.dashboard.widget.
     * <p>
     * Activates dashboard widgets and defines
     * their order.
     */
    public static final String DASHBOARD_WIDGET = PORTAL_BASE + "dashboard.widget.";

    /**
     * Context parameter prefix within configuration-subsystem with the name
     * portal.menu.
     * <p>
     * Activates navigation menu items (see
     * de.cuioss.jsf.api.components.model.menu.NavigationMenuItem and
     * defines their order and hierarchy.
     * </p>
     * <p>
     * Each entry consists of a logical name matching
     * NavigationMenuItem#getId(),
     * and the properties "enabled", "order" and "parent".
     * </p>
     * Example:
     *
     * <pre>
     * portal.menu:
     *   about:
     *     enabled: true
     *     order: 20
     *     parent: userMenuItem
     * </pre>
     * <ul>
     * <li>"enabled" defaults to true and can be used to disable existing
     * entries.</li>
     * <li>"order" is used to define an order of the items. Consider reserving space
     * between the items.</li>
     * <li>"parent" is used to define a hierarchy. It can be a specific menu item id
     * or {@value PortalConfigurationKeys#MENU_TOP_IDENTIFIER}.</li>
     * </ul>
     * <em>Conventions</em>:
     * <ul>
     * <li>To create a separator menu item, just create an entry starting with
     * "separator". No NavigationMenuItem needs to be created.</li>
     * </ul>
     */
    public static final String MENU_BASE = PORTAL_BASE + "menu.";

    /**
     * This is used for setting the top-level menu as parent. In previous versions
     * {@code null} as parent was interpreted as indicator for displaying the
     * menu-item as a top-level element
     */
    public static final String MENU_TOP_IDENTIFIER = "top";

    /**
     * Location of the property file for the portal-branding-configuration location
     */
    public static final String PORTAL_BRANDING_CONFIG_LOCATION = "classpath:/META-INF/portal_branding_configuration.properties";

    /**
     * in minutes
     */
    public static final String PORTAL_SESSION_MAX_INACTIVE_INTERVAL = SESSION_BASE + "maxInactiveInterval";

    /**
     * in minutes
     */
    public static final String PORTAL_SESSION_TIMEOUT = SESSION_BASE + "timeout";

    /**
     * Context parameter within configuration-subsystem with the name
     * portal.configuration.stage
     * <p>
     * Used to set the operating mode aka project stage. Defaults to
     * {@code production}. Valid values are:
     * development|test|configuration|production
     * </p>
     */
    public static final String PORTAL_STAGE = CONFIGURATION_BASE + "stage";

    /**
     * Context parameter for the installation configuration directory with the name:
     * 'portal.configuration.dir'
     * <p>
     * Used to locate the {@linkplain #PORTAL_CONFIG_FILENAME_DEFAULT} configuration
     * file. If this parameter is not present the default is
     * {@linkplain #PORTAL_CONFIG_DIR_DEFAULT}.
     * </p>
     */
    public static final String PORTAL_CONFIG_DIR = CONFIGURATION_BASE + "dir";

    /**
     * Defines the path, where the module default configs can be found:
     * 'META-INF/microprofile-config.properties'
     */
    public static final String MODULE_DEFAULT_CONFIG_PROPERTY_PATH = "META-INF/microprofile-config.properties";

    /**
     * Default portal configuration directory. Must be kept in sync with
     * {@code portal-configuration/src/main/resources/META-INF/microprofile-config.yaml}
     */
    public static final String PORTAL_CONFIG_DIR_DEFAULT = "config/";

    /**
     * File name of the portal configuration YAML file: application.yml
     */
    public static final String PORTAL_CONFIG_FILENAME_DEFAULT = "application.yml";

    /**
     * File name of the portal configuration production YAML file:
     * application-production.yml
     */
    public static final String PORTAL_CONFIG_PRODUCTION_FILENAME_DEFAULT = "application-production.yml";

    /**
     * Context parameter within configuration-subsystem with the name
     * {@value #SCHEDULER_FILE_SCAN_CRON_EXPRESSION}
     * <p>
     * Used for the configuration of the cron-based system for checking for changes
     * within configuration files. The configuration is done using cron-expressions.
     * </p>
     * <p>
     * The default value for the portal is '* 0/1 * * * ?' saying the files will be
     * scanned every minute for changes. In case of system configuration you can
     * lower it to e.g. every 5 seconds: '0/5 * * * * ?'. Always set it to a
     * production value like every minute if you finished configuration.
     * </p>
     * <ul>
     * <li><em>Caution</em>: Always keep in mind that this configuration change will
     * only picked up at the next scan-interval.</li>
     * <li><em>Caution</em>: Due to this nature long intervals like hourly or daily
     * can only be reconfigured with a restart or a longer time to wait.</li>
     * <li>Although the reconfiguration checks the new expression and ignores it on
     * an invalid value, the value set on start-up time must always define a correct
     * expression, because otherwise the cron-job won't start at all.</li>
     * <li>You can use <a href="http://www.cronmaker.com/">Cronmaker</a> to validate
     * you expressions.</li>
     * <li>The actual execution of the cron job will be logged at debug level of
     * de.cuioss.portal.configuration.application.schedule.FileWatcherScheduler</li>
     * </ul>
     */
    public static final String SCHEDULER_FILE_SCAN_CRON_EXPRESSION = SCHEDULER_BASE + "file_cron_expression";

    /**
     * Context parameter within configuration-subsystem with the name
     * {@value #SCHEDULER_FILE_SCAN_ENABLED}
     * <p>
     * Used for the configuration of tracking of changes in files, usually
     * configuration files, see {@link FileWatcherService} for handling details.
     * This is the successor of the previous cron-based approach. The default-value
     * is {@code true}
     * </p>
     */
    public static final String SCHEDULER_FILE_SCAN_ENABLED = CONFIGURATION_BASE + "file_watcher.enabled";

    /**
     * Context parameter within configuration-subsystem with the name
     * {@value #SCHEDULER_REST_SCAN_CRON_EXPRESSION}
     * <p>
     * Used for the configuration of the cron-based system for checking for changes
     * within configuration urls. The configuration is done using cron-expressions.
     * </p>
     * <p>
     * The default value for the portal is '* 0/1 * * * ?' saying the files will be
     * scanned every minute for changes. In case of system configuration you can
     * lower it to e.g. every 5 seconds: '0/5 * * * * ?'. Always set it to a
     * production value like every minute if you finished configuration.
     * </p>
     */
    public static final String SCHEDULER_REST_SCAN_CRON_EXPRESSION = SCHEDULER_BASE + "rest_cron_expression";

    /**
     * Context parameter within configuration-subsystem with the name
     * {@value #LOCALE_DEFAULT}
     * <p>
     * Defines the default locale. <em>Caution:</em> the default locale must be
     * defined within {@value #LOCALES_AVAILABLE}. The default value for the is: de
     * </p>
     */
    public static final String LOCALE_DEFAULT = LOCALE_BASE + "default";

    /**
     * Context parameter within configuration-subsystem with the name
     * {@value #LOCALES_AVAILABLE}
     * <p>
     * Defines the available locales for for the cdi-portal as a comma separated
     * list. The default value for the is: en,de,fr
     * </p>
     */
    public static final String LOCALES_AVAILABLE = LOCALE_BASE + "available";

    /**
     * The separator for cui-specific context-parameter.
     */
    public static final char CONTEXT_PARAM_SEPARATOR = ',';

    /**
     * Context parameter within configuration-subsystem with the name
     * portal.resource.handled_libraries
     * <p>
     * A comma separated list of libraries that should be handled by the
     * CuiResourceHandler. Handled by the resource handler means selecting the
     * .min-version of the resource is available and adding a cache-buster to the
     * resource request.
     * </p>
     */
    public static final String RESOURCE_HANDLED_LIBRARIES = RESOURCE_BASE + "handled_libraries";

    /**
     * Context parameter within configuration-subsystem with the name
     * portal.resource.handled_suffixes
     * <p>
     * A comma separated list of suffixes that should be handled by the
     * CuiResourceHandler. Handled by the resource handler means selecting the
     * .min-version of the resource is available and adding a cache-buster to the
     * resource request. The default value for the CDI portal is:
     * "eot,ttf,svg,js,css,woff"
     * </p>
     */
    public static final String RESOURCE_HANDLED_SUFFIXES = RESOURCE_BASE + "handled_suffixes";

    /**
     * Context parameter within configuration-subsystem with the name
     * portal.resource.version
     * <p>
     * The String used for the cache buster for the resources under the control. The
     * default value for the CDI portal is: The corresponding cui version, e.g.
     * "4.2"
     * </p>
     */
    public static final String RESOURCE_VERSION = RESOURCE_BASE + "version";

    /**
     * Context parameter within configuration-subsystem with the name
     * portal.resource.maxAge
     * <p>
     * To allow caching of specific resources it allows to specify how long (in
     * minutes) the browser does not need to retrieve the resource again. It will
     * not be used in {@link ProjectStage#DEVELOPMENT}.
     * </p>
     */
    public static final String RESOURCE_MAXAGE = RESOURCE_BASE + "maxAge";

    /**
     * Context parameter within configuration-subsystem with the name
     * portal.theme.default
     * <p>
     * The configured default theme. The default value for the CDI portal is
     * 'Default' <em>Caution: </em> In order to work the default theme must be
     * defined at {@value #THEME_AVAILABLE}
     * </p>
     */
    public static final String THEME_DEFAULT = THEME_BASE + "default";

    /**
     * Context parameter within configuration-subsystem with the name
     * portal.theme.available_themes
     * <p>
     * The configured available themes as comma separated list. The default value
     * for the CDI portal is 'Default,High-Contrast'
     * </p>
     */
    public static final String THEME_AVAILABLE = THEME_BASE + "available_themes";

    /**
     * Context parameter within configuration-subsystem with the name
     * portal.view.non_secured
     * <p>
     * Usually all our applications need authentication. This parameter defines the
     * views / partial trees that do not need any authentication in order to be
     * displayed, as comma separated list. Caution: The views are relative to the
     * root, usually starting with /faces. The distinct values are to be checked
     * using String#startsWith(). The default for the portal is '/guest/'. In order
     * to match all views you can use '/'.
     * </p>
     */
    public static final String NON_SECURED_VIEWS = VIEW_BASE + "non_secured";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.view.suppressed'
     * <p>
     * Defines the view that are being suppressed: The portal provides many
     * artifacts that can be overridden by concrete portals. This can be services or
     * beans, usually to be overridden using the CDI way. In addition there are
     * templates that can be overridden by our template mechanisms. But what about
     * overriding pages that are delivered from the portal, e.g.
     * faces/guest/login.jsf? If a concrete application has its own pages like in
     * pep using /pep-ui/guest/login.jsf for login. Due to the deployment mechanics
     * of the portal faces/guest/login.jsf will still be accessible. In order to
     * deal with this issue the portal view suppression mechanics may be used.
     * Caution: The views are relative to the root, usually starting with /faces.
     * The distinct values are to be checked using String#startsWith(). The default
     * for the portal is '', saying no view is to be suppressed. Multiple view
     * suppressions can be configured by using the
     * {@linkplain #CONTEXT_PARAM_SEPARATOR} character.
     * </p>
     */
    public static final String SUPPRESSED_VIEWS = VIEW_BASE + "suppressed";

    /**
     * Context parameter within configuration-subsystem with the name
     * portal.view.cache.enabled
     * <p>
     * Enables the view part cache (e.g. for the navigation menu).
     * </p>
     */
    public static final String ENABLE_CACHE = VIEW_BASE + "cache.enabled";

    /**
     * Prefix for identifying context-parameter defining the restrictions for
     * certain roles: 'portal.view.restrict.role.'.
     * The portal defines view-level
     * access rights-management.
     * All Views can be found under '/'.
     * The matching of
     * certain views is checked using String#startsWith().
     * Therefore, you can omit
     * any suffixes and define partial trees quite easily.
     * The individual views
     * and/or subtrees can be separated by colons.
     * If subtree is defined, it always
     * has to finish by '/', in case of single page without '/'.
     * A concrete view
     * restriction is always prepended by the string 'portal.view.restrict.role.'
     * Sample: portal.view.restrict.role.admin=/admin/,/content/verify will result
     * in all views under '/admin/' and the single view '/content/verify.jsf' be
     * only accessible if the user has the role 'admin'
     */
    public static final String VIEW_ROLE_RESTRICTION_PREFIX = VIEW_BASE + "restrict.role.";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.view.transient'
     * <p>
     * Defines the view that are transient, therefore not part of the state saving.
     * Caution: The views are relative to the root, usually starting with /faces.
     * The distinct values are to be checked using String#startsWith(). The default
     * for the portal is '/guest/'. In order to match all views you can use '/'.
     * </p>
     */
    public static final String TRANSIENT_VIEWS = VIEW_BASE + "transient";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.history.exclude_parameter'
     * <p>
     * Defines the url-parameter to be ignored for the server side history manager
     * as comma separated list. <em>Caution</em>: The checks are all lower-cased,
     * therefore this parameter must be lower-cased as well The default for the
     * portal is 'samlresponse,jsessionid,jfwid'
     * </p>
     */
    public static final String HISTORY_EXCLUDE_PARAMETER = HISTORY_BASE + "exclude_parameter";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.history.view_excludes'
     * <p>
     * Defines the views that are to be excluded from the server-side history, as
     * comma separated list. The distinct values are to be checked using
     * String#startsWith(). The default for the portal is '/guest/'. In order to
     * match all views you can use '/'.
     * </p>
     */
    public static final String HISTORY_VIEW_EXCLUDE_PARAMETER = HISTORY_BASE + "view_excludes";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.pages.login.logged_in_strategy'
     * <p>
     * Defines the behavior of the login page if called with an already logged-in
     * user. Currently there are two strategies available: 'goto_home' resulting in
     * a redirect to the home page and logout resulting in logging out the current
     * logged in user. The portal default is 'goto_home'
     * </p>
     */
    public static final String PAGES_LOGIN_ENTER_STRATEGY = PAGES_LOGIN_BASE + "logged_in_strategy";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.pages.login.default_user_store'
     * <p>
     * Define configuration for default selected user store. If no valid setting is
     * defined first available user store should be used.
     * </p>
     */
    public static final String PAGES_LOGIN_DEFAULT_USER_STORE = PAGES_LOGIN_BASE + "default_user_store";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.pages.error.404_redirect_to_home'
     * <p>
     * Defines the the behavior of the 404 / resource not found view. If set to
     * 'true' the application will implicitly redirect to the home-view If set to
     * 'false' it displays the 'home' link only The portal default is 'true'
     * </p>
     */
    public static final String PAGES_ERROR_404_REDIRECT = PAGES_ERROR_BASE + "404_redirect_to_home";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.storage.cookieMaxAge'
     * <p>
     * Defines the maximum age of the cookie in seconds; if negative, means the
     * cookie is not stored.
     * </p>
     */
    public static final String CLIENT_STORAGE_COOKIE_MAX_AGE = STORAGE_BASE + "cookieMaxAge";

    private static final String CSP_BASE = HTTP_HEADER_BASE + "csp.";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.httpHeader.csp.enabled'
     * <p>
     * Defines an optional header parameter to enable the Content Security Policy.
     * </p>
     * Valid values are:
     * <ul>
     * <li>false: the header will not be send</li>
     * <li>true: the header will be send</li>
     * </ul>
     */
    public static final String CSP_ENABLED = CSP_BASE + ENABLED;

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.httpHeader.csp.views'
     * <p>
     * Defines for which views the csp header should be added.
     * </p>
     */
    public static final String CSP_VIEWS = CSP_BASE + "views";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.httpHeader.csp.content'
     * <p>
     * Defines the content of the csp header.
     * </p>
     */
    public static final String CSP_CONTENT = CSP_BASE + "content";

    /**
     * Context parameter for the customization directory with the name:
     * 'portal.customization.dir'
     * <p>
     * Used to locate the folder for a customization. If this parameter is not
     * present the default is
     * {@linkplain #PORTAL_CONFIG_DIR}/{@linkplain #PORTAL_CUSTOMIZATION_DIR_DEFAULT}.
     * </p>
     */
    public static final String PORTAL_CUSTOMIZATION_DIR = CUSTOMIZATION_BASE + "dir";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.customization.enabled'
     * <p>
     * Used for enabling / disabling the customization. Defaults to {@code true}
     * </p>
     */
    public static final String PORTAL_CUSTOMIZATION_ENABLED = CUSTOMIZATION_BASE + ENABLED;

    /**
     * Defines the timeout to retrieve a backend request in seconds. After this
     * timeout the request will be aborted.
     */
    public static final String PORTAL_LAZY_LOADING_REQUEST_RETRIEVE_TIMEOUT = LAZY_LOADING_BASE
            + "request.retrieve.timeout";

    /**
     * Defines the timeout to handle a backend request in seconds. After this
     * timeout the request will be aborted. This can happen if the initial rendering
     * or the ajax update request was aborted because of redirect.
     */
    public static final String PORTAL_LAZY_LOADING_REQUEST_HANDLE_TIMEOUT = LAZY_LOADING_BASE + "request.handle.timeout";

    /**
     * Default portal customization directory.
     */
    public static final String PORTAL_CUSTOMIZATION_DIR_DEFAULT = "customization/";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.listener.StickyMessages.enabled'
     * <p>
     * Defines whether the listener for StickyMessages is enabled. Defaults to
     * {@code true}
     * </p>
     */
    public static final String PORTAL_LISTENER_STICKY_MESSAGES = LISTENER_BASE + "StickyMessages.enabled";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.listener.HistoryManager.enabled'
     * <p>
     * Defines whether the listener for HistoryManager is enabled. Defaults to
     * {@code true}
     * </p>
     */
    public static final String PORTAL_LISTENER_HISTORY_MANAGER = LISTENER_BASE + "HistoryManager.enabled";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.listener.Authentication.enabled'
     * <p>
     * Defines whether the listener for Authentication is enabled. Defaults to
     * {@code true}
     * </p>
     */
    public static final String PORTAL_LISTENER_AUTHENTICATION = LISTENER_BASE + "Authentication.enabled";

    private static final String PORTAL_LISTENER_TRACE_BASE = LISTENER_BASE + "Trace.";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.listener.Trace.enabled'
     * <p>
     * Defines whether the listener for tracing faces-requests is enabled. Defaults
     * to {@code false}. In order to activate tracing on the application log you
     * must set the Logger for de.cuioss.portal.ui.runtime.application.listener.metrics.RequestTracer
     * to debug. In case of metrics being enabled as well it will register as
     * metric.
     * </p>
     */
    public static final String PORTAL_LISTENER_TRACE_ENABLED = PORTAL_LISTENER_TRACE_BASE + ENABLED;

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.listener.Authorization.enabled'
     * <p>
     * Defines whether the listener for Authorization is enabled. Defaults to
     * {@code true}
     * </p>
     */
    public static final String PORTAL_LISTENER_AUTHORIZATION = LISTENER_BASE + "Authorization.enabled";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.listener.ViewSuppression.enabled'
     * <p>
     * Defines whether the listener for ViewSuppression is enabled. Defaults to
     * {@code true}
     * </p>
     */
    public static final String PORTAL_LISTENER_VIEW_SUPPRESSION = LISTENER_BASE + "ViewSuppression.enabled";

    /**
     * Context parameter within configuration-subsystem with the name:
     * 'portal.authentication.servlet.allowBasicAuth'
     * <p>
     * Used in the servlet authentication context to check whether BASIC
     * authentication is allowed. Valid values are: true|false Default value: true
     * </p>
     */
    public static final String PORTAL_SERVLET_BASIC_AUTH_ALLOWED = PORTAL_BASE
            + "authentication.servlet.allowBasicAuth";
}
