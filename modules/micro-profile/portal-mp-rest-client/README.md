# portal-mp-rest-client

## What is it?
A builder to build a micro-profile based rest client for a given service interface.

Expects javax.ws.rs.* annotations at the service interface.

Has no dependency to a specific implementation, assumes that a matching implementation 
(like RestEasy or JaxRS) is part of the classpath (for assembly or module unit test,
will be done automatically when using cui parent poms).
 
## Maven Coordinates
```xml
<dependency>
     <groupId>de.cuioss.portal.core.micro-profile</groupId>
     <artifactId>portal-mp-rest-client</artifactId>
</dependency>
```
## How to use in general
```java
CuiRestClientBuilder restClientBuilder = new CuiRestClientBuilder(log).connectionMetadata(connectionMeta)
        .registerExceptionMapper(new CodeSystemErrorDecoder());
try (CodeSystemRestResource resource = restClientBuilder.build(CodeSystemRestResource.class)) {
    log.debug("value-set-client: Configured to \ntype: svs\nconnection: {}\nwarnOnMissingLanguage: {}",
            connectionMeta,
            warnOnMissingLanguage);
    final SvsValueSet localeValueSet = resource.retrieveValueSet(codeCategory, locale).getValueSet();
    eventProducer.logSuccessfulImport(url, user, Collections.singleton(codeCategory));
    final List<SvsConceptList> conceptLists = localeValueSet.getConceptLists();
    if (!conceptLists.isEmpty()) {
        mappedLanguages.put(locale, conceptLists.iterator().next().getConcepts());
    }
} catch (RuntimeException e) {
    log.error(ValueSetClientImpl.PORTAL_511, e);
    configurationValid = false;
}
```

## How to use for oauth2 infrastructure including token handling according to RFC 6750
```java
CuiRestClientBuilder restClientBuilder = new CuiRestClientBuilder(log)
        .url(facadeUri.get())
        .registerExceptionMapper(new MissingScopesErrorDecoder())
        .token(token);

try (OnboardingEndpoint onboardingEndpoint = restClientBuilder.build(OnboardingEndpoint.class)) {
    OnboardingStatus onboardingStatus = onboardingEndpoint.getOnboardingStatus();
    if (null == onboardingStatus) {
        throw new IllegalStateException("Backend ErrorOnboardingService getOnboardingStatus onboardingStatus null");
    }
    return resultBuilder.result(onboardingStatus).state(ResultState.VALID).build();
} catch (MissingScopesException e) {
    log.debug("OnboardingService getOnboardingStatus MissingScopes", e);
    return resultBuilder.state(ResultState.ERROR).resultDetail(new ResultDetail(MESSAGE_ERROR_BACKEND))
            .errorCode(ErrorCodes.MISSING_SCOPES).build();
} catch (WebApplicationException e) {
    log.error("OnboardingService getOnboardingStatus Exception", e);
    CuiRestClientBuilder.debugResponse(e.getResponse(), log);
    return resultBuilder.state(ResultState.ERROR).resultDetail(new ResultDetail(MESSAGE_ERROR_BACKEND)).build();
} catch (IOException e) {
    log.error("OnboardingService getOnboardingStatus Exception", e);
    return resultBuilder.state(ResultState.ERROR).resultDetail(new ResultDetail(MESSAGE_ERROR_BACKEND)).build();
}
```
## Adding RestEasy dependencies to the assembly (already done for assemblies derived from cui parent pom)
```
        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-client-microprofile</artifactId>
            <scope>runtime</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.reactivestreams</groupId>
                    <artifactId>reactive-streams</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.jboss.spec.javax.interceptor</groupId>
                    <artifactId>jboss-interceptors-api_1.2_spec</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-client</artifactId>
            <scope>runtime</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.jboss.spec.javax.annotation</groupId>
                    <artifactId>jboss-annotations-api_1.3_spec</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-jaxb-provider</artifactId>
            <scope>runtime</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.jboss.spec.javax.xml.bind</groupId>
                    <artifactId>jboss-jaxb-api_2.3_spec</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

```
Copy and change scope to test for module unit tests.
