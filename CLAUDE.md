# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Full build (all modules)
./mvnw clean verify

#Quality Build
./mvnw clean verify -Ppre-commit

# Build without tests
./mvnw clean install -DskipTests

# Run tests for a single module (from repo root)
./mvnw test -pl modules/core/portal-configuration

# Run a single test class
./mvnw test -pl modules/core/portal-configuration -Dtest=ConfigurationProducerTest

# Run a single test method
./mvnw test -pl modules/core/portal-configuration -Dtest=ConfigurationProducerTest#shouldProduceSplittedList

# Build with release profiles (javadoc validation)
./mvnw clean verify -Pjavadoc
```

Java 21 is required. The project uses the Maven Wrapper (`./mvnw`).

## Project Architecture

This is a multi-module Maven project providing core infrastructure for CUI Portal applications. Parent POM: `de.cuioss:cui-java-parent`.

### Module Hierarchy

```
cui-portal-core (reactor root)
├── bom/                              # BOM for dependency management
└── modules/
    ├── authentication/               # Authentication subsystem
    │   ├── portal-authentication-api     # Core auth interfaces (AuthenticatedUserInfo, facades)
    │   ├── portal-authentication-oauth   # OAuth/OIDC implementation
    │   ├── portal-authentication-mock    # Mock implementation for testing (scope: test)
    │   └── portal-authentication-dummy   # Dummy implementation (scope: runtime)
    ├── core/                         # Core portal services
    │   ├── portal-common-cdi             # CDI utilities, bundles, locale, project stages, priorities
    │   ├── portal-configuration          # MicroProfile Config extensions, config types, file watchers
    │   └── portal-core                   # Servlet core: listeners, storage, user management
    ├── micro-profile/                # MicroProfile integrations
    │   ├── portal-mp-rest-client         # REST client builder with auth filters and logging
    │   └── portal-metrics-api            # Metrics API abstractions
    └── test/
        └── portal-core-unit-testing      # Test infrastructure: JUnit 5 extensions, mocks, base tests
```

### Key Packages

- `de.cuioss.portal.configuration` - Configuration keys (`PortalConfigurationKeys`), defaults, custom config types (`@ConfigAsList`, `@ConfigAsSet`, `@ConfigAsFileLoader`, `@ConfigAsCacheConfig`, `@ConfigPropertyNullable`)
- `de.cuioss.portal.authentication` - `AuthenticatedUserInfo` interface, login/user events, `AuthenticationFacade`
- `de.cuioss.portal.common` - CDI support (`PortalBeanManager`), locale handling, `ProjectStage`, priority annotations
- `de.cuioss.portal.core` - Servlet listeners, session storage, user info management
- `de.cuioss.portal.restclient` - `CuiRestClientBuilder` with token/basic auth filters

### Technology Stack

- **Jakarta EE 10** (CDI, Servlet, Interceptors)
- **MicroProfile Config** (via SmallRye implementation for tests)
- **Lombok** (with `@LombokGenerated` annotation enabled for coverage exclusion)
- **Weld** as CDI container (weld-junit5 for testing)

### Testing Patterns

- **CDI tests**: Use `@EnableAutoWeld` + custom `@Enable*` annotations (e.g., `@EnablePortalConfigurationLocal`) to bootstrap CDI in tests
- **Value object tests**: Extend `ValueObjectTest<T>` with `@VerifyConstructor` for automated contract testing
- **ModuleConsistencyTest**: Every CDI module has one - verifies the Weld container starts and `beans.xml` exists
- **Test logger**: `@EnableTestLogger` with `cui-test-juli-logger` for log assertion
- **Configuration in tests**: Inject `PortalTestConfiguration` (or `PortalTestConfigurationLocal`), use `.put(key, value)` and `.fireEvent()` to simulate config changes
- **Mocking**: EasyMock (not Mockito)

### Configuration System

Configuration is built on MicroProfile Config with custom extensions. Config files: `META-INF/microprofile-config.properties` (module defaults) and `application.yml` (installation config from `portal.configuration.dir`). The `FileWatcherService` monitors config files for runtime changes.

### CDI Conventions

- All CDI modules include `META-INF/beans.xml`
- Custom qualifier annotations for portal-specific injection points
- `@PortalPriority` for ordering CDI alternatives/decorators
- Project stages: `DEVELOPMENT`, `TEST`, `CONFIGURATION`, `PRODUCTION`

## CI/CD

- GitHub Actions using cuioss organization reusable workflows
- SonarCloud for code quality (project key: `cuioss_cui-portal-core`)
- Maven profiles: `release-snapshot`, `release`, `javadoc`

## Git Workflow

All cuioss repositories have branch protection on `main`. Direct pushes to `main` are never allowed. Always use this workflow:

1. Create a feature branch: `git checkout -b <branch-name>`
2. Commit changes: `git add <files> && git commit -m "<message>"`
3. Push the branch: `git push -u origin <branch-name>`
4. Create a PR: `gh pr create --repo cuioss/cui-portal-core --head <branch-name> --base main --title "<title>" --body "<body>"`
5. Wait for CI + Gemini review (waits until checks complete): `gh pr checks --watch`
6. **Handle Gemini review comments** — fetch with `gh api repos/cuioss/cui-portal-core/pulls/<pr-number>/comments` and for each:
   - If clearly valid and fixable: fix it, commit, push, then reply explaining the fix and resolve the comment
   - If disagree or out of scope: reply explaining why, then resolve the comment
   - If uncertain (not 100% confident): **ask the user** before acting
   - Every comment MUST get a reply (reason for fix or reason for not fixing) and MUST be resolved
7. Do **NOT** enable auto-merge unless explicitly instructed. Wait for user approval.
8. Return to main: `git checkout main && git pull`
