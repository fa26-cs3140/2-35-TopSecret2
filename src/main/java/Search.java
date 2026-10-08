import java.util.List;

public class Search implements SearchInterface {
    private SQLiteDataStore dataStore;

    public Search(SQLiteDataStore dataStoreObj){
        this.SQLiteDataStore = dataStoreObj;
    }

    /**
     * Searches the mission briefs for the provided phrase/word and returns the list of matched Missions
     *
     * @param phrase phrase/word to search in mission briefs, case-insensitive
     * @return {@code ArrayList<Mission>} containing all the Missions whose briefs contains {@code phrase}. Empty list if no match.
     */
    @Override
    public List<Mission> search(String phrase) {
        List<Mission> missions = dataStore.listMissions();

        missions.removeIf(mission -> mission.brief.toLowerCase().contains(phrase.toLowerCase()));

        return missions;
    }
}
