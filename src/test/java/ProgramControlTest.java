import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProgramControlTest {

    @Test
    void listFilescase1() {
        //Test case 1 (returns existing files)
        ProgramControl control = new ProgramControl();
        List<String> files = control.listFiles();
        assertEquals(List.of("carnivore.cip", "cointelpro.cip"), files);
    }
    /*@Test //Commented out since not using Mockito to stub out things yet
    void listFilescase2() {
        ProgramControl control = new ProgramControl();
        List<String> files = control.listFiles();
        assertTrue(files.isEmpty());
    }*/

    @Test
    void getFileContentscase3() throws TopSecretException{
        //test case 3 (valid file number and default key)
        ProgramControl control = new ProgramControl();
        String fileContents = control.getFileContents(1, null);
        assertNotNull(fileContents);
    }
    @Test
    void getFilecontentscase4() throws TopSecretException{
        //test case 4 (valid file, non-default key)
        ProgramControl control = new ProgramControl();
        String fileContents = control.getFileContents(1, "key2.txt");
        assertNotNull(fileContents);
    }
    @Test
    void getFileContentscase5(){
        //test case 5 (file number not ever possible (<1)
        ProgramControl control = new ProgramControl();
        assertThrows(TopSecretException.class,
                () -> control.getFileContents(0, null));
    }
    @Test
    void getFileContentscase6(){
        //test case 6 (file number exceeds total files)
        ProgramControl control = new ProgramControl();
        int invalidFileNumber = control.listFiles().size() + 1;
        assertThrows(
                TopSecretException.class,
                () -> control.getFileContents(invalidFileNumber, null)
        );
    }
    @Test
    void getFileContentscase7() {
        //test case 7 (valid file, invalid cipher)
        ProgramControl control = new ProgramControl();
        assertThrows(
                TopSecretException.class,
                () -> control.getFileContents(1, "will_not_work.txt")
        );
    }
    /*@Test Commented out because requires a corrupted file
    void getFileContentscase8() {
        // test case 8 (valid file number, file doesn't exist/is corrupted)
        ProgramControl control = new ProgramControl();
        assertThrows(
                TopSecretException.class,
                () -> control.getFileContents(1, null)
        );
    }*/
}