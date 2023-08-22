/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.authentication;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Interface of bean which represents an encapsulates the current user specific
 * information in session context. It should call data from authentication
 * facade on creation and then hold and provide this information for any other
 * beans.
 *
 * @author Stephan Babkin
 */
@SuppressWarnings("squid:S1214") // We allow constants in interfaces, if they belong together
                                 // (coherence).
public interface AuthenticatedUserInfo extends Serializable {

    /**
     * Bean name for looking up instances.
     *
     * @deprecated owolff: Should solely be used with types
     */
    @Deprecated(forRemoval = true)
    String BEAN_NAME = "authenticatedUserInfoBean";

    /**
     * Returns <code>true</code> in case of user is authenticated already and
     * <code>false</code> otherwise.
     *
     * @return <code>true</code> in case of user is authenticated already and
     *         <code>false</code> otherwise.
     */
    boolean isAuthenticated();

    /**
     * @return a list of role names assigned to the user.
     */
    List<String> getRoles();

    /**
     * Checks if is user in role.
     *
     * @param roleName the role name
     * @return true, if is user in role
     */
    default boolean isUserInRole(String roleName) {
        return getRoles().contains(roleName);
    }

    /**
     * @return a list of group names the user is assigned to.
     */
    List<String> getGroups();

    /**
     * @return the display name of the currently authenticated user
     */
    String getDisplayName();

    /**
     * @return the (technical) identifier for the currently authenticated user to be
     *         used with {@link #getSystem()}.
     */
    String getIdentifier();

    /**
     * @return the (technical) qualified identifier for the currently authenticated
     *         user to be used without {@link #getSystem()}.
     */
    String getQualifiedIdentifier();

    /**
     * @return the (technical) assigning authority for the {@link #getIdentifier()}
     *         of the currently authenticated user
     */
    String getSystem();

    /**
     * @return the context map containing additional runtime information belonging
     *         to the {@link AuthenticatedUserInfo}
     */
    Map<Serializable, Serializable> getContextMap();

}
