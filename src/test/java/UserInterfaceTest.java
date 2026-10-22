import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;


//Unit tests for the UserInterface
//Uses mock-ups for UserManagementInterface, AuditLogInterface,
//AccessControlInterface, DataStoreInterface
//Inputs are scripted via System.setIn
public class UserInterfaceTest {

    private UserManagerInterface users;
    private AccessControlInterface access;
    private DataStoreInterface store;
    private AuditLogInterface audit;
    private PrintStream origOut;
    private InputStream origIn;
    private ByteArrayOutputStream outBuf;

    @BeforeEach
    void setUp() {
        users = mock(UserManagerInterface.class);
        access = mock(AccessControlInterface.class);
        store = mock(DataStoreInterface.class);
        audit = mock(AuditLogInterface.class);
        origOut = System.out;
        origIn = System.in;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
    }

    @AfterEach
    void tearDown() {
        System.setOut(origOut);
        System.setIn(origIn);
    }
    //Scripting standard in, building the UI
    private UserInterface ui(String input){
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        return new UserInterface(users, access, store, audit);
    }
    private String out() {
        return outBuf.toString();
    }
    private List<Brief> twoBriefs() {
        return Arrays.asList(
                new Brief(1, 1, "Alpha", "2024-01-01", "Alpha content"),
                new Brief(2, 1, "Beta", "2024-02-02", "Beta content"));
    }
    private List<Mission> twoMissions() {
        return Arrays.asList(
                new Mission(1, "Alpha", 1),
                new Mission(2, "Beta", 1));
    }
    //Stubbing  the login
    private void loginAs(String name){
        when(users.hasUser()).thenReturn(true);
        when(users.verifyLogin(name, "Pass123")).thenReturn(true);
        when(users.getUserByUsername(name)).thenReturn(new User(1, name));

    }
    // run(): login, then exit -> 0
    @Test
    void runExitsCleanlyAfterLogin() {
        loginAs("tester");
        when(access.isAdmin("tester")).thenReturn(false);
        assertEquals(0, ui("tester\nPass123\n9\n").run());
    }

    // run(): bad login -> 1, menu never shows
    @Test
    void runReturnsOneOnBadLogin() {
        when(users.hasUser()).thenReturn(true);
        when(users.verifyLogin("tester", "wrong")).thenReturn(false);
        assertEquals(1, ui("tester\nwrong\n").run());
        assertFalse(out().contains("Browse briefs"));
    }

    // run(): EOF on stdin -> 1, must NOT loop forever (gradlew run bug)
    @Test
    void runReturnsOneOnEof() {
        when(users.hasUser()).thenReturn(true);
        assertEquals(1, ui("").run());
    }

    // loginStep(): no users yet -> create flow
    @Test
    void loginStepCreatesFirstAccount() throws Exception {
        when(users.hasUser()).thenReturn(false);
        when(users.createUser("newuser")).thenReturn(new User(1, "newuser"));
        assertTrue(ui("newuser\nPass123\nPass123\n").loginStep());
        verify(users).createUser("newuser");
        verify(users).changePassword("newuser", "Pass123", "Pass123");
    }

    // loginStep(): createUser throws (bad/duplicate name) -> false
    @Test
    void loginStepHandlesCreateUserException() throws Exception {
        when(users.hasUser()).thenReturn(false);
        when(users.createUser("bad name"))
                .thenThrow(new TopSecretException("Invalid username."));
        assertFalse(ui("bad name\n").loginStep());
        assertTrue(out().contains("Invalid username."));
    }

    // loginStep(): admin-created user sets password at first login
    @Test
    void loginStepSetsInitialPasswordForNewUser() throws Exception {
        when(users.hasUser()).thenReturn(true);
        when(users.needsPasswordSetup("worker")).thenReturn(true);
        when(users.getUserByUsername("worker")).thenReturn(new User(2, "worker"));
        assertTrue(ui("worker\nInitPass1\nInitPass1\n").loginStep());
        verify(users).changePassword("worker", "InitPass1", "InitPass1");
    }

