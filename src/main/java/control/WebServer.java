package control;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import view.WebView;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Week-2 web server: the browser's adapter to the controller.
// It listens, routes, identifies the requester from the session
// cookie, and translates controller text back into pages. It
// holds no record logic, menu logic, admin gating, or audit
// logic: the controller already decided all of that, and the
// server only shows what the controller returned. The web is
// create, edit, and message routes in HW7, admin-gated by
// the controller. Sessions live in the shared SessionManager:
// a random UUID minted at login, never derived from the
// username, one login = one token. The cookie has
// Max-Age=1800, re-issued on every authed response, so a
// session stays valid while in use and times out after 30
// minutes inactive, and no Secure flag, because the site
// is plain http on localhost and a Secure cookie would
// never be sent.
// One private method per route; start() only creates the
// server and registers the routes.
public class WebServer {
    private final ControllerInterface controller;
    private final SessionManager sessionManager;
    private final int port;
    private HttpServer server;
    private final WebView view = new WebView();

    // The login form is the server's own gate page, plain
    // HTML, no CSS, no JavaScript.
    private static final String FORM_FRAGMENT = "<form method=\"post\" action=\"/login\">"
            + "Username: <input name=\"username\"><br>"
            + "Password: <input type=\"password\" name=\"password\"><br>"
            + "<input type=\"submit\" value=\"Login\"></form>";
    private static final String LOGIN_PAGE = "<html><head><title>Login</title></head><body>"
            + FORM_FRAGMENT + "</body></html>";

    public WebServer(ControllerInterface controller, SessionManager sessionManager, int port) {
        this.controller = controller;
        this.sessionManager = sessionManager;
        this.port = port;
    }

    // The port the server actually listens on. After start()
    // this is the real port (so port 0 picks a free one);
    // before start() it is the configured port.
    public int getPort() {
        return server != null ? server.getAddress().getPort() : port;
    }

    public void start() throws IOException {
        if (server != null) {
            return;
        }
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(null);
        server.createContext("/", this::handleRoot);
        server.createContext("/login", this::handleLogin);
        server.createContext("/browse", this::handleBrowse);
        server.createContext("/read", this::handleRead);
        server.createContext("/audit", this::handleAudit);
        server.createContext("/logout", this::handleLogout);
        server.createContext("/create", this::handleCreate);
        server.createContext("/edit", this::handleEdit);
        server.createContext("/messages", this::handleMessages);
        server.start();
    }

