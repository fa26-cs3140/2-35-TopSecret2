import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private DataStoreInterface dataStore;

    public UserManager(DataStoreInterface dataStore) {
        this.dataStore = dataStore;
    }

    private int getNextUserID() {
        List<User> usersList = dataStore.listUsers();

        if (usersList.size() == 0) {
            return 1;
        }

        return (usersList.size() + 1);
    }

    boolean isUsernameValid(String username) {
       if (username.length() > 2) {
           String usernameNoValidChars =
                   username.replaceAll("[a-z]|[0-9]|-|_", "");

           if (usernameNoValidChars.isEmpty()) {
               return true;
           } else {
               return false;
           }

       } else {
           return false;
       }
    }

    boolean isPasswordValid(String password) {
        if (password.length() > 4) {
            int numUppercaseChars = 0;
            int numLowercaseChars = 0;

            for (int charIdx = 0;
                 charIdx < password.length();
                 charIdx++) {
                Character currChar = password.charAt(charIdx);

                if (Character.isUpperCase(currChar)) {
                    numUppercaseChars++;
                } else if (Character.isLowerCase(currChar)) {
                    numLowercaseChars++;
                }
            }

            if ((numUppercaseChars > 0) & (numLowercaseChars > 0)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    boolean hasUser() {
        List<User> currentUsers = dataStore.listUsers();

        if (currentUsers.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    User createUser(String username) {
        if (isUsernameValid(username)) {
            User newUser = new User(
                    getNextUserID(),
                    username
            );

            try {
                dataStore.addUser(newUser);
            } catch (IllegalArgumentException e) {
                return null;
            }

            return newUser;
        }

        return null;
    }

    User getUserByID(int userID) {
        try {
            User retrievedUser = dataStore.getUser(userID);
            return retrievedUser;
        } catch (RuntimeException e) {
            return null;
        }
    }

    boolean verifyLogin(String username, String Password) {
        // TODO: Implement credential check
        return true;
    }

    void changePassword(String username, String newPassword1, String newPasswordConfirmation) {
        // TODO: Implement password change
    }

    boolean needsPasswordSetup(String username) {
        // TODO: Implement password status
        return false;
    }

    User getUserByUsername(String username) {
        List<User> usersList = dataStore.listUsers();

        for (int currIdx = 0; currIdx < usersList.size(); currIdx++) {
            User currUser = usersList.get(currIdx);

            if (username.equals(currUser.getUsername())) {
                return currUser;
            }
        }

        return null;
    }

    List<String> listUsernames() {
        List<User> currUsers = dataStore.listUsers();
        List<String> usernameEntries = new ArrayList<String>();

        for (int userIdx = 0; userIdx < currUsers.size(); userIdx++) {
            User currUser = currUsers.get(userIdx);
            usernameEntries.add(currUser.getUsername());
        }

        return usernameEntries;

    }

}
