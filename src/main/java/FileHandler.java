import java.util.ArrayList;

public class FileHandler {

    /**
     * returns an ArrayList of the lines of the file
     *
     * @param fileName name of the file to read with its file extension
     * @param fileType type of file to read: DATA or CIPHER from {@code enum fileTypes}
     * @return {@code ArrayList<String>} containing the lines of the file. {@code null} if file does not exist
     */
    public static ArrayList<String> getFileLinesList(String fileName, fileTypes fileType) {

        return new ArrayList<>();
    }


    /**
     * returns an ArrayList of the names of data files in src/main/resources/data directory
     * @return {@code ArrayList<String>} of data file names in data directory.
     */
    public static ArrayList<String> listDataFiles() {

        return new ArrayList<String>();
    }

    public enum fileTypes {
        DATA,
        CIPHER
    }


}
