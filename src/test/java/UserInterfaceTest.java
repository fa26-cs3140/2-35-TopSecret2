import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Unit tests for UserInterface, using a mock ProgramControl.
class UserInterfaceTest {

    // Mock ProgramControl that returns set values and records what the UI asked for.
    static class FakeControl implements ProgramControl {
        List<String> files = new ArrayList<>(List.of("filea.txt", "fileb.txt", "filec.txt"));
        String contents = "secret contents";
        TopSecretException toThrow;
        RuntimeException runtimeToThrow;
        Integer lastNumber;
        String lastKey;
        boolean contentsCalled;

        public List<String> listFiles() { return files; }

        public String getFileContents(int n, String key) throws TopSecretException {
            contentsCalled = true;
            lastNumber = n;
            lastKey = key;
            if (runtimeToThrow != null) throw runtimeToThrow;
            if (toThrow != null) throw toThrow;
            return contents;
        }
    }

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outBytes;
    private ByteArrayOutputStream errBytes;
    private FakeControl control;
    private UserInterface ui;

    // Captures printed output and creates a fresh UI before each test.
    @BeforeEach
    void setUp() {
        outBytes = new ByteArrayOutputStream();
        errBytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBytes));
        System.setErr(new PrintStream(errBytes));
        control = new FakeControl();
        ui = new UserInterface(control);
    }

    // Restores normal screen output after each test.
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // Returns what was printed to normal output and error output.
    private String out() { return outBytes.toString().replace("\r\n", "\n"); }
    private String err() { return errBytes.toString(); }

    // Tests for running with no arguments
    @Test
    void noArgsListsNumberedFiles() {
        assertEquals(0, ui.run(new String[]{}));
        assertEquals("01 filea.txt\n02 fileb.txt\n03 filec.txt\n", out());
    }

    @Test
    void nullArgsTreatedAsNoArgs() {
        assertEquals(0, ui.run(null));
        assertTrue(out().startsWith("01 filea.txt"));
    }

    @Test
    void noArgsWithNoFilesPrintsMessage() {
        control.files = List.of();
        assertEquals(0, ui.run(new String[]{}));
        assertTrue(out().contains("No files available"));
    }

    // Tests for the file number argument
    @Test
    void validNumberDisplaysContentsWithDefaultKey() {
        assertEquals(0, ui.run(new String[]{"01"}));
        assertEquals(1, control.lastNumber);
        assertNull(control.lastKey);
        assertTrue(out().contains("secret contents"));
    }

    @Test
    void numberWithoutLeadingZeroAccepted() {
        assertEquals(0, ui.run(new String[]{"2"}));
        assertEquals(2, control.lastNumber);
    }

    @Test
    void nonNumericArgumentRejected() {
        assertEquals(1, ui.run(new String[]{"abc"}));
        assertFalse(control.contentsCalled);
        assertTrue(err().contains("not a valid file number"));
    }

    @Test
    void zeroRejected() {
        assertEquals(1, ui.run(new String[]{"00"}));
        assertFalse(control.contentsCalled);
    }

    @Test
    void negativeRejected() {
        assertEquals(1, ui.run(new String[]{"-1"}));
        assertFalse(control.contentsCalled);
    }

    // Tests for the alternate key and extra arguments
    @Test
    void secondArgumentPassedAsAlternateKey() {
        assertEquals(0, ui.run(new String[]{"01", "ciphers/alt.txt"}));
        assertEquals("ciphers/alt.txt", control.lastKey);
    }

    @Test
    void blankKeyRejected() {
        assertEquals(1, ui.run(new String[]{"01", "  "}));
        assertFalse(control.contentsCalled);
    }

    @Test
    void tooManyArgumentsRejected() {
        assertEquals(1, ui.run(new String[]{"01", "key.txt", "extra"}));
        assertFalse(control.contentsCalled);
        assertTrue(err().contains("Usage"));
    }

    // Tests for errors from Program Control
    @Test
    void controlErrorPrintedWithoutCrashing() {
        control.toThrow = new TopSecretException("File 09 does not exist.");
        assertEquals(1, ui.run(new String[]{"09"}));
        assertTrue(err().contains("File 09 does not exist."));
    }

    @Test
    void unexpectedRuntimeErrorHandledGracefully() {
        control.runtimeToThrow = new IllegalStateException("boom");
        assertEquals(1, ui.run(new String[]{"01"}));
        assertTrue(err().startsWith("Error:"));
    }

    // Tests for the helper methods
    @Test
    void formatListEntryPadsToTwoDigits() {
        assertEquals("05 x.txt", UserInterface.formatListEntry(5, "x.txt"));
        assertEquals("12 y.txt", UserInterface.formatListEntry(12, "y.txt"));
    }

    @Test
    void parseFileNumberCases() {
        assertEquals(1, UserInterface.parseFileNumber("01"));
        assertEquals(7, UserInterface.parseFileNumber(" 7 "));
        assertEquals(-1, UserInterface.parseFileNumber("x1"));
        assertEquals(-1, UserInterface.parseFileNumber(""));
        assertEquals(-1, UserInterface.parseFileNumber(null));
        assertEquals(-1, UserInterface.parseFileNumber("1.5"));
    }
}
