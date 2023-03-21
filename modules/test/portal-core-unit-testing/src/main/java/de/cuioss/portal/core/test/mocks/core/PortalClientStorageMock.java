package de.cuioss.portal.core.test.mocks.core;

import javax.enterprise.context.ApplicationScoped;

import de.cuioss.portal.core.storage.ClientStorage;
import de.cuioss.portal.core.storage.PortalClientStorage;
import de.cuioss.portal.core.storage.impl.MapStorageImpl;
import lombok.ToString;

/**
 * Mock Variant of {@link PortalClientStorage}
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@PortalClientStorage
@ToString
public class PortalClientStorageMock extends MapStorageImpl<String, String> implements ClientStorage {

    private static final long serialVersionUID = -4658738277163105697L;

}
