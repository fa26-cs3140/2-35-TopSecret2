import java.util.List;

public interface SearchInterface {
    /**
     * Searches the mission briefs for the provided phrase/word and returns the list of matched mission briefs
     *
     * @param phrase phrase/word to search in mission briefs
     * @return {@code ArrayList<String>} containing all the matched mission briefs. Empty if no match.
     */
    List<Mission> search(String phrase);
}
