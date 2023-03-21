package de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher;

@SuppressWarnings("javadoc")
public class PassThroughDispatcher implements ModuleDispatcherElement {

    public static final String BASE = "/pass";

    @Override
    public String getBaseUrl() {
        return BASE;
    }

}
