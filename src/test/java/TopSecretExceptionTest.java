import static org.junit.jupiter.api.Assertions.*;

import model.TopSecretException;
import org.junit.jupiter.api.Test;

// Unit tests for model.TopSecretException.
class TopSecretExceptionTest {

    @Test
    void storesMessage() {
        TopSecretException e = new TopSecretException("File not found.");
        assertEquals("File not found.", e.getMessage());
    }

    @Test
    void storesMessageAndCause() {
        Exception cause = new Exception("original");
        TopSecretException e = new TopSecretException("Could not read file.", cause);
        assertEquals("Could not read file.", e.getMessage());
        assertSame(cause, e.getCause());
    }
}
