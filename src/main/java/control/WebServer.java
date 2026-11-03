package control;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import view.WebView;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

// Week-2 web server: the browser's adapter to the controller.
// It listens, routes, identifies the requester from the session
// cookie, and translates controller text back into pages. It
// holds no record logic, menu logic, admin gating, or audit
// logic: the controller already decided all of that, and the
// server only shows what the controller returned. The web is
// read-only: browse, read, and audit routes only, no creating
// or editing. Sessions are one token map: a random UUID minted
// at login, never derived from the username, one login = one
// token. The cookie has no Expires and no Max-Age, which is
// what makes it a session cookie that dies when the browser
// closes, and no Secure flag, because the site is plain http
// on localhost and a Secure cookie would never be sent.
// Helpers live as local lambdas inside start(), not private
// methods, so the whole request path reads in one place.
public class WebServer {
    private final ControllerInterface controller;
    private final Map<String, String> sessions = new ConcurrentHashMap<>();
    private final int port;
    private HttpServer server;
    private final WebView view = new WebView();

    public WebServer(ControllerInterface controller, int port) {
        this.controller = controller;
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

        // Escape plain controller text for HTML pages.
        Function<String, String> escape = text -> text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        // Wrap controller text in a page. Uses C's WebView when
        // it returns content; until then, plain wrapping, so
        // pages work even while generateHTML is still a stub.
        BiFunction<String, String, String> wrapText = (title, text) -> {
            String html = view.generateHTML(title, text);
            if (html == null || html.isEmpty()) {
                html = "<html><head><title>" + escape.apply(title)
                        + "</title></head><body><pre>" + escape.apply(text)
                        + "</pre></body></html>";
            }
            return html;
        };

        // Parse a query string or form body into parameters.
        Function<String, Map<String, String>> parseForm = query -> {
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
        };

        // The session token from the Cookie header, or null.
        Function<HttpExchange, String> sessionToken = exchange -> {
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
        };

        // Who this request is from, or null when logged out.
        // The guard runs before any controller call, so an
        // unauthenticated request never touches the controller.
        Function<HttpExchange, String> sessionUser = exchange -> {
            String token = sessionToken.apply(exchange);
            return token == null ? null : sessions.get(token);
        };

        // Send a 200 page. A failed send means the client went
        // away, so there is nothing left to do.
        BiConsumer<HttpExchange, String> sendPage = (exchange, page) -> {
            try {
                byte[] bytes = page.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.getResponseBody().close();
            } catch (IOException ignored) {
            }
        };

        // The login form is the server's own gate page, plain
        // HTML, no CSS, no JavaScript.
        String formFragment = "<form method=\"post\" action=\"/login\">"
                + "Username: <input name=\"username\"><br>"
                + "Password: <input type=\"password\" name=\"password\"><br>"
                + "<input type=\"submit\" value=\"Login\"></form>";
        String loginPage = "<html><head><title>Login</title></head><body>"
                + formFragment + "</body></html>";

        // Root: the main menu when logged in, the login form
        // otherwise. This context also catches unknown paths,
        // which get an error page, never a crash.
        server.createContext("/", exchange -> {
            if (!exchange.getRequestURI().getPath().equals("/")) {
                sendPage.accept(exchange, wrapText.apply("Error", "Unknown page."));
                return;
            }
            String username = sessionUser.apply(exchange);
            if (username == null) {
                sendPage.accept(exchange, loginPage);
            } else {
                sendPage.accept(exchange, wrapText.apply("Main menu", controller.showMainMenu(username)));
            }
        });

        // Login: GET shows the form, POST checks credentials.
        // Success sets the session cookie and redirects home;
        // failure shows the error with the form again, and sets
        // no cookie.
        server.createContext("/login", exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendPage.accept(exchange, loginPage);
                return;
            }
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> params = parseForm.apply(body);
            String username = params.get("username");
            String password = params.get("password");
            if (username != null && password != null && controller.verifyLogin(username, password)) {
                String token = UUID.randomUUID().toString();
                sessions.put(token, username);
                exchange.getResponseHeaders().set("Location", "/");
                exchange.getResponseHeaders().set("Set-Cookie", "session=" + token + "; Path=/; HttpOnly");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
            } else {
                String page = "<html><head><title>Login</title></head><body>"
                        + "<p>Invalid username or password.</p>"
                        + formFragment + "</body></html>";
                sendPage.accept(exchange, page);
            }
        });

        // Browse: the record list for one type.
        server.createContext("/browse", exchange -> {
            String username = sessionUser.apply(exchange);
            if (username == null) {
                sendPage.accept(exchange, loginPage);
                return;
            }
            String type = parseForm.apply(exchange.getRequestURI().getRawQuery()).getOrDefault("type", "");
            sendPage.accept(exchange, wrapText.apply("Browse", controller.listEntries(username, type)));
        });

        // Read: one full record.
        server.createContext("/read", exchange -> {
            String username = sessionUser.apply(exchange);
            if (username == null) {
                sendPage.accept(exchange, loginPage);
                return;
            }
            Map<String, String> params = parseForm.apply(exchange.getRequestURI().getRawQuery());
            String type = params.getOrDefault("type", "");
            int id;
            try {
                id = Integer.parseInt(params.getOrDefault("id", ""));
            } catch (NumberFormatException e) {
                sendPage.accept(exchange, wrapText.apply("Read", "Invalid option"));
                return;
            }
            sendPage.accept(exchange, wrapText.apply("Read", controller.readEntry(username, type, id)));
        });

        // Audit: the audit log, admin-gated by the controller.
        server.createContext("/audit", exchange -> {
            String username = sessionUser.apply(exchange);
            if (username == null) {
                sendPage.accept(exchange, loginPage);
                return;
            }
            sendPage.accept(exchange, wrapText.apply("Audit log", controller.reviewAuditLog(username)));
        });

        // Logout: drop the session and expire the cookie, then
        // back to the login form.
        server.createContext("/logout", exchange -> {
            String token = sessionToken.apply(exchange);
            if (token != null) {
                sessions.remove(token);
            }
            exchange.getResponseHeaders().set("Location", "/");
            exchange.getResponseHeaders().set("Set-Cookie", "session=; Max-Age=0; Path=/");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        });

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
}
