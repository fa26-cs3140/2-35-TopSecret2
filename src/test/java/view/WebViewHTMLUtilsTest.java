package view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WebViewHTMLUtilsTest {

    @Test
    public void generateMainMenuHTMLIntegrationTest() {
        WebView webView = new WebView();

        String actual = webView.generateHTML(
                "Main menu",
                "Browse Briefs\nBrowse Agents\nBrowse Facilities\nBrowse Missions\nReview Audit Log"
        );

        assertTrue(actual.contains("<!DOCTYPE html>"));
        assertTrue(actual.contains("<title>Main Menu</title>"));
        assertTrue(actual.contains("<h1>Main Menu</h1>"));

        assertTrue(actual.contains("/browse?type=briefs"));
        assertTrue(actual.contains("/browse?type=agents"));
        assertTrue(actual.contains("/browse?type=facilities"));
        assertTrue(actual.contains("/browse?type=missions"));
        assertTrue(actual.contains("/audit"));
        assertTrue(actual.contains("/logout"));
    }

    @Test
    public void generateBrowsePageHTMLIntegrationTest() {
        WebView webView = new WebView();

        String actual = webView.generateHTML(
                "Browse: missions",
                "Mission ID: 1\nTitle: Operation Eclipse"
        );

        assertTrue(actual.contains("<!DOCTYPE html>"));
        assertTrue(actual.contains("<title>Browse missions</title>"));
        assertTrue(actual.contains("<h1>Browse missions</h1>"));

        assertTrue(actual.contains("Mission ID: 1"));
        assertTrue(actual.contains("Operation Eclipse"));

        assertTrue(actual.contains("<form"));
        assertTrue(actual.contains("action=\"/read\""));
        assertTrue(actual.contains("name=\"type\""));
        assertTrue(actual.contains("value=\"missions\""));

        assertTrue(actual.contains("Back to Main Menu"));
        assertTrue(actual.contains("href=\"/\""));
    }
}