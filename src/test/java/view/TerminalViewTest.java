package view;

import model.Agent;
import model.Brief;
import model.Facility;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TerminalViewTest {
    @Test
    public void mainMenuViewTest() {
        TerminalView view = new TerminalView(true);

        String actual = view.mainMenuView();
        String expected = """
                ----------Main Menu----------
                1. Browse briefs
                2. Browse agents
                3. Browse facilities
                4. Change Password
                5. Create a new user
                6. Promote or Demote users
                7. Review audit log
                8. Exit/logout
                Enter an option number:
                
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void mainMenuViewTest2() {
        TerminalView view = new TerminalView(false);

        String actual = view.mainMenuView();
        String expected = """
                ----------Main Menu----------
                1. Browse briefs
                2. Browse agents
                3. Browse facilities
                4. Change Password
                5. Exit/logout
                Enter an option number:
                
                """;
        assertEquals(expected, actual);
    }


    @Test
    public void browseBriefsViewTest1() {
        TerminalView view = new TerminalView(true);
        String actual = view.browseBriefsView();
        String expected = """
                ----------Browse briefs----------
                1. List Briefs
                2. Read Brief
                3. Add Brief
                4. Go back to main menu
                Enter an option number:
                
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void browseBriefsViewTest2() {
        TerminalView view = new TerminalView(false);
        String actual = view.browseBriefsView();
        String expected = """
                ----------Browse briefs----------
                1. List Briefs
                2. Read Brief
                3. Go back to main menu
                Enter an option number:
                
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void ListBriefsViewTest1() {
        TerminalView view = new TerminalView(true);
        List<Brief> briefs = List.of(
                new Brief(1, 2, "brief1", "1991-02-21", "content1"),
                new Brief(2, 3, "brief2", "1991-03-21", "content2")
        );

        String actual = view.listBriefsView(briefs);

        String expected = """
                Brief ID: 1
                Mission ID: 2
                Title: brief1
                Date: 1991-02-21
                Content: content1
                
                Brief ID: 2
                Mission ID: 3
                Title: brief2
                Date: 1991-03-21
                Content: content2
                
                
                """;

        assertEquals(expected, actual);
    }

    @Test
    public void singleBriefViewTest1() {
        TerminalView view = new TerminalView(true);
        Brief b = new Brief(1, 2, "brief1", "1991-02-21", "content1");

        String actual = view.singleBriefView(b);

        String expected = """
            Brief ID: 1
            Mission ID: 2
            Title: brief1
            Date: 1991-02-21
            Content: content1
            
            """;

        assertEquals(expected, actual);
    }

    //TODO add brief optoin

    @Test
    public void browseAgentsViewTest1() {
        TerminalView view = new TerminalView(true);
        String actual = view.browseAgentsView();

        String expected = """
            ----------Browse agents----------
            1. List Agents
            2. Read Agent
            3. Add Agent
            4. Go back to main menu
            Enter an option number:

            """;

        assertEquals(expected, actual);
    }

    @Test
    public void browseAgentsViewTest2() {
        TerminalView view = new TerminalView(false);
        String actual = view.browseAgentsView();

        String expected = """
            ----------Browse agents----------
            1. List Agents
            2. Read Agent
            3. Go back to main menu
            Enter an option number:

            """;

        assertEquals(expected, actual);
    }

    @Test
    public void ListAgentsViewTest1() {
        TerminalView view = new TerminalView(true);
        List<Agent> agents = List.of(
                new Agent(1, 2, 3, "agent1", "1990-02-01", "2000-02-01", "note1"),
                new Agent(2, 3, 4, "agent2", "1991-03-01", null, null)
        );

        String actual = view.listAgentsView(agents);

        String expected = """
            Agent ID: 1
            Mission ID: 2
            Facility ID: 3
            Name: agent1
            Date Of Birth: 1990-02-01
            Date Of Death: 2000-02-01
            Notes: note1

            Agent ID: 2
            Mission ID: 3
            Facility ID: 4
            Name: agent2
            Date Of Birth: 1991-03-01
            Date Of Death:
            Notes:


            """;

        assertEquals(expected, actual);
    }

    @Test
    public void singleAgentViewTest1() {
        TerminalView view = new TerminalView(true);
        Agent a = new Agent(1, 2, 3, "agent1", "1990-02-01", "2000-02-01", "note1");

        String actual = view.singleAgentView(a);

        String expected = """
            Agent ID: 1
            Mission ID: 2
            Facility ID: 3
            Name: agent1
            Date Of Birth: 1990-02-01
            Date Of Death: 2000-02-01
            Notes: note1
            
            """;

        assertEquals(expected, actual);
    }

    @Test
    public void browseFacilitiesViewTest1() {
        TerminalView view = new TerminalView(true);
        String actual = view.browseFacilitiesView();

        String expected = """
            ----------Browse facilities----------
            1. List Facilities
            2. Read Facility
            3. Add Facility
            4. Go back to main menu
            Enter an option number:

            """;

        assertEquals(expected, actual);
    }

    @Test
    public void browseFacilitiesViewTest2() {
        TerminalView view = new TerminalView(false);
        String actual = view.browseFacilitiesView();

        String expected = """
            ----------Browse facilities----------
            1. List Facilities
            2. Read Facility
            3. Go back to main menu
            Enter an option number:

            """;

        assertEquals(expected, actual);
    }

    @Test
    public void ListFacilitiesViewTest1() {
        TerminalView view = new TerminalView(true);
        List<Facility> facilities = List.of(
                new Facility(1, "Facility1", "F1"),
                new Facility(2, "Facility2", "F2")
        );

        String actual = view.listFacilitiesView(facilities);

        String expected = """
            Facility ID: 1
            Name: Facility1
            Abbreviation: F1

            Facility ID: 2
            Name: Facility2
            Abbreviation: F2


            """;

        assertEquals(expected, actual);
    }

    @Test
    public void singleFacilityViewTest1() {
        TerminalView view = new TerminalView(true);
        Facility f = new Facility(1, "Facility1", "F1");

        String actual = view.singleFacilityView(f);

        String expected = """
            Facility ID: 1
            Name: Facility1
            Abbreviation: F1
            
            """;

        assertEquals(expected, actual);
    }







}
