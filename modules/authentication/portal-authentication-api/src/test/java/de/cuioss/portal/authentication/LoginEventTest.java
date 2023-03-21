package de.cuioss.portal.authentication;

import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.contracts.VerifyBuilder;

@VerifyBuilder(required = { "action" })
class LoginEventTest extends ValueObjectTest<LoginEvent> {

}
