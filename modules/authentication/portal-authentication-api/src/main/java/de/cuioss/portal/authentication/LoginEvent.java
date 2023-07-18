package de.cuioss.portal.authentication;

import java.io.Serializable;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * To signal successful / failed login and logout events (e.g. to the atna
 * logger).
 */
@Value
@Builder
public class LoginEvent implements Serializable {

    private static final long serialVersionUID = -2436530653889693514L;

    /**
     * Identifies the concrete action that was executed
     */
    public enum Action {
        /** Login was successful. */
        LOGIN_SUCCESS,

        /** Login failed. */
        LOGIN_FAILED,
        /** User logged out. */
        LOGOUT
    }

    // Only needed for LOGIN_FAILED
    private String username;

    @NonNull
    private Action action;
}
