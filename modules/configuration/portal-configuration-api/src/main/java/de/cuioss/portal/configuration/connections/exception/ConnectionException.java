package de.cuioss.portal.configuration.connections.exception;

import lombok.Getter;

/**
 * General exception to be thrown if anything goes wrong while trying to
 * <em>establish</em> the connection. Other exceptions that are specific to the
 * concrete connections are not covered.
 *
 * @author Oliver Wolff
 */
public class ConnectionException extends Exception {

    private static final long serialVersionUID = 3441459660135305431L;

    @Getter
    private final ErrorReason errorReason;

    /**
     * @param root
     * @param errorReason
     */
    public ConnectionException(final Throwable root, final ErrorReason errorReason) {
        super(root);
        this.errorReason = errorReason;
    }

    /**
     * @param errorReason
     */
    public ConnectionException(final ErrorReason errorReason) {
        this.errorReason = errorReason;
    }

}
