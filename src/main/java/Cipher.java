import java.util.HashMap;
import java.util.ArrayList;

public class Cipher {
    private HashMap<Character, Character> substitutions;


    public Cipher() {
        // TODO: Implement exception

        ArrayList<String> keyLines =
                FileHandler.getFileLinesList(
                        "key.txt",
                        FileHandler.FileTypes.CIPHER
                );

        if (keyLines.size() != 2) {
            // TODO: Implement exception
        }

        String regularCharacters = keyLines.get(0);
        String cipherCharacters = keyLines.get(1);

        if (regularCharacters.length() != cipherCharacters.length()) {
            // TODO: Implement exception
        }

        int numKeyCharacters = regularCharacters.length();

        substitutions = new HashMap<>(numKeyCharacters);

        for (int characterIdx = 0;
             characterIdx < numKeyCharacters;
             ++characterIdx) {
            substitutions.put(
                    regularCharacters.charAt(characterIdx),
                    cipherCharacters.charAt(characterIdx)
            );
        }
    }

    public String decipher(String cipherText) {
        // TODO: Implement substitution cipher

        return cipherText;
    }

}
