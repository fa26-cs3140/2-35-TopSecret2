package view;

import java.util.List;

public class HTMLUtils {

    /**
     * Generates a complete HTML document.
     */
    public static String page(String title, String content) {
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
                """.formatted(title, content);
    }

    /**
     * Escapes special HTML characters.
     */
    public static String escape(String text) {
        return "";
    }


    /**
     * Generates a heading.
     */
    public static String heading(String text, int level) {
        return "<h" + level + ">" + text
                + "</h" + level + ">";
    }

    /**
     * Generates a hyperlink.
     */
    public static String link(String text, String href) {
        return "<a href=\"" + href + "\">"
                + text + "</a>";
    }

    /**
     * Generates an unordered list of hyperlinks.
     */
    public static String linkList(String... links) {
        return "";
    }


    /**
     * Generates an unordered list of formatted records.
     */
    public static String recordList(List<String> records) {
        return "";
    }

    /**
     * Generates a GET form for retrieving a record by ID.
     */
    public static String idForm(
            String action,
            String inputName,
            String label) {
        return "";
    }

    /**
     * Generates the login form.
     */
    public static String loginForm() {
        return "";
    }

    /**
     * Generates a link back to the main menu.
     */
    public static String mainMenuLink() {
        return "";
    }





}
