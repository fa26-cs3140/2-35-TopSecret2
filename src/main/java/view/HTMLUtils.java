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
        String navigation = """
            <nav class="site-nav">
                <a href="/">Main Menu</a>
                <a href="/browse?type=briefs">Briefs</a>
                <a href="/browse?type=agents">Agents</a>
                <a href="/browse?type=facilities">Facilities</a>
                <a href="/browse?type=missions">Missions</a>
                <a href="/browse?type=countries">Countries</a>
                <a href="/messages">Messages</a>
                <a href="/audit">Audit Log</a>
                <a href="/logout">Logout</a>
            </nav>
            """;

        String css = """
            body {
                font-family: Arial, sans-serif;
                margin: 0;
                padding: 20px;
                background-color: #f4f6f8;
                color: #222;
            }

            h1, h2 {
                color: #1f3a5f;
            }

            .site-nav {
                background-color: #1f3a5f;
                padding: 12px;
                margin-bottom: 24px;
            }

            .site-nav a {
                color: white;
                text-decoration: none;
                margin-right: 16px;
            }

            .site-nav a:hover {
                text-decoration: underline;
            }

            a {
                color: #245ea8;
            }

            form {
                margin: 16px 0;
            }

            input, select, button {
                padding: 8px;
                margin: 4px 0;
            }

            pre {
                background-color: white;
                padding: 16px;
                border-radius: 6px;
                white-space: pre-wrap;
            }
            """;

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
                <style>
            %s
                </style>
            </head>
            <body>
                %s
                %s
            </body>
            </html>
            """.formatted(escapeHTML(title), css, navigation, body == null ? "" : body);
    }

    /**
     * @param heading the text of the heading
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
     * @param url href link of the url
     * @param label label of the link
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
        return """
            <form method="GET" action="/read">
                <input type="hidden" name="type" value="%s">

                <label for="id">Enter %s ID:</label>
                <input type="number" id="id" name="id" required>

                <button type="submit">Read</button>
            </form>
            """.formatted(escapeHTML(type), escapeHTML(type));
    }

    /**
     * Generates a link back to the main menu.
     */
    public static String createBackLink() {
        return createLink("/", "Back to Main Menu");
    }
}