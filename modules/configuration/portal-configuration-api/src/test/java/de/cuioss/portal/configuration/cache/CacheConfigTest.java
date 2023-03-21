package de.cuioss.portal.configuration.cache;

import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.contracts.VerifyConstructor;

@VerifyConstructor(of = { "expiration", "timeUnit", "size", "recordStatistics" })
class CacheConfigTest extends ValueObjectTest<CacheConfig> {

}
