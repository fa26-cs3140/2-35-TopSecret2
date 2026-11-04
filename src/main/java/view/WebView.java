package view;

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

        if (title == null) {
            title = "TopSecret";
        }

        if (text == null) {
            text = "";
        }

        String normalizedTitle = title.toLowerCase();

        if (normalizedTitle.equals("main menu")) {
            return mainMenuView(text);
        }

        if (normalizedTitle.startsWith("browse:")) {
            String type = getTypeFromTitle(title);
            return browseView(type, text);
        }

        if (normalizedTitle.startsWith("read:")) {
            String type = getTypeFromTitle(title);
            return readView(type, text);
        }

        if (normalizedTitle.contains("audit")) {
            return auditView(text);
        }

        return HTMLUtils.createPage(
                title,
                HTMLUtils.createHeading(title)
                        + HTMLUtils.createTextBlock(text)
                        + HTMLUtils.createBackLink()
        );
    }

    /**
     * Generates the main menu with clickable navigation links.
     *
     * The controller's menu text determines which options
     * are displayed. In particular, the audit link is shown
     * only if the controller includes it.
     */
    public String mainMenuView(String controllerMenu) {

        String menu = controllerMenu.toLowerCase();

        StringBuilder body = new StringBuilder();

        body.append(HTMLUtils.createHeading("Main Menu"));

        String[][] possibleLinks = {
                {"/browse?type=briefs", "Browse Briefs"},
                {"/browse?type=agents", "Browse Agents"},
                {"/browse?type=facilities", "Browse Facilities"},
                {"/browse?type=missions", "Browse Missions"},
                {"/browse?type=messages", "Browse Messages"}
        };

        StringBuilder links = new StringBuilder("<ul>");

        boolean foundOption = false;

        for (String[] link : possibleLinks) {
            String label = link[1].toLowerCase();

            if (menu.contains(label)) {
                links.append("<li>")
                        .append(HTMLUtils.createLink(link[0], link[1]))
                        .append("</li>");

                foundOption = true;
            }
        }

        // Fallback if the controller uses different menu wording.
        if (!foundOption) {
            for (String[] link : possibleLinks) {
                links.append("<li>")
                        .append(HTMLUtils.createLink(link[0], link[1]))
                        .append("</li>");
            }
        }

        // Only display the audit link when the controller
        // includes an audit option in its menu.
        if (menu.contains("audit")) {
            links.append("<li>")
                    .append(HTMLUtils.createLink("/audit", "Review Audit Log"))
                    .append("</li>");
        }

        links.append("<li>")
                .append(HTMLUtils.createLink("/logout", "Logout"))
                .append("</li>");

        links.append("</ul>");

        body.append(links);

        return HTMLUtils.createPage("Main Menu", body.toString());
    }

    /**
     * Generates a browse page.
     *
     * Displays the records returned by the controller and
     * provides a form for reading a record by ID.
     */
    public String browseView(String type, String records) {

        String title = "Browse " + type;

        StringBuilder body = new StringBuilder();

        body.append(HTMLUtils.createHeading(title));

        body.append(HTMLUtils.createTextBlock(records));

        body.append(HTMLUtils.createHeading("Read a Record"));

        body.append(HTMLUtils.createReadForm(type));

        body.append("<p>")
                .append(HTMLUtils.createBackLink())
                .append("</p>");

        return HTMLUtils.createPage(title, body.toString());
    }

    /**
     * Generates a page displaying one selected record.
     */
    public String readView(String type, String record) {

        String title = "Read " + type;

        StringBuilder body = new StringBuilder();

        body.append(HTMLUtils.createHeading(title));

        body.append(HTMLUtils.createTextBlock(record));

        body.append("<p>")
                .append(HTMLUtils.createLink(
                        "/browse?type=" + type,
                        "Back to Browse " + type
                ))
                .append("</p>");

        body.append("<p>")
                .append(HTMLUtils.createBackLink())
                .append("</p>");

        return HTMLUtils.createPage(title, body.toString());
    }

    /**
     * Generates the audit log page.
     */
    public String auditView(String logs) {

        StringBuilder body = new StringBuilder();

        body.append(HTMLUtils.createHeading("Audit Log"));

        body.append(HTMLUtils.createTextBlock(logs));

        body.append("<p>")
                .append(HTMLUtils.createBackLink())
                .append("</p>");

        return HTMLUtils.createPage("Audit Log", body.toString());
    }

    /**
     * Extracts the record type from a page title.
     *
     * Example: "Browse: missions" -> "missions"
     */
    private String getTypeFromTitle(String title) {
        int separator = title.indexOf(':');

        if (separator == -1 || separator == title.length() - 1) {
            return "";
        }

        return title.substring(separator + 1).trim().toLowerCase();
    }
}