    // loginStep(): good credentials -> true
    @Test
    void loginStepSucceedsOnGoodCredentials() {
        when(users.hasUser()).thenReturn(true);
        when(users.verifyLogin("tester", "Pass123")).thenReturn(true);
        when(users.getUserByUsername("tester")).thenReturn(new User(1, "tester"));
        assertTrue(ui("tester\nPass123\n").loginStep());
    }

    // showMainMenu(): admin sees admin options
    @Test
    void adminSeesAdminOptions() {
        loginAs("tester");
        when(access.isAdmin("tester")).thenReturn(true);
        UserInterface ui = ui("tester\nPass123\n9\n");
        assertTrue(ui.loginStep());
        ui.showMainMenu();
        assertTrue(out().contains("Create a new user"));
        assertTrue(out().contains("Promote"));
        assertTrue(out().contains("audit log"));
    }

    // showMainMenu(): standard user does not see them
    @Test
    void standardUserSeesNoAdminOptions() {
        loginAs("tester");
        when(access.isAdmin("tester")).thenReturn(false);
        UserInterface ui = ui("tester\nPass123\n9\n");
        assertTrue(ui.loginStep());
        ui.showMainMenu();
        assertFalse(out().contains("Create a new user"));
        assertFalse(out().contains("Promote"));
        assertFalse(out().contains("audit log"));
    }

    // listEntries(): asks the store for briefs
    @Test
    void listEntriesAsksStoreForBriefs() {
        ui("").listEntries("briefs");
        verify(store).listBriefs();
    }

    // listEntries(): prints stubbed brief titles
    @Test
    void listEntriesPrintsBriefTitles() {
        when(store.listBriefs()).thenReturn(twoBriefs());
        ui("").listEntries("briefs");
        assertTrue(out().contains("Alpha"));
        assertTrue(out().contains("Beta"));
    }

    // listEntries(): prints every agent
    @Test
    void listEntriesPrintsAllAgents() {
        when(store.listAgents()).thenReturn(
                Arrays.asList(new Agent(1, 1, 1, "Ames", "1990-01-01", "2020-06-15", "notes")));
        ui("").listEntries("agents");
        assertTrue(out().contains("Ames"));
    }

    // readEntry(): prints full brief + logs READ with "briefs <id>"
    @Test
    void readEntryPrintsRecordAndAudits() {
        when(store.getBrief(1))
                .thenReturn(new Brief(1, 1, "Alpha", "2024-01-01", "Alpha content"));
        UserInterface ui = ui("tester\nPass123\n1\n");
        loginAs("tester");
        assertTrue(ui.loginStep());
        ui.readEntry("briefs");
        assertTrue(out().contains("Alpha content"));
        verify(audit).log(argThat(a -> a.getAction() == LoggedAction.READ
                && "briefs 1".equals(a.getRecord())
                && "tester".equals(a.getUser().getUsername())));
    }

    // readEntry(): unknown id -> "No brief found with this id", no audit
    @Test
    void readEntryHandlesUnknownId() {
        when(store.getBrief(99)).thenReturn(null);
        UserInterface ui = ui("tester\nPass123\n99\n");
        loginAs("tester");
        assertTrue(ui.loginStep());
        ui.readEntry("briefs");
        assertTrue(out().contains("No brief found with this id"));
        verify(audit, never()).log(any());
    }

    // addEntry(): saves, audits CREATE with the new id, re-lists
    @Test
    void addEntrySavesAuditsAndRelists() {
        when(store.addBrief(any(Brief.class)))
                .thenReturn(new Brief(7, 1, "Gamma", "2024-03-03", "Gamma text"));
        when(store.listBriefs()).thenReturn(twoBriefs());
        UserInterface ui = ui("tester\nPass123\n1\nGamma\n2024-03-03\nGamma text\n");
        loginAs("tester");
        assertTrue(ui.loginStep());
        ui.addEntry("briefs");
        verify(store).addBrief(argThat(
                b -> "Gamma".equals(b.getTitle()) && b.getMissionID() == 1));
        verify(audit).log(argThat(a -> a.getAction() == LoggedAction.CREATE
                && "briefs 7".equals(a.getRecord())));
        assertTrue(out().contains("Alpha")); // re-listed after add
    }

