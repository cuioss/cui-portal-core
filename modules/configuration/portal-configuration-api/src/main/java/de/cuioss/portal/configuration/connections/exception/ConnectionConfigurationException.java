package de.cuioss.portal.configuration.connections.exception;

/**
 * @author Oliver Wolff
 */
public class ConnectionConfigurationException extends ConnectionException {

    private static final long serialVersionUID = -9083478212303783709L;

    private final String message;

    /**
     * @param root
     * @param errorReason
     */
    public ConnectionConfigurationException(final Throwable root, final ErrorReason errorReason) {
        super(root, errorReason);
        message = null;
    }

    /**
     * @param root
     * @param errorReason
     * @param message
     */
    public ConnectionConfigurationException(final Throwable root, final ErrorReason errorReason, final String message) {
        super(root, errorReason);
        this.message = message;
    }

    /**
     * @param errorReason
     */
    public ConnectionConfigurationException(final ErrorReason errorReason) {
        super(errorReason);
        message = null;
    }

    /**
     * @param errorReason
     * @param message
     */
    public ConnectionConfigurationException(final ErrorReason errorReason, final String message) {
        super(errorReason);
        this.message = message;
    }

    @Override
    public String getMessage() {
        if (null != message) {
            return message;
        }
        return getErrorReason().getErrorText();
    }

}
