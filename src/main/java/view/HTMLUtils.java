package view;

import java.util.List;

public class HTMLUtils {

    //Utility class
    private HTMLUtils() {
        // Utility class; prevent instantiation.
    }

    /**
     * Generates a complete HTML document.
     *
     * @param title page title
     * @param body HTML content to place inside the body
     * @return complete HTML document
     */
    public static String createPage(String title, String body) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>%s</title>
                </head>
                <body>
                    %s
                </body>
                </html>
                """.formatted(title, body);
    }

    /**
     * Escapes special HTML characters to prevent HTML injection.
     */
    public static String escapeHTML(String text) {
        return "";
    }


    /**
     * Generates a heading.
     */
    public static String createHeading(String heading) {
        return "";
    }

    /**
     * Displays plain text while preserving its original formatting.
     */
    public static String createTextBlock(String text) {
        return "";
    }

    /**
     * Generates a hyperlink.
     */
    public static String createLink(String url, String label) {
        return "";
    }

    /**
     * Generates an unordered list of hyperlinks.
     */
    public static String createLinkList(String[][] links) {
        return "";
    }



    /**
     * Generates a form for submitting an ID.
     */
    public static String createIDForm(String action, String label) {
        return "";
    }
}
