import java.util.List;

public interface ProgramControl {

    List<String> listFiles();

    String getFileContents(int fileNumber, String keyPath) throws TopSecretException;
}