    // Shut the server down. Safe to call before start() and
    // safe to call twice.
    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    // Escape plain controller text for HTML pages.
    private String escape(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    // Wrap controller text in a page. Uses WebView when
    // it returns content; until then, plain wrapping, so
    // pages work even while generateHTML is still a stub.
    private String wrapText(String title, String text) {
        String html = view.generateHTML(title, text);
        if (html == null || html.isEmpty()) {
            html = "<html><head><title>" + escape(title)
                    + "</title></head><body><pre>" + escape(text)
                    + "</pre></body></html>";
        }
        return html;
    }

    // Parse a query string or form body into parameters.
    private Map<String, String> parseForm(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null) {
            for (String pair : query.split("&")) {
                int eq = pair.indexOf('=');
                if (eq > 0) {
                    params.put(URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8),
                            URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8));
                }
            }
        }
        return params;
    }

    // The session token from the Cookie header, or null.
    private String sessionToken(HttpExchange exchange) {
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookies != null) {
            for (String part : cookies.split(";")) {
                part = part.trim();
                if (part.startsWith("session=")) {
                    return part.substring("session=".length());
                }
            }
        }
        return null;
    }

    // Who this request is from, or null when logged out.
    // The guard runs before any controller call, so an
    // unauthenticated request never touches the controller.
    private String sessionUser(HttpExchange exchange) {
        String token = sessionToken(exchange);
        return token == null ? null : sessionManager.validate(token);
    }

    // Send a 200 page. A failed send means the client went
    // away, so there is nothing left to do.
    private void sendPage(HttpExchange exchange, String page) {
        try {
            byte[] bytes = page.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        } catch (IOException ignored) {
        }
    }

    // Root: the main menu when logged in, the login form
    // otherwise. This context also catches unknown paths,
    // which get an error page, never a crash.
    private void handleRoot(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestURI().getPath().equals("/")) {
            sendPage(exchange, wrapText("Error", "Unknown page."));
            return;
        }
        String username = sessionUser(exchange);
        if (username == null) {
            sendPage(exchange, LOGIN_PAGE);
        } else {
            sendPage(exchange, wrapText("Main menu", controller.showMainMenu(username)));
        }
    }

    // Login: GET shows the form, POST checks credentials.
    // Success sets the session cookie and redirects home;
    // failure shows the error with the form again, and sets
    // no cookie.
    private void handleLogin(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendPage(exchange, LOGIN_PAGE);
            return;
        }
        String lengthHeader = exchange.getRequestHeaders().getFirst("Content-Length");
        if (lengthHeader != null) {
            try {
                if (Integer.parseInt(lengthHeader) > 8192) {
                    sendPage(exchange, wrapText("Error", "Request too large."));
                    return;
                }
            } catch (NumberFormatException ignored) {
                // No usable length: read the body as before.
            }
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = parseForm(body);
        String username = params.get("username");
        String password = params.get("password");
        if (username != null && password != null && controller.verifyLogin(username, password)) {
            String token = sessionManager.createSession(username);
            exchange.getResponseHeaders().set("Location", "/");
            exchange.getResponseHeaders().set("Set-Cookie", "session=" + token + "; Path=/; HttpOnly; Max-Age=1800");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        } else {
            String page = "<html><head><title>Login</title></head><body>"
                    + "<p>Invalid username or password.</p>"
                    + FORM_FRAGMENT + "</body></html>";
            sendPage(exchange, page);
        }
    }

    // Browse: the record list for one type.
    private void handleBrowse(HttpExchange exchange) throws IOException {
        String username = sessionUser(exchange);
        if (username == null) {
            sendPage(exchange, LOGIN_PAGE);
            return;
        }
        String type = parseForm(exchange.getRequestURI().getRawQuery()).getOrDefault("type", "");
        sendPage(exchange, wrapText("Browse: " + type, controller.listEntries(username, type)));
    }

    // Read: one full record.
    private void handleRead(HttpExchange exchange) throws IOException {
        String username = sessionUser(exchange);
        if (username == null) {
            sendPage(exchange, LOGIN_PAGE);
            return;
        }
        Map<String, String> params = parseForm(exchange.getRequestURI().getRawQuery());
        String type = params.getOrDefault("type", "");
        int id;
        try {
            id = Integer.parseInt(params.getOrDefault("id", ""));
        } catch (NumberFormatException e) {
            sendPage(exchange, wrapText("Read", "Invalid option"));
            return;
        }
        sendPage(exchange, wrapText("Read: " + type, controller.readEntry(username, type, id)));
    }

    // Audit: the audit log, admin-gated by the controller.
    private void handleAudit(HttpExchange exchange) throws IOException {
        String username = sessionUser(exchange);
        if (username == null) {
            sendPage(exchange, LOGIN_PAGE);
            return;
        }
        sendPage(exchange, wrapText("Audit log", controller.reviewAuditLog(username)));
    }

    // Logout: drop the session and expire the cookie, then
    // back to the login form.
    private void handleLogout(HttpExchange exchange) throws IOException {
        String token = sessionToken(exchange);
        if (token != null) {
            sessionManager.revokeSession(token);
        }
        exchange.getResponseHeaders().set("Location", "/");
        exchange.getResponseHeaders().set("Set-Cookie", "session=; Max-Age=0; Path=/");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    // Create: empty form for a new record, admin only.
    // Week 1 stub: the FormField list is shown as plain
    // text, not a form. A form needs a form helper from
    // the view, and none exists yet.
    private void handleCreate(HttpExchange exchange) throws IOException {
        String username = sessionUser(exchange);
        if (username == null) {
            sendPage(exchange, LOGIN_PAGE);
            return;
        }
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String type = parseForm(exchange.getRequestURI().getRawQuery()).getOrDefault("type", "");
            sendPage(exchange, wrapText("Create: " + type, controller.getFormFields(username, type, 0).toString()));
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = parseForm(body);
        String type = params.getOrDefault("type", "");
        String result;
        try {
            result = controller.addEntry(username, type, List.of());
        } catch (RuntimeException e) {
            result = e.getMessage();
        }
        sendPage(exchange, wrapText("Create: " + type, result));
    }

    // Edit: prefilled form, admin only. Week 1 stub, as /create.
    private void handleEdit(HttpExchange exchange) throws IOException {
        String username = sessionUser(exchange);
        if (username == null) {
            sendPage(exchange, LOGIN_PAGE);
            return;
        }
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> params = parseForm(exchange.getRequestURI().getRawQuery());
            String type = params.getOrDefault("type", "");
            int id;
            try {
                id = Integer.parseInt(params.getOrDefault("id", ""));
            } catch (NumberFormatException e) {
                sendPage(exchange, wrapText("Edit", "Invalid option"));
                return;
            }
            sendPage(exchange, wrapText("Edit: " + type, controller.getFormFields(username, type, id).toString()));
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = parseForm(body);
        String type = params.getOrDefault("type", "");
        String result;
        try {
            result = controller.editEntry(username, type, Integer.parseInt(params.getOrDefault("id", "0")), List.of());
        } catch (RuntimeException e) {
            result = e.getMessage();
        }
        sendPage(exchange, wrapText("Edit: " + type, result));
    }

    // Messages: list, any logged-in user. Week 1 stub.
    private void handleMessages(HttpExchange exchange) throws IOException {
        String username = sessionUser(exchange);
        if (username == null) {
            sendPage(exchange, LOGIN_PAGE);
            return;
        }
        sendPage(exchange, wrapText("Messages", controller.listMessages(username)));
    }
}
