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

        // Valid key should have 2 lines
        if (keyLines.size() != 2) {
            // TODO: Implement exception
        }

        String regularCharacters = keyLines.get(0);
        String cipherCharacters = keyLines.get(1);

        // Valid key should have pairs of characters
        if (regularCharacters.length() != cipherCharacters.length()) {
            // TODO: Implement exception
        }

        int numKeyCharacters = regularCharacters.length();

        // Initialize substitutions map with set capacity
        substitutions = new HashMap<>(numKeyCharacters);

        for (int characterIdx = 0;
             characterIdx < numKeyCharacters;
             ++characterIdx) {
            substitutions.put(
                    // Key is cipher character because we are deciphering
                    cipherCharacters.charAt(characterIdx),
                    regularCharacters.charAt(characterIdx)
            );
        }
    }

    public String decipher(String cipherText) {
        StringBuilder plaintextBuilder = new StringBuilder(cipherText);

        for (int characterIdx = 0;
             characterIdx < cipherText.length();
             ++characterIdx) {
            Character currCipheredCharacter =
                    cipherText.charAt(characterIdx);

            Character currPlaintextCharacter =
                    substitutions.get(currCipheredCharacter);

            // Check if character was ciphered
            if (currPlaintextCharacter != null) {
                plaintextBuilder.setCharAt(characterIdx, currPlaintextCharacter);
            }

            // Non-ciphered characters are unchanged
        }
        return plaintextBuilder.toString();
    }

}
