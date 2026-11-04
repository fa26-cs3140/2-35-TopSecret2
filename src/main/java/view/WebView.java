package view;

import model.*;

import java.util.List;

public class WebView {


    public WebView() {
    }


    /**
     * Generates an HTML page based on the page title and
     * controller output.
     *
     * The server supplies titles such as:
     * Main menu
     * Browse: missions
     * Read: missions
     * Audit log
     */
    public String generateHTML(String title, String text) {
        return "";
    }

    /**
     * Generates the main menu with clickable navigation links.
     *
     * The controller's menu text determines which options
     * are displayed. In particular, the audit link is shown
     * only if the controller includes it.
     */
    public String mainMenuView(String controllerMenu) {
        return "";
    }

    /**
     * Generates a browse page.
     *
     * Displays the records returned by the controller and
     * provides a form for reading a record by ID.
     */
    public String browseView(String type, String records) {
        return "";
    }

    /**
     * Generates a page displaying one selected record.
     */
    public String readView(String type, String record) {
        return "";
    }

    /**
     * Generates the audit log page.
     */
    public String auditView(String logs) {
        return "";
    }

    /**
     * Extracts the record type from a page title.
     *
     * Example: "Browse: missions" -> "missions"
     */
    private String getTypeFromTitle(String title) {
        return "";
    }


}
