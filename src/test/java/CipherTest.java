import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class CipherTest {
    Cipher cipher;

    @BeforeEach
    void setUp() {
        cipher = new Cipher();
    }

    @Test
    void decipherTestCase1() {
        String inputString = "Uif rvjdl cspxo gpy kvnqt pwfs uif mbAz eph";
        String expectedString = "The quick brown fox jumps over the lazy dog";

        assertEquals(expectedString, cipher.decipher(inputString));
    }

    @Test
    void decipherTestCase2() {
        String inputString = "UIF RVJDL CSPXO GPY KVNQT PWFS UIF MB1Z EPH";
        String expectedString = "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG";

        assertEquals(expectedString, cipher.decipher(inputString));
    }

    @Test
    void decipherTestCase3() {
        String inputString = "Kfooz 978-64a0";
        String expectedString = "Jenny 867-5309";

        assertEquals(expectedString, cipher.decipher(inputString));
    }

    @Test
    void decipherTestCase4() {
        String inputString = "@#*&*(!^<>";
        String expectedString = "@#*&*(!^<>";

        assertEquals(expectedString, cipher.decipher(inputString));
    }

    @Test
    void decipherTestCase5() {
        String inputString = "ntu4l@wjshjojb.fev";
        String expectedString = "mst3k@virginia.edu";

        assertEquals(expectedString, cipher.decipher(inputString));
    }

    @Test
    void decipherTestCase6() {
        String inputString = "Ifz!\tXifsf't Qfssz?";
        String expectedString = "Hey!\tWhere's Perry?";

        assertEquals(expectedString, cipher.decipher(inputString));
    }
}
