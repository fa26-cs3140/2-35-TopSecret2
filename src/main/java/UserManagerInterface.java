public interface UserManagerInterface {
    // Whether a user has been created
    boolean hasUser();

    // If successful, returns object of newly created user
    User createUser(String username) throws TopSecretException;

    User getUserByID(int userID) throws TopSecretException;
}