    // reviewAuditLog(): prints time, user, event, record
    @Test
    void reviewAuditLogPrintsEntries() {
        when(audit.getLog()).thenReturn(Arrays.asList(
                new LoggedAction(LoggedAction.READ, new User(1, "tester"),
                        "2024-01-01T10:00", "briefs 1")));
        ui("").reviewAuditLog();
        assertTrue(out().contains("tester"));
        assertTrue(out().contains("read"));
        assertTrue(out().contains("briefs 1"));
    }

    // changePassword(): prompts twice, delegates to users
    @Test
    void changePasswordDelegatesToUsers() throws Exception{
        UserInterface ui = ui("tester\nPass123\nNewPass1\nNewPass1\n");
        loginAs("tester");
        assertTrue(ui.loginStep());
        ui.changePassword();
        verify(users).changePassword("tester", "NewPass1", "NewPass1");
    }

    // createUser(): username only, delegates
    @Test
    void createUserDelegatesToUsers() throws Exception {
        UserInterface ui = ui("tester\nPass123\nnewguy\n");
        loginAs("tester");
        assertTrue(ui.loginStep());
        ui.createUser();
        verify(users).createUser("newguy");
    }

    // createUser(): TopSecretException from D is printed, not thrown
    @Test
    void createUserPrintsExceptionMessage() throws Exception {
        when(users.createUser("bad name"))
                .thenThrow(new TopSecretException("Invalid username."));
        UserInterface ui = ui("tester\nPass123\nbad name\n");
        loginAs("tester");
        assertTrue(ui.loginStep());
        ui.createUser(); // must not throw
        assertTrue(out().contains("Invalid username."));
    }

    // manageAccess(): promote path delegates to access with current user
    @Test
    void manageAccessPromotesUser() {
        when(users.listUsernames()).thenReturn(Arrays.asList("alice", "bob"));
        when(access.promoteUser("tester", "alice")).thenReturn(true);
        UserInterface ui = ui("tester\nPass123\npromote\nalice\n");
        loginAs("tester");
        assertTrue(ui.loginStep());
        ui.manageAccess();
        verify(access).promoteUser("tester", "alice");
        assertTrue(out().contains("alice was promoted"));
    }

    // manageAccess(): demote path delegates; "Not allowed" on false
    @Test
    void manageAccessDemotesUser() {
        when(users.listUsernames()).thenReturn(Arrays.asList("alice", "bob"));
        when(access.demoteUser("tester", "tester")).thenReturn(false);
        UserInterface ui = ui("tester\nPass123\ndemote\ntester\n");
        loginAs("tester");
        assertTrue(ui.loginStep());
        ui.manageAccess();
        verify(access).demoteUser("tester", "tester");
        assertTrue(out().contains("Not allowed"));
    }

    // searchMissions(): real Search over mocked store finds the match
    @Test
    void searchMissionsFindsMatch() {
        when(store.listMissions()).thenReturn(twoMissions());
        ui("alpha\n").searchMissions();
        assertTrue(out().contains("Alpha"));
    }

    // browseMenu(): option 1 lists, then back
    @Test
    void browseMenuListsThenBack() {
        when(access.isAdmin("tester")).thenReturn(false);
        when(store.listBriefs()).thenReturn(twoBriefs());
        UserInterface ui = ui("tester\nPass123\n1\n4\n");
        loginAs("tester");
        assertTrue(ui.loginStep());
        ui.browseMenu("briefs");
        assertTrue(out().contains("Alpha"));
    }
}
