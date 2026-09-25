import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class FileHandlerTest {
    Scanner scnr;


    /**
     * Testing when querying for a cipher key file
     */
    @Test
    void getCipherLinesList() {
        try {
            Scanner scnr = new Scanner(new File("src/main/resources/ciphers/key.txt"));
            ArrayList<String> expectedLines = new ArrayList<>();
            while (scnr.hasNextLine()) {
                expectedLines.add(scnr.nextLine());
            }

            ArrayList<String> actualLines = FileHandler.getFileLinesList("key.txt", FileHandler.FileTypes.CIPHER);

            assertArrayEquals(expectedLines.toArray(), actualLines.toArray());
        } catch (FileNotFoundException e) {
            assertEquals(1, 0, "Exception: " + e.getMessage());
        }
    }

    /**
     * Testing when querying for a data file
     */
    @Test
    void getFileLinesList2() {
        try {
            Scanner scnr = new Scanner(new File("src/main/resources/data/carnivore.cip"));
            ArrayList<String> expectedLines = new ArrayList<>();
            while (scnr.hasNextLine()) {
                expectedLines.add(scnr.nextLine());
            }
            System.out.println(expectedLines);
            ArrayList<String> actualLines = FileHandler.getFileLinesList("carnivore.cip", FileHandler.FileTypes.DATA);

            assertArrayEquals(expectedLines.toArray(), actualLines.toArray());
        } catch (FileNotFoundException e) {
            assertEquals(1, 0, "Exception: " + e.getMessage());
        }


    }

    /**
     * Testing when querying for a data file
     */
    @Test
    void getFileLinesList3() {
        try {
            Scanner scnr = new Scanner(new File("src/main/resources/data/cointelpro.cip"));
            ArrayList<String> expectedLines = new ArrayList<>();
            while (scnr.hasNextLine()) {
                expectedLines.add(scnr.nextLine());
            }

            ArrayList<String> actualLines = FileHandler.getFileLinesList("cointelpro.cip", FileHandler.FileTypes.DATA);

            assertArrayEquals(expectedLines.toArray(), actualLines.toArray());
        } catch (FileNotFoundException e) {
            assertEquals(1, 0, "Exception: " + e.getMessage());
        }
    }


    /**
     * Testing when querying for a non-existent file
     */
    @Test
    void getFileLinesList4() {
        ArrayList<String> actualLines = FileHandler.getFileLinesList("example.cip", FileHandler.FileTypes.DATA);
        assertNull(actualLines);
    }


    /**
     * Testing when asking for list of file names in data directory
     */
    @Test
    void listDataFiles() {
        File folder = new File("src/main/resources/data");
        ArrayList<String> expected = new ArrayList<>(Arrays.asList(folder.list()));
        ArrayList<String> actual = FileHandler.listDataFiles();

        assertArrayEquals(expected.toArray(), actual.toArray());
    }
}