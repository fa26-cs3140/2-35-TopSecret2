import java.util.List;

// Methods the user interface calls to get files and their contents.
public interface ProgramControl {

    // Returns the names of the available files, in display order.
    List<String> listFiles();

    // Returns the file's readable contents, using the default key if keyPath is null.
    String getFileContents(int fileNumber, String keyPath) throws TopSecretException;
}