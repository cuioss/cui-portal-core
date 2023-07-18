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
