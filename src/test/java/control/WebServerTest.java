package controller;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

// Unit tests for the week-1 WebServer stub. There is nothing
// to assert beyond "it constructs and its lifecycle methods do
// not throw": routing, sessions, and HTTP behavior arrive in
// week 2, when these are replaced by integration tests against
// a real server (the week-2 integration file is in recoverable
// trash until then).
// Run: .\gradlew test --tests "controller.WebServerTest" --console=plain
public class WebServerTest {
    @Test
    void startAndStopDoNotThrow() throws Exception {
        WebServer server = new WebServer(mock(ControllerInterface.class), 0);
        server.start();
        server.stop();
    }

    @Test
    void stopBeforeStartDoesNotThrow() {
        WebServer server = new WebServer(mock(ControllerInterface.class), 0);
        server.stop();
    }
}

