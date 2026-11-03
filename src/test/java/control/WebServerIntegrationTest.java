package control;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

// Integration tests: a real WebServer on an ephemeral port
// (port 0, so tests never clash with the real 3002), a
// Mockito-mocked ControllerInterface, and a real HttpClient.
// No browser needed. The mocked controller lets these tests
// prove routing, cookies, and gating.
// Run: .\gradlew test --tests "control.WebServerIntegrationTest" --console=plain
public class WebServerIntegrationTest {
    private ControllerInterface controller;
    private WebServer server;
    private HttpClient client;
    private String base;

    @BeforeEach
    void setUp() throws Exception {
        controller = mock(ControllerInterface.class);
        server = new WebServer(controller, 0);
        server.start();
        base = "http://localhost:" + server.getPort();
        client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    // Logs in through the mocked controller and returns the raw
    // session cookie value, e.g. "session=<token>". Also asserts
    // the login contract: a 302, a session cookie, and no expiry
    // attributes (a session cookie dies with the browser).
    private String loginAndGetCookie() throws Exception {
        when(controller.verifyLogin("alice", "Pass123")).thenReturn(true);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(base + "/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("username=alice&password=Pass123"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());
        String setCookie = response.headers().firstValue("Set-Cookie").orElse("");
        assertTrue(setCookie.contains("session="));
        assertFalse(setCookie.contains("Max-Age="));
        assertFalse(setCookie.contains("Expires="));
        return setCookie.split(";")[0].trim();
    }

    @Test
    void unauthenticatedRootShowsLoginForm() throws Exception {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create(base + "/")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("<form"));
    }

    @Test
    void invalidLoginShowsErrorWithoutCookie() throws Exception {
        // Mockito's default is false, so no stubbing: bad
        // credentials fail on their own.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(base + "/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("username=alice&password=Wrong"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Invalid username or password."));
        assertTrue(response.headers().firstValue("Set-Cookie").isEmpty());
    }

    @Test
    void validLoginSetsSessionCookie() throws Exception {
        loginAndGetCookie();
    }

    @Test
    void authenticatedBrowseCallsController() throws Exception {
        String cookie = loginAndGetCookie();
        when(controller.listEntries("alice", "briefs")).thenReturn("1 : Title");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(base + "/browse?type=briefs"))
                .header("Cookie", cookie)
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("1 : Title"));
        verify(controller).listEntries("alice", "briefs");
    }

    @Test
    void browseWithoutCookieShowsLoginForm() throws Exception {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create(base + "/browse?type=briefs")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertTrue(response.body().contains("<form"));
        verifyNoInteractions(controller);
    }

    @Test
    void unknownPathReturnsErrorPageNotCrash() throws Exception {
        String cookie = loginAndGetCookie();
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create(base + "/nope"))
                        .header("Cookie", cookie).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Unknown page."));
    }

    @Test
    void logoutClearsSession() throws Exception {
        String cookie = loginAndGetCookie();
        client.send(HttpRequest.newBuilder().uri(URI.create(base + "/logout"))
                        .header("Cookie", cookie).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create(base + "/browse?type=briefs"))
                        .header("Cookie", cookie).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertTrue(response.body().contains("<form"));
    }

    @Test
    void auditRouteCallsController() throws Exception {
        String cookie = loginAndGetCookie();
        when(controller.reviewAuditLog("alice")).thenReturn("2026-10-29 : alice : read : briefs 1");
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create(base + "/audit"))
                        .header("Cookie", cookie).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertTrue(response.body().contains("2026-10-29 : alice : read : briefs 1"));
        verify(controller).reviewAuditLog("alice");
    }
}
