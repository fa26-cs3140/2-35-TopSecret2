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
                {"/browse?type=countries", "Browse Countries"},
                {"/messages", "Send or view messages"}
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

        body.append(createBrowseLinks(type, records));

        body.append(HTMLUtils.createHeading("Read a Record"));

        body.append(HTMLUtils.createReadForm(type));

        body.append("<p>")
                .append(HTMLUtils.createBackLink())
                .append("</p>");

        body.append("<p>")
                .append(HTMLUtils.createLink("/create?type=" + type, "Add " + type))
                .append("</p>");

        return HTMLUtils.createPage(title, body.toString());
    }

    private String createBrowseLinks(String type, String records) {

        if (records == null || records.isBlank()) {
            return HTMLUtils.createTextBlock("No records found.");
        }

        StringBuilder html = new StringBuilder("<ul>");

        for (String line : records.split("\\R")) {

            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                continue;
            }

            String[] parts = trimmed.split("\\s*:\\s*", 2);

            if (parts.length == 2 && parts[0].matches("\\d+")) {

                int id = Integer.parseInt(parts[0]);

                String url = "/read?type=" + type + "&id=" + id;

                html.append("<li>")
                        .append(HTMLUtils.createLink(url, trimmed))
                        .append("</li>");

            } else {
                html.append("<li>")
                        .append(HTMLUtils.escapeHTML(trimmed))
                        .append("</li>");
            }
        }

        html.append("</ul>");

        return html.toString();
    }

    /**
     * Generates a page displaying one selected record.
     */
    public String readView(String type, String record) {
        return readView(type, 0, record);
    }

    public String readView(String type, int id, String record) {
        String title = "Read " + type;

        StringBuilder body = new StringBuilder();

        body.append(HTMLUtils.createHeading(title));
        body.append(renderRecord(type, record));

        if (id > 0) {
            body.append("<p>")
                    .append(HTMLUtils.createLink(
                            "/edit?type=" + type + "&id=" + id,
                            "Edit this record"))
                    .append("</p>");
        }

        body.append("<p>")
                .append(HTMLUtils.createLink(
                        "/browse?type=" + type,
                        "Back to Browse " + type))
                .append("</p>");

        body.append("<p>")
                .append(HTMLUtils.createBackLink())
                .append("</p>");

        return HTMLUtils.createPage(title, body.toString());
    }

    private String renderRecord(String type, String record) {
        if (record == null || record.isBlank()) {
            return HTMLUtils.createTextBlock("");
        }

        StringBuilder html = new StringBuilder();
        String relatedType = null;

        for (String line : record.split("\\R")) {
            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                html.append("<br>");
                continue;
            }

            // Detect related-record sections such as Missions: or Agents:
            if (trimmed.endsWith(":")) {
                String section = trimmed.substring(0, trimmed.length() - 1);
                relatedType = getRelatedType(type, section);

                html.append("<h2>")
                        .append(HTMLUtils.escapeHTML(section))
                        .append("</h2>");

                continue;
            }

            // Detect inline fields such as Facility: 3 : CIA Headquarters
            int colon = trimmed.indexOf(':');

            if (colon > 0) {
                String field = trimmed.substring(0, colon).trim();
                String value = trimmed.substring(colon + 1).trim();

                String inlineType = getRelatedType(type, field);

                html.append("<p><strong>")
                        .append(HTMLUtils.escapeHTML(field))
                        .append(":</strong> ");

                if (inlineType != null) {
                    html.append(HTMLUtils.createRecordLink(
                            inlineType, value));
                } else {
                    html.append(HTMLUtils.escapeHTML(value));
                }

                html.append("</p>");
                continue;
            }

            // Lines inside related-record sections become clickable.
            if (relatedType != null) {
                html.append(HTMLUtils.createRecordLink(
                                relatedType, trimmed))
                        .append("<br>");
            } else {
                html.append(HTMLUtils.escapeHTML(trimmed))
                        .append("<br>");
            }
        }

        return html.toString();
    }

    private String getRelatedType(String currentType, String relationship) {
        switch (currentType) {
            case "briefs":
                if (relationship.equalsIgnoreCase("Related Mission")) {
                    return "missions";
                }
                break;

            case "agents":
                if (relationship.equalsIgnoreCase("Facility")) {
                    return "facilities";
                }
                if (relationship.equalsIgnoreCase("Missions")) {
                    return "missions";
                }
                break;

            case "facilities":
                if (relationship.equalsIgnoreCase("Country")) {
                    return "countries";
                }
                if (relationship.equalsIgnoreCase("Agents")) {
                    return "agents";
                }
                if (relationship.equalsIgnoreCase("Missions")) {
                    return "missions";
                }
                break;

            case "missions":
                if (relationship.equalsIgnoreCase("Facility")) {
                    return "facilities";
                }
                if (relationship.equalsIgnoreCase("Agents")) {
                    return "agents";
                }
                if (relationship.equalsIgnoreCase("Briefs")) {
                    return "briefs";
                }
                break;

            case "countries":
                if (relationship.equalsIgnoreCase("Facilities")) {
                    return "facilities";
                }
                break;
        }

        return null;
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