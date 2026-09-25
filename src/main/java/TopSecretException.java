// Error used when something goes wrong, like a missing file or invalid key.
// The message is shown to the user.
public class TopSecretException extends Exception {

    // Creates the error with a message.
    public TopSecretException(String message) {
        super(message);
    }

    // Creates the error with a message and the original error that caused it.
    public TopSecretException(String message, Throwable cause) {
        super(message, cause);
    }
}
