package de.cuioss.portal.core.storage;

/**
 * Used for storing information to the client. The default implementation uses
 * cookies, but other implementations like LocalStorage are possible.
 *
 * @author Matthias Walliczek
 */
public interface ClientStorage extends MapStorage<String, String> {
}
