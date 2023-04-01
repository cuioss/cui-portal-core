package de.cuioss.portal.core.servlet;

import static de.cuioss.test.generator.Generators.letterStrings;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;

import org.apache.deltaspike.core.api.common.DeltaSpike;
import org.easymock.EasyMock;
import org.easymock.Mock;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.test.generator.junit.EnableGeneratorController;

@EnableAutoWeld
@EnableGeneratorController
@AddBeanClasses(ContextPathProducer.class)
class ContextPathProducerTest {

    @Produces
    @DeltaSpike
    @Mock
    private ServletContext servletContext;

    @Inject
    @CuiContextPath
    private Provider<String> contextPathProvider;

    String path;

    @BeforeEach
    void init() {
        path = letterStrings(2, 10).next();
        servletContext = EasyMock.createNiceMock(ServletContext.class);
        EasyMock.expect(servletContext.getContextPath()).andReturn(path);
        EasyMock.replay(servletContext);
    }

    @Test
    void test() {
        assertEquals(path, contextPathProvider.get());
    }

}
