import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.jar.JarFile;

public class FileHandler {

    /**
     * returns an ArrayList of the lines of the file
     *
     * @param fileName name of the file to read with its file extension
     * @param fileType type of file to read: DATA or CIPHER from {@code enum fileTypes}
     * @return {@code ArrayList<String>} containing the lines of the file. {@code null} if file does not exist
     */
    public static ArrayList<String> getFileLinesList(String fileName, FileTypes fileType) {
        String folder = (fileType == FileTypes.DATA ? "data/" : "ciphers/");
        InputStream input = FileHandler.class.getResourceAsStream("/" + folder + fileName);
        String path = "src/main/resources/" + (fileType == FileTypes.DATA ? "data/" : "ciphers/") + fileName;

        if (input == null) {
            return null;
        }

        Scanner scnr = new Scanner(input);
        ArrayList<String> lines = new ArrayList<>();

        while (scnr.hasNextLine()) {
            lines.add(scnr.nextLine());
        }

        scnr.close();
        return lines;
    }


    /**
     * returns an ArrayList of the names of data files in src/main/resources/data directory
     *
     * @return {@code ArrayList<String>} of data file names in data directory.
     */
    public static ArrayList<String> listDataFiles() {
        ArrayList<String> fileNames = new ArrayList<>();

        try {
            URL resource = FileHandler.class.getClassLoader().getResource("data");

            if (resource == null) {
                return null;
            }

            if (resource.getProtocol().equals("file")) {
                // Running from IntelliJ or Gradle
                File folder = new File(resource.toURI());
                String[] files = folder.list();

                if (files != null) {
                    fileNames.addAll(Arrays.asList(files));
                }

            } else if (resource.getProtocol().equals("jar")) {
                // Running from the JAR
                JarURLConnection connection =
                        (JarURLConnection) resource.openConnection();

                try (JarFile jar = connection.getJarFile()) {
                    jar.stream()
                            .filter(entry -> !entry.isDirectory())
                            .filter(entry -> entry.getName().startsWith("data/"))
                            .forEach(entry -> {
                                String name =
                                        entry.getName().substring("data/".length());

                                // Only files directly inside data/
                                if (!name.contains("/")) {
                                    fileNames.add(name);
                                }
                            });
                }
            }

            return fileNames;

        } catch (Exception e) {
            return null;
        }
    }

    public enum FileTypes {
        DATA,
        CIPHER
    }

}
