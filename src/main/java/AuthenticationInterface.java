public interface AuthenticationInterface {
    boolean credentialExists(); //true if .cip exists

    void createCredentials(String username, String password) throws TopSecretException;//Makes a new credential file as long as requirements are met

    boolean verifyCredentials(String username, String password); //true if input matches .cip

    void changePassword(String username, String password) throws TopSecretException; //replaces .cip with new credentials if reqiurements are met

    boolean isValidUsername(String username); //true if no uppercase

    boolean isValidPassword(String password); //true if password more than 5 characters
}