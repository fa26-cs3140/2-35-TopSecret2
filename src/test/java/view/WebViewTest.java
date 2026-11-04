package view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WebViewTest {

    @Test
    public void generateHTMLMainMenuTest1() {
        WebView view = new WebView();

        String actual = view.generateHTML("Main menu", "Browse Agents");

        assertTrue(actual.contains("<title>Main Menu</title>"));
        assertTrue(actual.contains("<h1>Main Menu</h1>"));
        assertTrue(actual.contains("/browse?type=agents"));
        assertTrue(actual.contains("Browse Agents"));
    }

    @Test
    public void generateHTMLBrowseTest1() {
        WebView view = new WebView();

        String actual = view.generateHTML("Browse: missions", "Mission ID: 1");

        assertTrue(actual.contains("<title>Browse missions</title>"));
        assertTrue(actual.contains("<h1>Browse missions</h1>"));
        assertTrue(actual.contains("Mission ID: 1"));
        assertTrue(actual.contains("Enter missions ID:"));
    }

    @Test
    public void generateHTMLReadTest1() {
        WebView view = new WebView();

        String actual = view.generateHTML("Read: agents", "Agent ID: 1");

        assertTrue(actual.contains("<title>Read agents</title>"));
        assertTrue(actual.contains("<h1>Read agents</h1>"));
        assertTrue(actual.contains("Agent ID: 1"));
        assertTrue(actual.contains("/browse?type=agents"));
    }

    @Test
    public void generateHTMLAuditTest1() {
        WebView view = new WebView();

        String actual = view.generateHTML("Audit log", "User logged in");

        assertTrue(actual.contains("<title>Audit Log</title>"));
        assertTrue(actual.contains("<h1>Audit Log</h1>"));
        assertTrue(actual.contains("User logged in"));
    }

    @Test
    public void generateHTMLDefaultTest1() {
        WebView view = new WebView();

        String actual = view.generateHTML("Custom Page", "Custom content");

        assertTrue(actual.contains("<title>Custom Page</title>"));
        assertTrue(actual.contains("<h1>Custom Page</h1>"));
        assertTrue(actual.contains("Custom content"));
        assertTrue(actual.contains("Back to Main Menu"));
    }

    @Test
    public void generateHTMLNullTest1() {
        WebView view = new WebView();

        String actual = view.generateHTML(null, null);

        assertTrue(actual.contains("<title>TopSecret</title>"));
        assertTrue(actual.contains("<h1>TopSecret</h1>"));
    }

    @Test
    public void mainMenuViewTest1() {
        WebView view = new WebView();

        String actual = view.mainMenuView(
                "Browse Agents\nBrowse Missions\nReview Audit Log"
        );

        assertTrue(actual.contains("Browse Agents"));
        assertTrue(actual.contains("Browse Missions"));
        assertTrue(actual.contains("Review Audit Log"));
        assertTrue(actual.contains("/audit"));
        assertTrue(actual.contains("/logout"));
    }

    @Test
    public void mainMenuViewTest2() {
        WebView view = new WebView();

        String actual = view.mainMenuView("Browse Agents");

        assertTrue(actual.contains("Browse Agents"));
        assertFalse(actual.contains("Review Audit Log"));
        assertTrue(actual.contains("/logout"));
    }

    @Test
    public void mainMenuViewTest3() {
        WebView view = new WebView();

        String actual = view.mainMenuView("Some unknown menu option");

        // Tests fallback behavior.
        assertTrue(actual.contains("Browse Briefs"));
        assertTrue(actual.contains("Browse Agents"));
        assertTrue(actual.contains("Browse Facilities"));
        assertTrue(actual.contains("Browse Missions"));
        assertTrue(actual.contains("Browse Messages"));
    }

    @Test
    public void browseViewTest1() {
        WebView view = new WebView();

        String actual = view.browseView("agents", "Agent ID: 1");

        assertTrue(actual.contains("<title>Browse agents</title>"));
        assertTrue(actual.contains("<h1>Browse agents</h1>"));
        assertTrue(actual.contains("Agent ID: 1"));
        assertTrue(actual.contains("Read a Record"));
        assertTrue(actual.contains("Enter agents ID:"));
        assertTrue(actual.contains("Back to Main Menu"));
    }

    @Test
    public void readViewTest1() {
        WebView view = new WebView();

        String actual = view.readView("agents", "Agent ID: 1");

        assertTrue(actual.contains("<title>Read agents</title>"));
        assertTrue(actual.contains("<h1>Read agents</h1>"));
        assertTrue(actual.contains("Agent ID: 1"));
        assertTrue(actual.contains("Back to Browse agents"));
        assertTrue(actual.contains("Back to Main Menu"));
    }

    @Test
    public void auditViewTest1() {
        WebView view = new WebView();

        String actual = view.auditView("User logged in");

        assertTrue(actual.contains("<title>Audit Log</title>"));
        assertTrue(actual.contains("<h1>Audit Log</h1>"));
        assertTrue(actual.contains("User logged in"));
        assertTrue(actual.contains("Back to Main Menu"));
    }
}