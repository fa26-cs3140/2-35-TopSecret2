import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchToDataStoreIntegrationTest {
    private Search searchObj;
    private SQLiteDataStore dataStore;

    @BeforeEach
    void beforeEach() {
        dataStore = new SQLiteDataStore();
        searchObj = new Search(dataStore);
    }

    @Test
    public void searchFoundScenario1() {

        List<Mission> actual = searchObj.search("secUre tHe");
        List<Mission> expected = List.of(
                new Mission(1,"The Tehran Handshake","1979-02-11",
                        "Secure the shredding room at the US Embassy before the revolutionary guard breaches the perimeter."),
                new Mission(2,"Project Seraphim","1992-02-28",
                        "Secure the loose tactical nukes from the decommissioned base in Kazakhstan."),
                new Mission(3,"The Quantum Leap","2024-02-14",
                        "Secure the first functional quantum computer from the secret research facility in Shanghai.")
        );

        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).getTitle(), actual.get(i).getTitle());
            assertEquals(expected.get(i).getDate(), actual.get(i).getDate());
            assertEquals(expected.get(i).getBrief(), actual.get(i).getBrief());
        }
    }



    @Test
    public void searchFoundScenario2() {

        List<Mission> actual = searchObj.search("TACTICAL");
        List<Mission> expected = List.of(
                new Mission(2, "Project Seraphim", "1992-02-28",
                        "Secure the loose tactical nukes from the decommissioned base in Kazakhstan."),
                new Mission(5, "The Crimea Freeze", "2014-03-15",
                        "Identify the 'Little Green Men' by intercepting their unencrypted tactical radios.")
        );

        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).getTitle(), actual.get(i).getTitle());
            assertEquals(expected.get(i).getDate(), actual.get(i).getDate());
            assertEquals(expected.get(i).getBrief(), actual.get(i).getBrief());
        }
    }

    @Test
    public void searchNotFoundScenario1() {

        List<Mission> actual = searchObj.search("NonExistentword");

        assertEquals(0, actual.size());
    }

}
