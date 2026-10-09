import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AuthenticationIntegrationTest {
    private static final Path CREDENTIAL_FILE = Paths.get("credentials.cip");
    @BeforeEach
    void setUp() throws Exception{
        Files.deleteIfExists(CREDENTIAL_FILE);
    }
    @Test
    void fullFirstRunCanBeValidated() throws Exception{
        Authentication auth = new Authentication();
        assertFalse(auth.credentialExists());
        auth.createCredentials("username", "password");
        assertTrue(auth.credentialExists()); //make a new credential

        Authentication login = new Authentication(); //second run
        assertTrue(login.verifyCredentials("username", "password"));//the correct username/password should work
        assertFalse(login.verifyCredentials("notmyusername", "fakepassword"));
    }
    @Test
    void passwordChangePropagates() throws Exception{
        Authentication auth = new Authentication();
        auth.createCredentials("firstusername", "firstpassword"); //set up the credentials
        auth.changePassword("firstusername" , "secondpassword"); //replace the password

        assertTrue(auth.verifyCredentials("firstusername", "secondpassword"));
        assertFalse(auth.verifyCredentials("firstusername", "firstpassword"));
        String fileContents = Files.readString(CREDENTIAL_FILE);
        assertFalse(fileContents.contains("firstusername:secondpassword")); //makes sure contents are still ciphered
    }
}
