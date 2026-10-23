import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Member D (Jaime) -- HW5 user accounts.
// User records live in the database (through C's store); passwords live in
// a ciphered file, credentials.cip in the working directory, one ciphered
// "username:password" line per user -- the HW4 Authentication pattern,
// extended to multiple users.
public class UserManager implements UserManagerInterface {
    private static final Path CREDENTIAL_FILE = Paths.get("credentials.cip");
    private static final String KEY_FILE = "key.txt";
    //valid username characters
    private static final String ALLOWED_CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";

    private DataStoreInterface dataStore;

    public UserManager(DataStoreInterface dataStore) {
        this.dataStore = dataStore;
    }

    // One more than the highest ID in use, so deletions can't cause a clash
    private int getNextUserID() {
        int maxID = 0;
        for (User user : dataStore.listUsers()) {
            if (user.getUserID() > maxID) {
                maxID = user.getUserID();
            }
        }
        return maxID + 1;
    }

    // At least 3 characters, letters/numbers/hyphens/underscores only
    boolean isUsernameValid(String username) {
        if (username == null || username.length() < 3) {
            return false;
        }
        for (int i = 0; i < username.length(); i++) {
            if (ALLOWED_CHARACTERS.indexOf(username.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    // At least 5 characters, with an uppercase and a lowercase letter
    boolean isPasswordValid(String password) {
        if (password == null || password.length() < 5) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                hasUpper = true;
            } else if (Character.isLowerCase(password.charAt(i))) {
                hasLower = true;
            }
        }
        return hasUpper && hasLower;
    }

    // Reads the ciphered password file into a username -> password map.
    // No file yet means no passwords have been set.
    private Map<String, String> loadCredentials() throws TopSecretException {
        Map<String, String> credentials = new LinkedHashMap<>();
        if (!Files.exists(CREDENTIAL_FILE)) {
            return credentials;
        }
        try {
            Cipher cipher = new Cipher(KEY_FILE);
            for (String line : Files.readAllLines(CREDENTIAL_FILE)) {
                if (line.isEmpty()) {
                    continue;
                }
                String stored = cipher.decipher(line);
                int colon = stored.indexOf(':');
                if (colon > 0) {
                    credentials.put(stored.substring(0, colon),
                            stored.substring(colon + 1));
                }
            }
        } catch (IOException e) {
            throw new TopSecretException("Couldn't read the credential file", e);
        }
        return credentials;
    }

    // Writes the whole map back, one ciphered line per user.
    private void saveCredentials(Map<String, String> credentials)
            throws TopSecretException {
        try {
            Cipher cipher = new Cipher(KEY_FILE);
            List<String> lines = new ArrayList<>();
            for (Map.Entry<String, String> entry : credentials.entrySet()) {
                lines.add(cipher.encipher(
                        entry.getKey() + ":" + entry.getValue()));
            }
            Files.write(CREDENTIAL_FILE, lines);
        } catch (IOException e) {
            throw new TopSecretException("Couldn't write to file", e);
        }
    }

    @Override
    public boolean hasUser() {
        return !dataStore.listUsers().isEmpty();
    }

    @Override
    public User createUser(String username) throws TopSecretException {
        if (!isUsernameValid(username)) {
            throw new TopSecretException("Invalid username, make sure it is "
                    + "at least 3 characters using only letters, numbers, "
                    + "hyphens and underscores!");
        }
        if (getUserByUsername(username) != null) {
            throw new TopSecretException("Username already exists");
        }
        User newUser = new User(getNextUserID(), username);
        try {
            dataStore.addUser(newUser);
        } catch (IllegalArgumentException e) {
            throw new TopSecretException("Could not create user", e);
        }
        return newUser;
    }

    @Override
    public User getUserByID(int userID) throws TopSecretException {
        try {
            return dataStore.getUser(userID);
        } catch (RuntimeException e) {
            throw new TopSecretException("Could not look up user", e);
        }
    }

    @Override
    public boolean verifyLogin(String username, String password) {
        try {
            String stored = loadCredentials().get(username);
            return stored != null && stored.equals(password);
        } catch (Exception e) {
            return false; //any issues with the files means no login
        }
    }

    @Override
    public void changePassword(String username, String newPassword1,
                               String newPasswordConfirmation) throws TopSecretException {
        if (getUserByUsername(username) == null) {
            throw new TopSecretException("User does not exist");
        }
        if (newPassword1 == null
                || !newPassword1.equals(newPasswordConfirmation)) {
            throw new TopSecretException("Passwords don't match!");
        }
        if (!isPasswordValid(newPassword1)) {
            throw new TopSecretException("Invalid password, make sure it is "
                    + "at least 5 characters with uppercase and lowercase "
                    + "letters!");
        }
        Map<String, String> credentials = loadCredentials();
        credentials.put(username, newPassword1);
        saveCredentials(credentials);
    }

    @Override
    public boolean needsPasswordSetup(String username) {
        try {
            return !loadCredentials().containsKey(username);
        } catch (Exception e) {
            return true; //no readable credentials means no password set
        }
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null) {
            return null;
        }
        for (User currUser : dataStore.listUsers()) {
            if (username.equals(currUser.getUsername())) {
                return currUser;
            }
        }
        return null;
    }

    @Override
    public List<String> listUsernames() {
        List<String> usernameEntries = new ArrayList<String>();
        for (User currUser : dataStore.listUsers()) {
            usernameEntries.add(currUser.getUsername());
        }
        return usernameEntries;
    }
}
