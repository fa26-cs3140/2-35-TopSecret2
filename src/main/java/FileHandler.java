import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FileHandler {

    /**
     * returns an ArrayList of the lines of the file
     *
     * @param fileName name of the file to read with its file extension
     * @param fileType type of file to read: DATA or CIPHER from {@code enum fileTypes}
     * @return {@code ArrayList<String>} containing the lines of the file. {@code null} if file does not exist
     */
    public static ArrayList<String> getFileLinesList(String fileName, fileTypes fileType) {

        String path = "src/main/resources/" + (fileType == fileTypes.DATA ? "data/" : "ciphers/") + fileName;
        try {
            Scanner scnr = new Scanner(new File(path));
            ArrayList<String> lines = new ArrayList<>();
            while (scnr.hasNextLine()) {
                lines.add(scnr.nextLine());
            }

            return lines;
        } catch (FileNotFoundException e) {
            return null;
        }

    }


    /**
     * returns an ArrayList of the names of data files in src/main/resources/data directory
     *
     * @return {@code ArrayList<String>} of data file names in data directory.
     */
    public static ArrayList<String> listDataFiles() {
        File folder = new File("src/main/resources/data");
        return new ArrayList<>(Arrays.asList(folder.list()));
    }

    public enum fileTypes {
        DATA,
        CIPHER
    }


}
