package control;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

// Unit tests for the week-1 WebServer stub. There is nothing
// to assert beyond "it constructs and its lifecycle methods do
// not throw": routing, sessions, and HTTP behavior are covered
// by WebServerControllerIntegrationTest, against a real server.
// Run: .\gradlew test --tests "control.WebServerTest" --console=plain
public class WebServerTest {
    @Test
    void startAndStopDoNotThrow() throws Exception {
        WebServer server = new WebServer(mock(ControllerInterface.class), mock(SessionManager.class), 0);
        server.start();
        server.stop();
    }

    @Test
    void stopBeforeStartDoesNotThrow() {
        WebServer server = new WebServer(mock(ControllerInterface.class), mock(SessionManager.class), 0);
        server.stop();
    }
}

