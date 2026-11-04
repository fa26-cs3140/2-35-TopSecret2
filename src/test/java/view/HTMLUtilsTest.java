package view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HTMLUtilsTest {

    @Test
    public void escapeHTMLTest1() {
        String actual = HTMLUtils.escapeHTML("<script>alert(\"test\")</script> & 'hello'");

        String expected = "&lt;script&gt;alert(&quot;test&quot;)&lt;/script&gt; &amp; &#39;hello&#39;";

        assertEquals(expected, actual);
    }

    @Test
    public void escapeHTMLTest2() {
        String actual = HTMLUtils.escapeHTML(null);

        assertEquals("", actual);
    }

    @Test
    public void createPageTest1() {
        String actual = HTMLUtils.createPage("Test Page", "<h1>Hello</h1>");

        String expected = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Test Page</title>
                </head>
                <body>
                    <h1>Hello</h1>
                </body>
                </html>
                """;

        assertEquals(expected, actual);
    }

    @Test
    public void createPageTest2() {
        String actual = HTMLUtils.createPage("<Test>", null);

        String expected = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>&lt;Test&gt;</title>
                </head>
                <body>
                   \s
                </body>
                </html>
                """;

        assertEquals(expected, actual);
    }

    @Test
    public void createHeadingTest1() {
        String actual = HTMLUtils.createHeading("Hello World");

        String expected = "<h1>Hello World</h1>";

        assertEquals(expected, actual);
    }

    @Test
    public void createHeadingTest2() {
        String actual = HTMLUtils.createHeading("<script>");

        String expected = "<h1>&lt;script&gt;</h1>";

        assertEquals(expected, actual);
    }

    @Test
    public void createTextBlockTest1() {
        String actual = HTMLUtils.createTextBlock("Line 1\nLine 2");

        String expected = "<pre>Line 1\nLine 2</pre>";

        assertEquals(expected, actual);
    }

    @Test
    public void createTextBlockTest2() {
        String actual = HTMLUtils.createTextBlock("<script>");

        String expected = "<pre>&lt;script&gt;</pre>";

        assertEquals(expected, actual);
    }

    @Test
    public void createLinkTest1() {
        String actual = HTMLUtils.createLink("/browse", "Browse Records");

        String expected = "<a href=\"/browse\">Browse Records</a>";

        assertEquals(expected, actual);
    }

    @Test
    public void createLinkTest2() {
        String actual = HTMLUtils.createLink("/search?a=1&b=2", "<Search>");

        String expected = "<a href=\"/search?a=1&amp;b=2\">&lt;Search&gt;</a>";

        assertEquals(expected, actual);
    }

    @Test
    public void createLinkListTest1() {
        String[][] links = {
                {"/agents", "Browse Agents"},
                {"/missions", "Browse Missions"}
        };

        String actual = HTMLUtils.createLinkList(links);

        String expected = "<ul>"
                + "<li><a href=\"/agents\">Browse Agents</a></li>"
                + "<li><a href=\"/missions\">Browse Missions</a></li>"
                + "</ul>";

        assertEquals(expected, actual);
    }

    @Test
    public void createLinkListTest2() {
        String actual = HTMLUtils.createLinkList(null);

        String expected = "<ul></ul>";

        assertEquals(expected, actual);
    }

    @Test
    public void createLinkListTest3() {
        String[][] links = {
                {"/agents", "Browse Agents"},
                null,
                {"/missions"},
                {"/facilities", "Browse Facilities"}
        };

        String actual = HTMLUtils.createLinkList(links);

        String expected = "<ul>"
                + "<li><a href=\"/agents\">Browse Agents</a></li>"
                + "<li><a href=\"/facilities\">Browse Facilities</a></li>"
                + "</ul>";

        assertEquals(expected, actual);
    }

    @Test
    public void createIDFormTest1() {
        String actual = HTMLUtils.createIDForm("/read", "Enter ID:");

        String expected = """
                <form method="GET" action="/read">
                    <label for="id">Enter ID:</label>
                    <input type="number" id="id" name="id" required>
                    <button type="submit">Read</button>
                </form>
                """;

        assertEquals(expected, actual);
    }

    @Test
    public void createIDFormTest2() {
        String actual = HTMLUtils.createIDForm("/read?a=1&b=2", "<Enter ID>");

        String expected = """
                <form method="GET" action="/read?a=1&amp;b=2">
                    <label for="id">&lt;Enter ID&gt;</label>
                    <input type="number" id="id" name="id" required>
                    <button type="submit">Read</button>
                </form>
                """;

        assertEquals(expected, actual);
    }

    @Test
    public void createReadFormTest1() {
        String actual = HTMLUtils.createReadForm("Agent");

        String expected = """
                <form method="GET" action="/read">
                    <input type="hidden" name="type" value="Agent">
                
                    <label for="id">Enter Agent ID:</label>
                    <input type="number" id="id" name="id" required>
                
                    <button type="submit">Read</button>
                </form>
                """;

        assertEquals(expected, actual);
    }

    @Test
    public void createBackLinkTest1() {
        String actual = HTMLUtils.createBackLink();

        String expected = "<a href=\"/\">Back to Main Menu</a>";

        assertEquals(expected, actual);
    }


}