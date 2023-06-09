# portal-unit-testing-junit5

## Status
[![Build Status](https://ehf-ux-build-trunk.ci.dev.icw.int/jenkins/view/All%20(eHF%20CUI)/job/ehf-cui-ehf-common-ui/badge/icon)](https://ehf-ux-build-trunk.ci.dev.icw.int/jenkins/view/All%20(eHF%20CUI)/job/ehf-cui-ehf-common-ui/)

## What is it?
As part of the migration to Junit 5 we restructured the unit-testing of portal-modules as well. It is about mock-classes, base-classes, test-infrastructure and extensions (Junit 5). This is the starting point for all unit-test in the context of the cui-portal. 


## Maven Coordinates
```xml
<dependency>
     <groupId>de.cuioss.portal.core.test</groupId>
     <artifactId>portal-unit-testing-junit5</artifactId>
</dependency>
```

## Usage
### Configuration
The handling of configuration is simplified. The @EnablePortalConfiguration bootstraps the configuration-sub-system and adds the necessary producer by using @AddBeanClasses.

Caution: part of the migration is using micro-profile-configuration as a recommended step. Sounds complicated but in essence you change your imports from org.apache.deltaspike.core.api.config.ConfigProperty to org.eclipse.microprofile.config.inject.ConfigProperty. The usage is the same.

```java
@EnableAutoWeld
@EnablePortalConfiguration
class ConfigurationEnabledTest {
```
If you stick to deltaspike-config you need to add the corresponding producer as well

```java
@EnableAutoWeld
@EnablePortalConfiguration
@AddBeanClasses({ DefaultConfigPropertyProducer.class })
class ConfigurationEnabledTest {
```

Additional configuration can be added either by the annotation, for all tests:

```java
@EnableAutoWeld
@EnablePortalConfiguration(configuration = {"key1:value1", "key2:value2"})
class ConfigurationEnabledTest {
```

or by injecting the PortalTestConfiguration for applying in the context of certain tests

```java
@EnableAutoWeld
@EnablePortalConfiguration
class ConfigurationEnabledTest {

    @Inject
    @PortalConfigurationSource
    private PortalTestConfiguration configuration;
    
    @​Test
    void shouldAdjustConfiguration() {
        configuration.fireEvent("someKey", "someValue");
    }
```
Caution: de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration is the successor of com.icw.ehf.cui.portal.configuration.PortalConfigurationMock that will be deprecated soon.

### Testing in a CDI / Portal / JSF Context
The quickest variant is using @EnablePortalUiEnvironment. This is the typical one-stop annotation for testing UI-related modules in the portal-context.

```java
@EnablePortalUiEnvironment
class EnablePortalUiEnvironmentTest {

    @Inject
    private Provider<FacesContext> facesContext;

    @Inject
    @PortalConfigurationSource
    private PortalTestConfiguration configuration;

    @​Test
    void shouldEnableEnvironment() {
        assertNotNull(facesContext.get());
        assertNotNull(configuration);
        assertEquals(BasicApplicationConfiguration.FIREFOX,
                facesContext.get().getExternalContext().getRequestHeaderMap()
                        .get(BasicApplicationConfiguration.USER_AGENT));
    }
```

If you want to access views / navigation with the standard portal views like login, preferences and so on you can add:

```java
@EnablePortalUiEnvironment
@JsfTestConfiguration(PortalTestNavigationConfiguration.class)
class EnablePortalUiEnvironmentTest {
```

### Testing a page / Backing bean
There is a base class for testing Backing beans in a convenient manner:
de.cuioss.portal.core.test.tests.AbstractPageBeanTest<T> 
Minimal sample, testing the wiring and the object-contracts:

```java
@EnablePortalUiEnvironment
class ViewBeanTest extends AbstractPageBeanTest<ViewBean> {

    @Inject
    @Getter
    private ViewBean underTest;

}
```

in case your bean needs an initViewAction to be called prior testing this can be done within #getUnderTest():

```java
@EnablePortalUiEnvironment
class ViewBeanTest extends AbstractPageBeanTest<ViewBean> {

    @Inject
    private ViewBean underTest;
    
    @Override
    public ViewBean getUnderTest() {
        underTest.initBean();
        return underTest;
    }

}
```
Automatic bean-testing can be introduced by annotating with @VerifyBeanProperty. The further configuration is defined within ValueObjectTest

```java
@EnablePortalUiEnvironment
@VerifyBeanProperty
class ViewBeanTest extends AbstractPageBeanTest<ViewBean> {

    @Inject
    @Getter
    private ViewBean underTest;

}
```

verifyObjectContracts(): Verifies the contract of Object.equals(Object), Object.hashCode(), Object.toString() and Serializable by serializing / deserializing the object. 

*Caution*: it will check less detailed compared to previous variants. If you want a full blown test use ValueObjectTest directly. If you want to adapt the test you must override it. Previous annotation will not work.

### Testing a ResourceBundle-Locator
This tests the initial structure, the provided path and an actual message to be resolved. In order to work the module needs a test / runtime dependency on com.icw.ehf.cui.portal.cdi:cdi-portal-core-impl

```java
@EnableAutoWeld
@AddBeanClasses({ PortalResourceBundleBean.class, PortalProjectStageImpl.class, PortalResourceBundleWrapper.class,
    ResourceBundleRegistryImpl.class, PortalLocaleProducerMock.class })
@ActivateScopes(SessionScoped.class)
class RuntimeBundleNamesTest implements ShouldBeNotNull<RuntimeBundleNames> {

    @Inject
    @Getter
    @PortalResourceBundleLocator
    private RuntimeBundleNames underTest;

    @Inject
    @PortalResourceBundle
    private ResourceBundle bundle;

    @​Test
    void shouldProvideAtLeastOneBundle() {
        assertEquals("Runtime Configuration", bundle.getString("portal.runtime.configuration.default.title"));
    }

}
```

### Testing with PortalMessageProducer
If you experience: `Could not load bean of type com.icw.ehf.cui.core.api.application.message.MessageProducer and name #{messageProducer}`
setup your test class as following:

```
@EnablePortalUiEnvironment
class MyTestClass extends AbstractPageBeanTest|AbstractValidatorTest|... { 

    @Inject
    @PortalMessageProducer
    private PortalMessageProducerMock messageProducer;

    @BeforeEach
    void beforeTest() {
        getBeanConfigDecorator().register(messageProducer, MessageProducerMock.BEAN_NAME);
    }
}
```

### Module Consistency Test
A consistent module must be deployable within a CDI-Context. It must define all dependencies. This is checked by de.cuioss.portal.core.test.tests.BaseModuleConsistencyTest the successor of de.icw.cui.test.cdi.BaseCdiContextTest that will be deprecated soon.

```java
class ModuleConsistencyTest extends BaseModuleConsistencyTest {
}
```
The test actually starts a Weld-Container and looks up the Bean-Manager. In addition it checks for the file META-INF/beans.xml being present. 

### Assembly Consistency Test
A consistent assembly must be deployable within a CDI-Context. It must define all dependencies. This is checked by de.cuioss.portal.core.test.tests.BaseAssemblyConsistencyTest.

```java
class AssemblyConsistencyTest extends BaseAssemblyConsistencyTest {
}
```
The test actually starts a Weld-Container and looks up the Bean-Manager. In addition it checks for the file WEB-INF/beans.xml being present. 

### Handling AuthenticatedUserInfo
If you need an AuthenticatedUserInfo, add de.cuioss.portal.core.test.mocks.authentication.PortalTestUserProducer, the successor of de.icw.portal.core.test.mocks.PortalUserProducerMock that will be deprecated soon.

```java
@EnableAutoWeld
@AddBeanClasses(PortalTestUserProducer.class)
class PortalTestUserProducerTest {

    @Inject
    private PortalTestUserProducer userProducer;

    @Inject
    @PortalUser
    private Provider<AuthenticatedUserInfo> userProvider;

    @​Test
    void shouldProduceUser() {
        assertNotNull(userProvider.get());
        assertTrue(userProvider.get().isAuthenticated());
        userProducer.authenticated(false);
        assertFalse(userProvider.get().isAuthenticated());
    }
```

### Testing a configuration Module
Extend de.cuioss.portal.core.test.tests.configuration.AbstractConfigurationKeyVerifierTest, the successor of com.icw.ehf.cui.portal.configuration.AbstractConfigurationKeyVerifierTest that will be deprecated soon.
The actual documentation can be found at class level

### Testing NavigationMenuItems
The base class is de.cuioss.portal.core.test.tests.navigation.PortalNavigationMenuItemsTest replacing de.icw.portal.core.test.navigation.PortalNavigationMenuItemPackageTest that will be deprecated soon

```java
@EnablePortalUiEnvironment
@AddBeanClasses({ AboutMenuItem.class, AccountMenuItem.class})
class PortalNavigationMenuItemsTestTest extends PortalNavigationMenuItemsTest implements BeanConfigurator {

    @​Test
    void shouldFilterNoType() {
        assertEquals(2, getFilteredInstances().size());
    }

    @Override
    public void configureBeans(BeanConfigDecorator decorator) {
        decorator.register(new PortalMirrorResourceBundle());

    }
```

### Using EasyMock
**!!! Attention !!!** using easymock with JUnit 5 + CDI require easy mock version 4.1 or higher additional infos see [Easymock user-guide](https://easymock.org/user-guide.html)
```java
@ExtendWith(EasyMockExtension.class)
@EnableAutoWeld
class SomeTestClass {

    @Produces
    @Mock
    private SomeMockedService mockedOne;

}
```

### Using MockWebServer
This module supports an extension for using [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver). You need add the dependency first:

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>mockwebserver</artifactId>
</dependency>
```
now you can use it in your tests:

```java
@EnableMockWebServer
class MockWebServerExtensionTest implements MockWebServerHolder {

    @Setter
    private MockWebServer mockWebServer;

    @​Test
    void shouldHandleMockWebServer() {
        assertNotNull(mockWebServer);
    }
}
```

#### Dispatching Requests
If you want to reuse the request dispatching of the server you can provide a concrete implementation for de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder.getDispatcher()

```java
@EnableMockWebServer
class ValueSetClientImplFhirTest implements MockWebServerHolder {

    static final FileLoader CONFORMANCE =
        CuiFileUtil.getLoaderForPath(FileTypePrefix.CLASSPATH + "/fhir/conformance.xml");

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                switch (request.getPath()) {
                    case "/metadata":
                        return new MockResponse().setResponseCode(HttpServletResponse.SC_OK)
                                .addHeader("Content-Type", "application/fhir+xml")
                                .setBody(CuiFileUtil.toStringUnchecked(CONFORMANCE));
                    case "/ValueSet/C_GE_DRR_PRACTICESETTING_CODE":
                        return new MockResponse().setResponseCode(HttpServletResponse.SC_OK)
                                .addHeader("Content-Type", "application/fhir+xml")
                                .setBody(CuiFileUtil.toStringUnchecked(CONTENT));
                    default:
                        return new MockResponse().setResponseCode(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        };
    }
}
```

#### de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.ModuleDispatcherElement

The idea of an ModuleDispatcherElement is the reuse of answers in the context of EnableMockWebServer. In essence calls to MockWebServerHolder.getDispatcher() can be replaced with this structure.The general idea is to return an Optional MockResponse if the concrete handle can answer the call, Optional.empty() otherwise.

Dispatcher for a jwks endpoint 

```java
/**
 * Handles the Resolving of JWKS Files from the Mocked oauth-Server. In essence it returns the file
 * "src/test/resources/token/test-public-key.jwks"
 */
public class JwksResolveDispatcher implements ModuleDispatcherElement {

    /** "/oidc/jwks.json" */
    public static final String LOCAL_PATH = "/oidc/jwks.json";

    @Getter
    @Setter
    private int callCounter = 0;

    @Override
    public Optional<MockResponse> handleGet(@NonNull RecordedRequest request) {
        callCounter++;
        return Optional.of(new MockResponse().addHeader("Content-Type", "application/json")
                .setBody(FileLoaderUtility
                        .toStringUnchecked(FileLoaderUtility.getLoaderForPath(PUBLIC_KEY_JWKS)))
                .setResponseCode(SC_OK));
    }

    @Override
    public String getBaseUrl() {
        return LOCAL_PATH;
    }

    /**
     * Verifies whether this endpoint was called the given times
     *
     * @param expected
     */
    public void assertCallsAnswered(int expected) {
        assertEquals(expected, callCounter);
    }
}
```

Can now be reused like this: 

```java
@EnableAutoWeld
@EnablePortalConfiguration
@EnableMockWebServer
class TokenParserProducerTest implements ShouldBeNotNull<TokenParserProducer>, MockWebServerHolder {

    @Setter
    private MockWebServer mockWebServer;

    protected int mockserverPort;

    private JwksResolveDispatcher jwksResolveDispatcher = new JwksResolveDispatcher();

    @Getter
    private CombinedDispatcher dispatcher = new CombinedDispatcher().addDispatcher(jwksResolveDispatcher);

    @BeforeEach
    void setupMockServer() {
        mockserverPort = mockWebServer.getPort();
        configuration.put(VERIFY_SIGNATURE_JWKS_URL,
                "http://localhost:" + mockserverPort + jwksResolveDispatcher.getBaseUrl());
        configuration.put(VERIFY_SIGNATURE_ISSUER, TestTokenProducer.ISSUER);
        configuration.put(VERIFY_SIGNATURE_REFRESH_INTERVAL, "60");
        configuration.fireEvent();

        jwksResolveDispatcher.setCallCounter(0);
    }

    @Test
    void shouldCacheMultipleCalls() {
        jwksResolveDispatcher.assertCallsAnswered(0);
        String token = validSignedJWTWithClaims(PATIENT_ACCESS_TOKEN);
        JWTParser parser = parserProvider.get();

        for (int i = 0; i < 100; i++) {
            JsonWebToken jsonWebToken = assertDoesNotThrow(() -> ParsedToken.jsonWebTokenFrom(token, parser, LOGGER));
            assertValidJsonWebToken(jsonWebToken, token);
        }
        // For some reason there are always at least 2 calls, instead of expected one call. No
        // problem because as shown within this test, the number stays at 2
        assertTrue(jwksResolveDispatcher.getCallCounter() < 3);

        for (int i = 0; i < 100; i++) {
            JsonWebToken jsonWebToken = assertDoesNotThrow(() -> ParsedToken.jsonWebTokenFrom(token, parser, LOGGER));
            assertValidJsonWebToken(jsonWebToken, token);
        }
        assertTrue(jwksResolveDispatcher.getCallCounter() < 3);
    }
```

### Testing InstallationPaths
On some rare cases you need to access the de.cuioss.portal.configuration.installationpaths.InstallationPaths in a mock variant. This can be done by using:

```java
@EnableAlternatives(PortalInstallationPathsMock.class)
class TestWithInstalltionPaths {

    @Inject
    @PortalInstallationPaths
    private PortalInstallationPathsMock installationPaths;
    
```    
