package de.cuioss.portal.core.test.mocks.configuration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.schedule.PortalFileWatcherService;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
class FileWatcherServiceMockTest implements ShouldBeNotNull<FileWatcherServiceMock> {

    @Inject
    @PortalFileWatcherService
    @Getter
    private FileWatcherServiceMock underTest;

    @Test
    void shouldHandlePaths() {
        assertTrue(underTest.getRegisteredPaths().isEmpty());
        underTest.register(Paths.get("/"));
        assertFalse(underTest.getRegisteredPaths().isEmpty());
        underTest.unregister(Paths.get("/"));
        assertTrue(underTest.getRegisteredPaths().isEmpty());
    }
}
