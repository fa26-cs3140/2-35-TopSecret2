import java.util.HashMap;
import java.util.ArrayList;

public class Cipher {
    private HashMap<Character, Character> substitutions;

    public Cipher(String keyFileName) throws TopSecretException {
        ArrayList<String> keyLines =
                FileHandler.getFileLinesList(
                        keyFileName,
                        FileHandler.FileTypes.CIPHER
                );

        // Cannot decipher without a key
        if (keyLines == null) {
            throw new TopSecretException("Cipher key does not exist");
        }

        // Valid key should have 2 lines
        if (keyLines.size() != 2) {
            throw new TopSecretException("Invalid cipher key provided");
        }

        String regularCharacters = keyLines.get(0);
        String cipherCharacters = keyLines.get(1);

        // Valid key should have pairs of characters
        if (regularCharacters.length() != cipherCharacters.length()) {
            throw new TopSecretException("Invalid cipher key provided");
        }

        int numKeyCharacters = regularCharacters.length();

        // Initialize substitutions map with set capacity
        substitutions = new HashMap<>(numKeyCharacters);

        for (int characterIdx = 0;
             characterIdx < numKeyCharacters;
             ++characterIdx) {
            substitutions.put(
                    // HashMap Key is cipher character because deciphering
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
