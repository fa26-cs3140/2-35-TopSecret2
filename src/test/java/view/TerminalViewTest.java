package view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TerminalViewTest {


    @Test
    public void TestTerminalViewTest() {
        TerminalView terminalView = new TerminalView();
        String actual = terminalView.promptMessage("Hello World");

        String expected = "Hello World";
        assertEquals(expected, actual);
    }
}
