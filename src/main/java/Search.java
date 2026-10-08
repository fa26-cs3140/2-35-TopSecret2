import java.util.List;

public class Search implements SearchInterface {
    private DataStoreInterface dataStore;


    /**
     * creates an object of Search by specifying the source from the dataStoreObj
     *
     * @param dataStoreObj where we get the list of missions. if null, setMissionListSrc must be manually called after
     *                     object creation. Otherwise, an empty list will be searched
     */
    public Search(DataStoreInterface dataStoreObj){
        this.dataStore = dataStoreObj;
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
