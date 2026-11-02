package controller;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Week-1 stub of the web server. The fields and constructor are
// real; start() and stop() are empty until week 2 implements
// routing and sessions (see WebServerUML). The throws clause
// on start() is already there so week 2 does not change the
// signature on callers.
public class WebServer {
    private final ControllerInterface controller;
    private final Map<String, String> sessions = new ConcurrentHashMap<>();
    private final int port;
    private HttpServer server;

    public WebServer(ControllerInterface controller, int port) {
        this.controller = controller;
        this.port = port;
    }

    // Week 2: create the HttpServer on port, register one
    // context per route, and start listening.
    public void start() throws IOException {
    }

    // Week 2: shut the server down. Safe to call before start().
    public void stop() {
    }
}
