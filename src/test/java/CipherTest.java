import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class CipherTest {
    Cipher defaultKeyCipher;
    Cipher key2Cipher;

    @BeforeEach
    void setUp() {
        try {
            defaultKeyCipher = new Cipher("key.txt");
            key2Cipher = new Cipher("key2.txt");
            
        } catch (TopSecretException e) {
            Assertions.fail("Unexpected exception", e);
        }
    }

    @Test
    void decipherTestCase1() {
        String defaultKeyInput = "Uif rvjdl cspxo gpy kvnqt pwfs uif mbAz eph";
        String key2Input = "ftQ FMrOa JpHIw iHe 1M2UR H3Qp gtQ YmjV AHs";
        String expectedString = "The quick brown fox jumps over the lazy dog";

        assertEquals(expectedString, defaultKeyCipher.decipher(defaultKeyInput));
        assertEquals(expectedString, key2Cipher.decipher(key2Input));
    }

    @Test
    void decipherTestCase2() {
        String defaultKeyInput = "UIF RVJDL CSPXO GPY KVNQT PWFS UIF MB1Z EPH";
        String key2Input = "fcy lP4vk LnCW6 DC0 bPXoB Cqyn fcy 8EZ5 7Cd";
        String expectedString = "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG";

        assertEquals(expectedString, defaultKeyCipher.decipher(defaultKeyInput));
        assertEquals(expectedString, key2Cipher.decipher(key2Input));
    }

    @Test
    void decipherTestCase3() {
        String defaultKeyInput = "Kfooz 978-64a0";
        String key2Input = "bQwwV SN9-xuGh";
        String expectedString = "Jenny 867-5309";

        assertEquals(expectedString, defaultKeyCipher.decipher(defaultKeyInput));
        assertEquals(expectedString, key2Cipher.decipher(key2Input));
    }

    @Test
    void decipherTestCase4() {
        String defaultKeyInput = "@#*&*(!^<>";
        String key2Input = "}#*&*(!^<>";
        String expectedString = "@#*&*(!^<>";

        assertEquals(expectedString, defaultKeyCipher.decipher(defaultKeyInput));
        assertEquals(expectedString, key2Cipher.decipher(key2Input));
    }

    @Test
    void decipherTestCase5() {
        String defaultKeyInput = "ntu4l@wjshjojb.fev";
        String key2Input = "2Rgua}3rpsrwrm.QAM";
        String expectedString = "mst3k@virginia.edu";

        assertEquals(expectedString, defaultKeyCipher.decipher(defaultKeyInput));
        assertEquals(expectedString, key2Cipher.decipher(key2Input));
    }

    @Test
    void decipherTestCase6() {
        String defaultKeyInput = "Ifz!\tXifsf't Qfssz?";
        String key2Input = "cQV!\tWtQpQ'R oQppV?";
        String expectedString = "Hey!\tWhere's Perry?";

        assertEquals(expectedString, defaultKeyCipher.decipher(defaultKeyInput));
        assertEquals(expectedString, key2Cipher.decipher(key2Input));
    }
}
