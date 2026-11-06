
package view;

import control.ControllerInterface;
import control.WebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebViewWebServerTest {

    private ControllerInterface controller;
    private WebServer server;
    private HttpClient client;
    private String baseUrl;

    @BeforeEach
    public void setUp() throws IOException {
        controller = mock(ControllerInterface.class);
        server = new WebServer(controller, 0);
        client = HttpClient.newHttpClient();

        server.start();
        baseUrl = "http://localhost:" + server.getPort();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    // ---------------- HTTP HELPERS ----------------

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();

        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private HttpResponse<String> getWithCookie(
            String path, String cookie
    ) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Cookie", cookie)
                .GET()
                .build();

        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private HttpResponse<String> post(
            String path, String body
    ) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header(
                        "Content-Type",
                        "application/x-www-form-urlencoded"
                )
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String login() throws Exception {
        when(controller.verifyLogin("alice", "password"))
                .thenReturn(true);

        HttpResponse<String> response = post(
                "/login",
                "username=alice&password=password"
        );

        assertEquals(302, response.statusCode());

        String cookie = response.headers()
                .firstValue("Set-Cookie")
                .orElseThrow();

        return cookie.split(";", 2)[0];
    }

    // ---------------- CONSTRUCTOR / SERVER ----------------

    @Test
    public void serverStartsOnAvailablePort() {
        assertNotNull(server);
        assertTrue(server.getPort() > 0);
    }

    @Test
    public void startCanBeCalledMoreThanOnce() throws Exception {
        int originalPort = server.getPort();

        server.start();

        assertEquals(originalPort, server.getPort());
    }

    // ---------------- LOGIN PAGE ----------------

    @Test
    public void rootDisplaysLoginPageWhenNotLoggedIn() throws Exception {
        HttpResponse<String> response = get("/");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Login"));
        assertTrue(response.body().contains("username"));
        assertTrue(response.body().contains("password"));

        verifyNoInteractions(controller);
    }

    @Test
    public void loginGetDisplaysLoginForm() throws Exception {
        HttpResponse<String> response = get("/login");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Login"));
        assertTrue(response.body().contains("action=\"/login\""));
    }

    @Test
    public void loginFailsWithIncorrectCredentials() throws Exception {
        when(controller.verifyLogin("alice", "wrong"))
                .thenReturn(false);

        HttpResponse<String> response = post(
                "/login",
                "username=alice&password=wrong"
        );

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(
                "Invalid username or password."
        ));
        assertTrue(response.body().contains("action=\"/login\""));

        assertFalse(response.headers()
                .firstValue("Set-Cookie").isPresent());

        verify(controller).verifyLogin("alice", "wrong");
    }

    @Test
    public void loginSucceedsAndCreatesSession() throws Exception {
        when(controller.verifyLogin("alice", "password"))
                .thenReturn(true);

        HttpResponse<String> response = post(
                "/login",
                "username=alice&password=password"
        );

        assertEquals(302, response.statusCode());
        assertEquals(
                "/",
                response.headers().firstValue("Location").orElse("")
        );

        String cookie = response.headers()
                .firstValue("Set-Cookie")
                .orElseThrow();

        assertTrue(cookie.startsWith("session="));
        assertTrue(cookie.contains("HttpOnly"));

        verify(controller).verifyLogin("alice", "password");
    }

    // ---------------- MAIN MENU ----------------

    @Test
    public void rootDisplaysMainMenuAfterLogin() throws Exception {
        String cookie = login();

        when(controller.showMainMenu("alice")).thenReturn(
                "Browse Briefs Browse Agents Browse Facilities "
                        + "Browse Missions Review Audit Log"
        );

        HttpResponse<String> response = getWithCookie("/", cookie);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Main Menu"));
        assertTrue(response.body().contains("Browse Briefs"));
        assertTrue(response.body().contains("Browse Agents"));
        assertTrue(response.body().contains("Review Audit Log"));
        assertTrue(response.body().contains("/logout"));

        verify(controller).showMainMenu("alice");
    }

    // ---------------- BROWSE ----------------

    @Test
    public void browseDisplaysRecords() throws Exception {
        String cookie = login();

        when(controller.listEntries("alice", "briefs"))
                .thenReturn("Brief ID: 1 Title: Mission Brief");

        HttpResponse<String> response = getWithCookie(
                "/browse?type=briefs",
                cookie
        );

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Browse briefs"));
        assertTrue(response.body().contains("Mission Brief"));
        assertTrue(response.body().contains("Read a Record"));

        verify(controller).listEntries("alice", "briefs");
    }

    @Test
    public void browseHandlesMissingType() throws Exception {
        String cookie = login();

        when(controller.listEntries("alice", ""))
                .thenReturn("No records.");

        HttpResponse<String> response = getWithCookie(
                "/browse",
                cookie
        );

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Browse"));
        assertTrue(response.body().contains("No records."));

        verify(controller).listEntries("alice", "");
    }

    // ---------------- READ ----------------

    @Test
    public void readDisplaysSelectedRecord() throws Exception {
        String cookie = login();

        when(controller.readEntry("alice", "briefs", 12))
                .thenReturn("Brief ID: 12 Mission: Operation Alpha");

        HttpResponse<String> response = getWithCookie(
                "/read?type=briefs&id=12",
                cookie
        );

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Read briefs"));
        assertTrue(response.body().contains("Operation Alpha"));
        assertTrue(response.body().contains("Back to Browse briefs"));

        verify(controller).readEntry("alice", "briefs", 12);
    }

    @Test
    public void readHandlesInvalidID() throws Exception {
        String cookie = login();

        HttpResponse<String> response = getWithCookie(
                "/read?type=briefs&id=invalid",
                cookie
        );

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Invalid option"));

        verify(controller, never()).readEntry(
                anyString(), anyString(), anyInt()
        );
    }

    // ---------------- AUDIT LOG ----------------

    @Test
    public void auditDisplaysAuditLog() throws Exception {
        String cookie = login();

        when(controller.reviewAuditLog("alice"))
                .thenReturn("User alice logged in.");

        HttpResponse<String> response = getWithCookie(
                "/audit",
                cookie
        );

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Audit Log"));
        assertTrue(response.body().contains("User alice logged in."));

        verify(controller).reviewAuditLog("alice");
    }

    // ---------------- AUTHENTICATION ----------------

    @Test
    public void browseRequiresLogin() throws Exception {
        HttpResponse<String> response = get("/browse?type=briefs");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Login"));

        verifyNoInteractions(controller);
    }

    @Test
    public void readRequiresLogin() throws Exception {
        HttpResponse<String> response = get(
                "/read?type=briefs&id=1"
        );

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Login"));

        verifyNoInteractions(controller);
    }

    @Test
    public void auditRequiresLogin() throws Exception {
        HttpResponse<String> response = get("/audit");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Login"));

        verifyNoInteractions(controller);
    }

    // ---------------- LOGOUT ----------------

    @Test
    public void logoutRemovesSession() throws Exception {
        String cookie = login();

        HttpResponse<String> logoutResponse = getWithCookie(
                "/logout",
                cookie
        );

        assertEquals(302, logoutResponse.statusCode());
        assertEquals(
                "/",
                logoutResponse.headers().firstValue("Location").orElse("")
        );

        String expiredCookie = logoutResponse.headers()
                .firstValue("Set-Cookie")
                .orElse("");

        assertTrue(expiredCookie.contains("Max-Age=0"));

        HttpResponse<String> rootResponse = getWithCookie("/", cookie);

        assertEquals(200, rootResponse.statusCode());
        assertTrue(rootResponse.body().contains("Login"));

        verify(controller, never()).showMainMenu("alice");
    }

    // ---------------- ERROR HANDLING ----------------

    @Test
    public void unknownPathDisplaysErrorPage() throws Exception {
        HttpResponse<String> response = get("/unknown");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Error"));
        assertTrue(response.body().contains("Unknown page."));

        verifyNoInteractions(controller);
    }
}
