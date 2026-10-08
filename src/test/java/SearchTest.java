import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class SearchTest {
    private Search searchObj;

    @Mock
    private DataStoreInterface mockDataStore;

    @BeforeEach
    void beforeEach(){
        searchObj = new Search(mockDataStore);
    }


    @Test
    public void searchFoundScenario1() {
        //TODO assertEquals with mockObj of DataStoreInterface
        //mockDataStore.listMissions() return a List<Mission> of given choice
        searchObj.search("Secure");
    }

    @Test
    public void searchFoundScenario2() {
        //TODO assertEquals with mockObj of DataStoreInterface
        searchObj.search("secure");
    }

    @Test
    public void searchFoundScenario3() {
        //TODO assertEquals with mockObj of DataStoreInterface
        searchObj.search("SECURE");
    }

    @Test
    public void searchFoundScenario4() {
        //TODO assertEquals with mockObj of DataStoreInterface
        searchObj.search("sEcUrE");
    }

    @Test
    public void searchFoundScenario5() {
        //TODO assertEquals with mockObj of DataStoreInterface
        searchObj.search("secure the");
    }

    @Test
    public void searchFoundScenario6() {
        //TODO assertEquals with mockObj of DataStoreInterface
        searchObj.search("secure vpn tunnel");
    }

    @Test
    public void searchNotFoundScenario1() {
        //TODO assertEquals with mockObj of DataStoreInterface, must be empty
        searchObj.search("nonExisetentWord");
    }

    @Test
    public void searchNotFoundScenario2() {
        //TODO assertEquals with mockObj of DataStoreInterface, must be empty
        searchObj.search("secureThe");
    }
    
    


}
