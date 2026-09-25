import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgramControl {
    private static final String DEFAULT_KEY_FILE = "key.txt";

    public List<String> listFiles() {//Returning all file names available
        ArrayList<String> files;
        try {
            files = FileHandler.listDataFiles();
        } catch (RuntimeException e) {
            return new ArrayList<String>();
        }
        ;
        if (files == null) {
            return new ArrayList<String>();
        }
        Collections.sort(files);
        return files;
    }

    ;

    public String getFileContents(int fileNumber, String keyFile) throws TopSecretException {//Returns the file deciphered;
        List<String> files = listFiles();
        if (fileNumber < 1 || fileNumber > files.size()) {
            throw new TopSecretException("File number does not exist");
        }
        String selectedFileName = files.get(fileNumber - 1);
        ArrayList<String> fileLines = FileHandler.getFileLinesList(selectedFileName, FileHandler.FileTypes.DATA);
        if (fileLines == null) {
            throw new TopSecretException("Data file does not exist");
        }
        String fileContents = String.join(System.lineSeparator(), fileLines);
        String selectedKeyFile;
        if (keyFile == null) {
            selectedKeyFile = DEFAULT_KEY_FILE;
        } else {
            selectedKeyFile = keyFile;
        }
        Cipher cipher = new Cipher(selectedKeyFile);
        return cipher.decipher(fileContents);
    }
}