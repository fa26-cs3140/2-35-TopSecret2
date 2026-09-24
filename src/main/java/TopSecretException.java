public class TopSecretException extends Exception {
    public TopSecretException(String message) {
        super(message);
    }

    public TopSecretException(String message, Throwable cause) {
        super(message, cause);
    }
}
