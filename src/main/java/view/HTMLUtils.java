package view;

public class HTMLUtils {
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

    public static String heading(String text, int level) {
        return "<h" + level + ">" + text
                + "</h" + level + ">";
    }

    public static String link(String text, String href) {
        return "<a href=\"" + href + "\">"
                + text + "</a>";
    }
}
