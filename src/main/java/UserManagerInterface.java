import java.util.List;

public interface UserManagerInterface {
    // Whether a user has been created
    boolean hasUser();

    // If successful, returns object of newly created user
    User createUser(String username) throws TopSecretException;

    User getUserByID(int userID) throws TopSecretException;

    //should verify username and password match and work
    boolean verifyLogin(String username, String Password);
    //changes the username's password to the new password, pending all password requirements are met
    void changePassword(String username, String newPassword1, String newPasswordConfirmation) throws TopSecretException;

    //True if account only has a username, no password
    boolean needsPasswordSetup(String username);

    //returns the user type associated with a username for audit logging
    User getUserByUsername(String username);

    //returns all the usernames as a list type
    List<String> listUsernames();
}
