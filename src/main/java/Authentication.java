public class Authentication implements AuthenticationInterface {
    @Override
    public boolean credentialExists() {
        return false;
    }

    @Override
    public void createCredentials(String username, String password) throws TopSecretException {

    }

    @Override
    public boolean verifyCredentials(String username, String password) {
        return false;
    }

    @Override
    public void changePassword(String username, String password) throws TopSecretException {

    }

    @Override
    public boolean isValidUsername(String username) {
        return false;
    }

    @Override
    public boolean isValidPassword(String password) {
        return false;
    }
}
