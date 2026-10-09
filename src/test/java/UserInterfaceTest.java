import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Unit tests for UserInterface, using a mock ProgramControl.
class UserInterfaceTest {
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outBytes;
    private ByteArrayOutputStream errBytes;
    private UserInterface uiClass;

    @Mock
    AuthenticationInterface authMock =
            mock(AuthenticationInterface.class);

    @Mock
    ProgramControlInterface PCMock =
            mock(ProgramControlInterface.class);

    // Captures printed output and creates a fresh UI before each test.
    @BeforeEach
    void setUp() {
        outBytes = new ByteArrayOutputStream();
        errBytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBytes));
        System.setErr(new PrintStream(errBytes));
        uiClass = new UserInterface(PCMock);
    }

    // Restores normal screen output after each test.
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
    }

    // Returns what was printed to normal output and error output.
    private String out() { return outBytes.toString().replace("\r\n", "\n"); }
    private String err() { return errBytes.toString(); }

    @Test
    void basicOutputTest() {
        String[] inputArgs = {};
        String input = "5\n";
        assertTrue(out().contains("""
                ------------------------------
                Welcome to TopSecret
              
                Available Options:
               
                1. List missions
                2. Search missions
                3. View mission
                4. Change password
                5. Exit
                ------------------------------
                Enter an option number:\s"""));
        assertEquals(0, uiClass.run(inputArgs));
    }

}