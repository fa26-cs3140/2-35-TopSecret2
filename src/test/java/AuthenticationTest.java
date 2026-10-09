import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Paths;

class AuthenticationTest {
    private static final Path CREDENTIAL_FILE = Paths.get("credentials.cip");
    private Authentication auth;
    @BeforeEach
    void setup() throws Exception{
        Files.deleteIfExists(CREDENTIAL_FILE);
        auth = new Authentication();//resets the credential file
    }
    @Test
    void usernameValidation()
    {
        assertTrue(auth.isValidUsername("seb")); //meets requirements
        assertFalse(auth.isValidUsername("Seb")); //no uppercase
        assertFalse(auth.isValidUsername("seb1")); //no numbers
        assertFalse(auth.isValidUsername("seb!")); //no non-letters
        assertFalse(auth.isValidUsername("")); //no empty
        assertFalse(auth.isValidUsername(null)); //no null
    }
    @Test
    void passwordValidation(){
        assertTrue(auth.isValidPassword("12345")); //valid @ 5 characters exactly
        assertTrue(auth.isValidPassword("12345678910")); //valid @ >5 characters
        assertFalse(auth.isValidPassword("1234")); //less than 5
        assertFalse(auth.isValidPassword("")); //no empty
        assertFalse(auth.isValidPassword(null)); //no null
    }
    @Test
    void createCredentialsCreatesFile() throws Exception{
        assertFalse(auth.credentialExists()); //there should be no file
        auth.createCredentials("seb", "12345"); //creates with valid inputs
        assertTrue(auth.credentialExists()); //.cip should exist
    }
    @Test
    void createCredentialsRejectsBadInput(){
        assertThrows(TopSecretException.class, () -> auth.createCredentials("Seb", "12345")); //bad username
        assertThrows(TopSecretException.class, () -> auth.createCredentials("seb", "1234")); //bad password
        assertThrows(TopSecretException.class, () -> auth.createCredentials("Seb", "1234")); //bad both
        assertFalse(auth.credentialExists()); //none of these should have created file
    }
    @Test
    void credentialsCheckStoredPair() throws Exception{
        auth.createCredentials("seb", "12345");
        assertTrue(auth.verifyCredentials("seb", "12345")); //right user/pass combo
        assertFalse(auth.verifyCredentials("seb", "wrong"));//right user, wrong pass
        assertFalse(auth.verifyCredentials("wrong", "12345")); //wrong user, right pass
        assertFalse(auth.verifyCredentials("wrong", "wrong")); //wrong user/pass combo
    }
    @Test
    void failsWithNoFile(){
        assertFalse(auth.verifyCredentials("seb", "12345")); //no file, but shouldn't crash

    }
    @Test
    void ChangePasswordOverwrites() throws Exception{
        auth.createCredentials("seb", "oldpass");
        auth.changePassword("newpass", "newpass");
        assertTrue(auth.verifyCredentials("seb", "newpass")); //new password works
        assertFalse(auth.verifyCredentials("seb", "oldpass")); //older password, shouldn't work
    }
    @Test
    void changePasswordRejectsMismatches() throws Exception{
        auth.createCredentials("seb", "oldpass");
        assertThrows(TopSecretException.class, () -> auth.changePassword("newpass" ,"notnewpass")); //mismatched new passwords should throw an error
        assertTrue(auth.verifyCredentials("seb", "oldpass")); //the file should be untouched
    }
    @Test
    void changePasswordRejectsInvalidPassword() throws Exception{
        auth.createCredentials("seb", "oldpass");
        assertThrows(TopSecretException.class, () -> auth.changePassword("a", "a")); //not a valid password should throw
        assertTrue(auth.verifyCredentials("seb", "oldpass")); //file shouldn't have been touched
    }

}