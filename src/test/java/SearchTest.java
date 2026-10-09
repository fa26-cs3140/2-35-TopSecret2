import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchTest {
    private Search searchObj;

    @Mock
    private DataStoreInterface mockDataStore;

    @BeforeEach
    void beforeEach() {
        searchObj = new Search(mockDataStore);
    }


    @Test
    public void searchFoundScenario1() {
        //mockDataStore.listMissions() return a List<Mission> of given choice
        //setting up mock test data
        List<Mission> missions = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area that is secure"),
                new Mission(4, "Package", "2010-11-12", "pick up the secured backage")
        );
        when(mockDataStore.listMissions()).thenReturn(missions);


        //begin scenario
        List<Mission> actual = searchObj.search("Secure");
        List<Mission> expected = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area that is secure")
        );

        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).getId(), actual.get(i).getId());
            assertEquals(expected.get(i).getTitle(), actual.get(i).getTitle());
            assertEquals(expected.get(i).getDate(), actual.get(i).getDate());
            assertEquals(expected.get(i).getBrief(), actual.get(i).getBrief());
        }

    }

    @Test
    public void searchFoundScenario2() {
        //setting up mock test data
        List<Mission> missions = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area that is secure"),
                new Mission(4, "Package", "2010-11-12", "pick up the secured backage")
        );
        when(mockDataStore.listMissions()).thenReturn(missions);


        //begin scenario
        List<Mission> actual = searchObj.search("secure");
        List<Mission> expected = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area that is secure")
        );

        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).getId(), actual.get(i).getId());
            assertEquals(expected.get(i).getTitle(), actual.get(i).getTitle());
            assertEquals(expected.get(i).getDate(), actual.get(i).getDate());
            assertEquals(expected.get(i).getBrief(), actual.get(i).getBrief());
        }
    }

    @Test
    public void searchFoundScenario3() {
        //setting up mock test data
        List<Mission> missions = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area that is secure"),
                new Mission(4, "Package", "2010-11-12", "pick up the secured backage")
        );
        when(mockDataStore.listMissions()).thenReturn(missions);


        //begin scenario
        List<Mission> actual = searchObj.search("SECURE");
        List<Mission> expected = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area that is secure")
        );

        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).getId(), actual.get(i).getId());
            assertEquals(expected.get(i).getTitle(), actual.get(i).getTitle());
            assertEquals(expected.get(i).getDate(), actual.get(i).getDate());
            assertEquals(expected.get(i).getBrief(), actual.get(i).getBrief());
        }
    }

    @Test
    public void searchFoundScenario4() {
        //setting up mock test data
        List<Mission> missions = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area that is secure"),
                new Mission(4, "Package", "2010-11-12", "pick up the secured backage")
        );
        when(mockDataStore.listMissions()).thenReturn(missions);


        //begin scenario
        List<Mission> actual = searchObj.search("sEcUrE");
        List<Mission> expected = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area that is secure")
        );

        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).getId(), actual.get(i).getId());
            assertEquals(expected.get(i).getTitle(), actual.get(i).getTitle());
            assertEquals(expected.get(i).getDate(), actual.get(i).getDate());
            assertEquals(expected.get(i).getBrief(), actual.get(i).getBrief());
        }

    }

    @Test
    public void searchFoundScenario5() {
        //setting up mock test data
        List<Mission> missions = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area that is secure"),
                new Mission(4, "Package", "2010-11-12", "pick up the secured backage")
        );
        when(mockDataStore.listMissions()).thenReturn(missions);


        //begin scenario
        List<Mission> actual = searchObj.search("secure the");
        List<Mission> expected = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes.")
        );

        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).getId(), actual.get(i).getId());
            assertEquals(expected.get(i).getTitle(), actual.get(i).getTitle());
            assertEquals(expected.get(i).getDate(), actual.get(i).getDate());
            assertEquals(expected.get(i).getBrief(), actual.get(i).getBrief());
        }
    }

    @Test
    public void searchFoundScenario6() {
        List<Mission> missions = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area that is secure"),
                new Mission(4, "Package", "2010-11-12", "pick up the secured backage and use it to secure the VPN TunNel.")
        );
        when(mockDataStore.listMissions()).thenReturn(missions);


        //begin scenario
        List<Mission> actual = searchObj.search("secure vpn tunnel");
        List<Mission> expected = List.of(
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(4, "Package", "2010-11-12", "pick up the secured backage and use it to secure VPN TunNel.")
        );

        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).getId(), actual.get(i).getId());
            assertEquals(expected.get(i).getTitle(), actual.get(i).getTitle());
            assertEquals(expected.get(i).getDate(), actual.get(i).getDate());
            assertEquals(expected.get(i).getBrief(), actual.get(i).getBrief());
        }
    }

    @Test
    public void searchFoundScenario7() {
        //setting up mock test data
        List<Mission> missions = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish 12 secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area 12 that is secure"),
                new Mission(4, "Package", "2010-11-12", "pick up 12 secured backages")
        );
        when(mockDataStore.listMissions()).thenReturn(missions);


        //begin scenario
        List<Mission> actual = searchObj.search("12");
        List<Mission> expected = List.of(
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish 12 secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area 12 that is secure"),
                new Mission(4, "Package", "2010-11-12", "pick up 12 secured backages")
        );

        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).getId(), actual.get(i).getId());
            assertEquals(expected.get(i).getTitle(), actual.get(i).getTitle());
            assertEquals(expected.get(i).getDate(), actual.get(i).getDate());
            assertEquals(expected.get(i).getBrief(), actual.get(i).getBrief());
        }

    }

    @Test
    public void searchNotFoundScenario1() {
        //TODO assertEquals with mockObj of DataStoreInterface, must be empty
        searchObj.search("nonExisetentWord");

        //setting up mock test data
        List<Mission> missions = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area that is secure"),
                new Mission(4, "Package", "2010-11-12", "pick up the secured backage")
        );
        when(mockDataStore.listMissions()).thenReturn(missions);


        //begin scenario
        List<Mission> actual = searchObj.search("nonExisetentWord");

        assertEquals(0, actual.size());

    }

    @Test
    public void searchNotFoundScenario2() {
        //setting up mock test data
        List<Mission> missions = List.of(
                new Mission(1, "Nukes in the loose", "2010-11-12", "Secure the loose tactical nukes."),
                new Mission(2, "VPN Tunnel", "2010-11-12", "Establish a secure VPN tunnel"),
                new Mission(3, "Deployment Area", "2010-11-12", "deploy in the area that is secure"),
                new Mission(4, "Package", "2010-11-12", "pick up the secured backage")
        );
        when(mockDataStore.listMissions()).thenReturn(missions);


        //begin scenario
        List<Mission> actual = searchObj.search("secureThe");

        assertEquals(0, actual.size());
    }


}
