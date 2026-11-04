package view;

public class HTMLUtils {

    // Utility class
    private HTMLUtils() {
    }

    /**
     * Escapes special HTML characters.
     */
    public static String escapeHTML(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Generates a complete HTML document.
     */
    public static String createPage(String title, String body) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                </head>
                <body>
                    %s
                </body>
                </html>
                """.formatted(escapeHTML(title), body == null ? "" : body);
    }

    /**
     * Generates a heading.
     */
    public static String createHeading(String heading) {
        return "<h1>" + escapeHTML(heading) + "</h1>";
    }

    /**
     * Displays plain text while preserving line breaks and spacing.
     */
    public static String createTextBlock(String text) {
        return "<pre>" + escapeHTML(text) + "</pre>";
    }

    /**
     * Generates a hyperlink.
     */
    public static String createLink(String url, String label) {
        return "<a href=\"" + escapeHTML(url) + "\">"
                + escapeHTML(label) + "</a>";
    }

    /**
     * Generates an unordered list of hyperlinks.
     *
     * Each entry contains:
     * [0] URL
     * [1] Link label
     */
    public static String createLinkList(String[][] links) {
        StringBuilder html = new StringBuilder("<ul>");

        if (links != null) {
            for (String[] link : links) {
                if (link == null || link.length < 2) {
                    continue;
                }

                html.append("<li>")
                        .append(createLink(link[0], link[1]))
                        .append("</li>");
            }
        }

        html.append("</ul>");

        return html.toString();
    }

    /**
     * Generates a form for submitting a record ID.
     *
     * The action can contain query parameters.
     */
    public static String createIDForm(String action, String label) {
        return """
                <form method="GET" action="%s">
                    <label for="id">%s</label>
                    <input type="number" id="id" name="id" required>
                    <button type="submit">Read</button>
                </form>
                """.formatted(escapeHTML(action), escapeHTML(label));
    }

    /**
     * Generates a form for reading a particular record type.
     */
    public static String createReadForm(String type) {
        return createIDForm(
                "/read?type=" + type,
                "Enter " + type + " ID:"
        );
    }

    /**
     * Generates a link back to the main menu.
     */
    public static String createBackLink() {
        return createLink("/", "Back to Main Menu");
    }
}