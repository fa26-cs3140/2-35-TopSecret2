import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Authentication implements AuthenticationInterface {
    private static final Path CREDENTIAL_FILE = Paths.get("credentials.cip");
    private static final String KEY_FILE = "key.txt";
    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyz"; //valid username characters
    @Override
    public boolean credentialExists() { //this is true if there is a credential file
        return Files.exists(CREDENTIAL_FILE);
    }

    @Override //makes a new credential
    public void createCredentials(String username, String password) throws TopSecretException {
        if (!isValidUsername(username) ){
            throw new TopSecretException("Invalid Username, make sure it is only lowercase letters!");
        } else if (!isValidPassword(password)) {
            throw new TopSecretException("Invalid password, make sure it is over 5 characters!");
        }
        try {
            String ciphered = new Cipher(KEY_FILE).encipher(username + ":" + password);
            Files.writeString(CREDENTIAL_FILE, ciphered);
        }
        catch (IOException e){
            throw new TopSecretException("Couldn't write to file", e);

    }
    }

    @Override
    public boolean verifyCredentials(String username, String password) {
        if (!credentialExists()){
            return false; //no credentials means no login
        }
        try {
            String ciphered = Files.readString(CREDENTIAL_FILE).strip();
            String stored = new Cipher(KEY_FILE).decipher(ciphered);
            return stored.equals(username + ":" + password); //True if stored matches entered usernames
        } catch (Exception e) {
            return false; //any issues with the files, means no login
        }
    }

    @Override
    public void changePassword(String username, String password) throws TopSecretException {
        if (!credentialExists()){
            throw new TopSecretException("No credential File Found");
        }
        if (!isValidPassword(password)){
            throw new TopSecretException("Invalid username or password");
        }
        try {
            String currentPassword = new Cipher(KEY_FILE).decipher(Files.readString(CREDENTIAL_FILE).strip());
            if (!currentPassword.split(":")[0].equals(username)){
                throw new TopSecretException("Username doesn't match existing username, please only change the password");
            }
            String ciphered = new Cipher(KEY_FILE).encipher(username + ":" + password);
            Files.writeString(CREDENTIAL_FILE, ciphered);
        } catch (IOException e) {
            throw new TopSecretException("Couldn't write a credential file", e);
        }
    }

    @Override
    public boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()){ //edge cases (null/empty)
            return false;
        }
        for (int i = 0; i < username.length(); i++){
            if (ALLOWED_CHARACTERS.indexOf(username.charAt(i)) == -1){
                return false; //checks against list of allowed characters (currently all lowercase letters)
            }
        }
        return true;
    }

    @Override
    public boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()){ //edge cases (null/empty)
            return false;}
        else if (password.length() < 5) {
            return false;
        }
        return true;
    }
}
