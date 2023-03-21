package de.cuioss.portal.authentication.oauth;

/**
 * Helper class for redirecting to another (external) URL. To be used in non jsf modules.
 *
 */
public interface Redirector {

    /**
     * Redirect to another url. The url is expected to be complete and independent from the current
     * context.
     *
     * @param url the url as string
     * @throws IllegalStateException if an exception occurred during redirect.
     */
    void sendRedirect(String url) throws IllegalStateException;

}
