Please add a microprofile rest implementation to the classpath - if not using cui parent poms.

## Adding RestEasy dependencies
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
