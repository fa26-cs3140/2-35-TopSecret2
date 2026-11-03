package view;

import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TerminalViewTest {
    @TempDir
    Path tempDir;


    // ---------------- INPUT METHODS ----------------

    @Test
    public void getInputIntTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("3\n");

        int actual = view.getInputInt(scnr, 1, 5, "Enter: ");

        assertEquals(3, actual);
    }

    @Test
    public void getInputIntTest2() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("25\n");

        int actual = view.getInputInt(scnr, "Enter: ");

        assertEquals(25, actual);
    }

    @Test
    public void getInputIntTest3() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("8\n3\n");

        int actual = view.getInputInt(scnr, 1, 5, "Enter: ");

        assertEquals(3, actual);
    }

    @Test
    public void getInputStrTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("12345\n");

        String actual = view.getInputStr(scnr, "\\d+", "Enter: ");

        assertEquals("12345", actual);
    }

    @Test
    public void getInputStrTest2() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("username\n");

        String actual = view.getInputStr(scnr, "Enter: ");

        assertEquals("username", actual);
    }

    @Test
    public void getInputStrTest3() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("abc\n123\n");

        String actual = view.getInputStr(scnr, "\\d+", "Enter: ");

        assertEquals("123", actual);
    }

    @Test
    public void promptMessageTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;

        try {
            System.setOut(new PrintStream(output));

            view.promptMessage("Test message");

        } finally {
            System.setOut(original);
        }

        assertEquals("Test message" + System.lineSeparator(),
                output.toString());
    }


    // ---------------- MISSION VIEWS ----------------

    @Test
    public void singleMissionViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Mission mission = new Mission(1, "title", 2);

        String actual = view.singleMissionView(mission);
        String expected = utility.missionToStr(mission);

        assertEquals(expected, actual);
    }

    @Test
    public void listMissionsViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        List<Mission> missions = List.of(
                new Mission(1, "mission1", 2),
                new Mission(2, "mission2", 3)
        );

        String actual = view.listMissionsView(missions);
        String expected = String.join(
                "\n", utility.missionsToStr(missions)
        );

        assertEquals(expected, actual);
    }

    // ---------------- FACILITY VIEWS ----------------

    @Test
    public void ListFacilitiesViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

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
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Facility f = new Facility(1, "Facility1", "F1");

        String actual = view.singleFacilityView(f);

        String expected = """
                Facility ID: 1
                Name: Facility1
                Abbreviation: F1
                
                """;

        assertEquals(expected, actual);
    }


    // ---------------- BRIEF VIEWS ----------------
    @Test
    public void ListBriefsViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

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
        TerminalView view = new TerminalView();
        view.setAdmin(true);

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


    // ---------------- AGENT VIEWS ----------------

    @Test
    public void ListAgentsViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

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
        TerminalView view = new TerminalView();
        view.setAdmin(true);

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


    // ---------------- USER VIEWS ----------------

    @Test
    public void singleUserViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        User user = mock(User.class);

        when(user.getUserID()).thenReturn(1);
        when(user.getUsername()).thenReturn("user1");

        String actual = view.singleUserView(user);
        String expected = utility.userToStr(user);

        assertEquals(expected, actual);
    }

    @Test
    public void listUsersViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(user1.getUserID()).thenReturn(1);
        when(user1.getUsername()).thenReturn("user1");

        when(user2.getUserID()).thenReturn(2);
        when(user2.getUsername()).thenReturn("user2");

        List<User> users = List.of(user1, user2);

        String actual = view.listUsersView(users);
        String expected = String.join(
                "\n", utility.usersToStr(users)
        );

        assertEquals(expected, actual);
    }


    // ---------------- AUDIT LOG VIEWS ----------------

    @Test
    public void singleLogViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        User user = mock(User.class);
        LoggedAction log = mock(LoggedAction.class);

        when(user.getUserID()).thenReturn(1);
        when(user.getUsername()).thenReturn("user1");

        when(log.getUser()).thenReturn(user);
        when(log.getAction()).thenReturn(LoggedAction.READ);
        when(log.getDateTime()).thenReturn("2026-11-03");
        when(log.getRecord()).thenReturn("briefs 1");

        String actual = view.singleLogView(log);
        String expected = utility.logToStr(log);

        assertEquals(expected, actual);
    }

    @Test
    public void listLogsViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        User user = mock(User.class);

        when(user.getUserID()).thenReturn(1);
        when(user.getUsername()).thenReturn("user1");

        LoggedAction log1 = mock(LoggedAction.class);
        LoggedAction log2 = mock(LoggedAction.class);

        for (LoggedAction log : List.of(log1, log2)) {
            when(log.getUser()).thenReturn(user);
            when(log.getAction()).thenReturn(LoggedAction.READ);
        }

        when(log1.getDateTime()).thenReturn("2026-11-03");
        when(log1.getRecord()).thenReturn("briefs 1");

        when(log2.getDateTime()).thenReturn("2026-11-04");
        when(log2.getRecord()).thenReturn("agents 2");

        List<LoggedAction> logs = List.of(log1, log2);

        String actual = view.listLogsView(logs);
        String expected = String.join(
                "\n", utility.logsToStr(logs)
        );

        assertEquals(expected, actual);
    }


    // ---------------- MESSAGE VIEWS ----------------

    // ---------------- MESSAGE VIEWS ----------------

    @Test
    public void listMessagesViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Agent agent = new Agent(
                1, 2, 3, "agent1",
                "1990-02-01", null, null
        );

        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);

        when(message1.getRecipient()).thenReturn(agent);
        when(message2.getRecipient()).thenReturn(agent);

        when(message1.getMessageContent()).thenReturn("Message 1");
        when(message2.getMessageContent()).thenReturn("Message 2");

        List<Message> messages = List.of(message1, message2);

        String actual = view.listMessagesView(messages);
        String expected = String.join(
                "\n", utility.messagesToStr(messages)
        );

        assertEquals(expected, actual);
    }

    // ---------------- MAIN MENU ----------------
    @Test
    public void mainMenuViewTest() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        String actual = view.mainMenuView();
        String expected = """
                ----------Main Menu----------
                
                1. Browse briefs
                2. Browse agents
                3. Browse facilities
                4. Browse missions
                5. Browse messages
                6. Change Password
                7. Create a new user
                8. Promote or Demote users
                9. Review audit log
                10. Exit/logout
                Enter an option number:
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void mainMenuViewTest2() {
        TerminalView view = new TerminalView();
        view.setAdmin(false);

        String actual = view.mainMenuView();
        String expected = """
                ----------Main Menu----------
                
                1. Browse briefs
                2. Browse agents
                3. Browse facilities
                4. Browse missions
                5. Browse messages
                6. Change Password
                7. Exit/logout
                Enter an option number:
                """;
        assertEquals(expected, actual);
    }


    // ---------------- BROWSE MENUS ----------------
    @Test
    public void browseMissionsViewTest() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        String expected = """
                ----------Browse missions----------
                
                1. List missions
                2. Read mission
                3. Add mission
                4. Go back to main menu
                Enter an option number:
                """;

        assertEquals(expected, view.browseMissionsView());
    }

    @Test
    public void browseBriefsViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

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
        TerminalView view = new TerminalView();
        view.setAdmin(false);

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
    public void browseAgentsViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

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
        TerminalView view = new TerminalView();
        view.setAdmin(false);

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
    public void browseFacilitiesViewTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

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
        TerminalView view = new TerminalView();
        view.setAdmin(false);

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
    public void browseMessagesViewTest() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        String expected = """
            ----------Browse messages----------

            1. List your messages
            2. Leave a message to an agent
            Enter an option number:
            """;

        assertEquals(expected, view.browseMessagesView());
    }


    @Test
    public void manageUsersViewTest() {
        String databaseUrl = "jdbc:sqlite:" + tempDir.resolve("test.db");
        SQLiteDataStore store = new SQLiteDataStore(databaseUrl);

        try {
            User currentUser = store.addUser(new User(-1, "currentUser"));
            store.addUser(new User(-1, "username1"));
            store.addUser(new User(-1, "username2"));

            TerminalView view = new TerminalView();
            view.setAdmin(true);
            List<User> users = store.listUsers();

            String expected = """
                    ----------Promote or Demote users----------
                    
                    Users:
                    
                    -username1
                    -username2
                    
                    Enter Promote or Demote followed by username like "Promote username1"
                    
                    """;

            assertEquals(expected, view.manageUsersView(users, currentUser));
        } finally {
            store.close();
        }
    }

    @Test
    public void reviewAuditLogViewTest1() {
        String databaseUrl = "jdbc:sqlite:" + tempDir.resolve("test.db");
        SQLiteDataStore store = new SQLiteDataStore(databaseUrl);

        try {
            User user = store.addUser(new User(-1, "user1"));
            User user2 = store.addUser(new User(-1, "user2"));

            store.addLog(new LoggedAction(1, user, "date1", "record1"));
            store.addLog(new LoggedAction(0, user2, "date2", "record2"));

            TerminalView view = new TerminalView();
            view.setAdmin(true);

            List<LoggedAction> logs = store.listLogs();

            String actual = view.reviewAuditLogView(logs);

            assertNotNull(actual);
            assertTrue(actual.contains("----------Logs----------"));
            assertTrue(actual.contains("date1 : user1 : write : record1"));
            assertTrue(actual.contains("date2 : user2 : read : record2"));
        } finally {
            store.close();
        }
    }

    // ---------------- AUTHENTICATION INPUT ----------------

    @Test
    public void promptLoginTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("user1\npassword1\n");

        String[] actual = view.promptLogin(scnr);

        String[] expected = {"user1", "password1"};

        assertArrayEquals(expected, actual);
    }

    @Test
    public void promptSignupTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner(
                "newuser\npassword1\npassword1\n"
        );

        String[] actual = view.promptSignup(scnr);

        String[] expected = {
                "newuser", "password1", "password1"
        };

        assertArrayEquals(expected, actual);
    }

    @Test
    public void promptChangePasswordTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("newpassword\nnewpassword\n");

        String[] actual = view.promptChangePassword(scnr);

        String[] expected = {
                "newpassword", "newpassword"
        };

        assertArrayEquals(expected, actual);
    }


    // ---------------- RECORD INPUT ----------------

    @Test
    public void promptAddMissionTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("New Mission\n2\n");

        String[] actual = view.promptAddMission(scnr);

        String[] expected = {"New Mission", "2"};

        assertArrayEquals(expected, actual);
    }

    @Test
    public void promptAddFacilityTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);
        Scanner scnr = new Scanner("New Facility\nNF\n");

        String[] actual = view.promptAddFacility(scnr);

        String[] expected = {"New Facility", "NF"};

        assertArrayEquals(expected, actual);
    }

    @Test
    public void promptAddBriefTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner(
                "2\nNew Brief\n2026-11-03\nSecret content\n"
        );

        String[] actual = view.promptAddBrief(scnr);

        String[] expected = {
                "2", "New Brief", "2026-11-03", "Secret content"
        };

        assertArrayEquals(expected, actual);
    }

    @Test
    public void promptAddAgentTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner(
                "2\n3\nAgent Name\n1990-01-01\n\nNotes\n"
        );

        String[] actual = view.promptAddAgent(scnr);

        String[] expected = {
                "2", "3", "Agent Name",
                "1990-01-01", "", "Notes"
        };

        assertArrayEquals(expected, actual);
    }

    @Test
    public void promptRecordIDTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("5\n");

        String actual = view.promptRecordID(scnr, "briefs");

        assertEquals("5", actual);
    }


    // ---------------- USER MANAGEMENT INPUT ----------------

    @Test
    public void promptCreateUserTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("newuser\n");

        String actual = view.promptCreateUser(scnr);

        assertEquals("newuser", actual);
    }

    @Test
    public void promptManageAccessTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("promote\nuser2\n");

        String[] actual = view.promptManageAccess(scnr);

        String[] expected = {"Promote", "user2"};

        assertArrayEquals(expected, actual);
    }

    @Test
    public void promptSelectUserTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("user2\n");

        String actual = view.promptSelectUser(scnr);

        assertEquals("user2", actual);
    }

    // ---------------- MESSAGE INPUT ----------------

    @Test
    public void promptCreateMessageTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("3\nSecret message\n");

        String[] actual = view.promptCreateMessage(scnr);

        String[] expected = {"3", "Secret message"};

        assertArrayEquals(expected, actual);
    }


    // ---------------- GENERAL PROMPTS ----------------

    @Test
    public void promptMainMenuChoiceTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("3\n");

        int actual = view.promptMainMenuChoice(scnr);

        assertEquals(3, actual);
    }

    @Test
    public void promptBrowseMenuChoiceTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("2\n");

        int actual = view.promptBrowseMenuChoice(scnr, "briefs");

        assertEquals(2, actual);
    }

    @Test
    public void promptConfirmationTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("yes\n");

        boolean actual = view.promptConfirmation(
                scnr, "Are you sure?"
        );

        assertTrue(actual);
    }

    @Test
    public void promptConfirmationTest2() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("no\n");

        boolean actual = view.promptConfirmation(
                scnr, "Are you sure?"
        );

        assertFalse(actual);
    }

    @Test
    public void promptOptionTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        Scanner scnr = new Scanner("promote\n");

        String actual = view.promptOption(
                scnr, "Select an option: "
        );

        assertEquals("promote", actual);
    }


    // ---------------- ERROR / STATUS VIEWS ----------------

    @Test
    public void formatErrorTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);


        String actual = view.formatError("Invalid username");

        assertNotNull(actual);
        assertTrue(actual.contains("Invalid username"));
    }

    @Test
    public void formatSuccessTest1() {
        TerminalView view = new TerminalView();
        view.setAdmin(true);

        String actual = view.formatSuccess("Password changed");

        assertNotNull(actual);
        assertTrue(actual.contains("Password changed"));
    }


}
