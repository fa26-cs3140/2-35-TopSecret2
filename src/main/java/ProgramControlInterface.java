import java.util.List;

public interface ProgramControlInterface {
    List<String> listFiles();
    String getFileContents(int fileNumber, String keyFile) throws TopSecretException;
}
