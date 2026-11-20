package control;

import java.time.Instant;
import java.util.List;
import model.AccessControlInterface;
import model.Agent;
import model.AuditLogInterface;
import model.Brief;
import model.Country;
import model.DataStoreInterface;
import model.Facility;
import model.LoggedAction;
import model.Message;
import model.Mission;
import model.TopSecretException;
import model.User;
import model.UserManagerInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

// Week-2 unit tests for the Controller. Collaborators are
// Mockito mocks. The four delegations are verified against the
// mocks; every other method is tested on its real returned
// text, reusing the HW5 UserInterface wording and inputs.
// Run: .\gradlew test --tests "control.ControllerTest" --console=plain
public class ControllerTest {
    private UserManagerInterface users;
    private AccessControlInterface access;
    private DataStoreInterface store;
    private AuditLogInterface audit;
    private MessageManager messages;
    private SessionManager sessions;
    private Controller controller;

    @BeforeEach
    void setUp() {
        users = mock(UserManagerInterface.class);
        access = mock(AccessControlInterface.class);
        store = mock(DataStoreInterface.class);
        audit = mock(AuditLogInterface.class);
        messages = mock(MessageManager.class);
        sessions = mock(SessionManager.class);
        controller = new Controller(users, access, store, audit, messages, sessions);
    }

    @Test
    void hasUsersDelegatesToUsers() {
        when(users.hasUser()).thenReturn(true);
        assertTrue(controller.hasUsers());
        when(users.hasUser()).thenReturn(false);
        assertFalse(controller.hasUsers());
    }

    @Test
    void verifyLoginDelegatesToUsers() {
        when(users.verifyLogin("alice", "Pass123")).thenReturn(true);
        assertTrue(controller.verifyLogin("alice", "Pass123"));
        verify(users).verifyLogin("alice", "Pass123");
    }

    @Test
    void needsPasswordSetupDelegatesToUsers() {
        when(users.needsPasswordSetup("alice")).thenReturn(true);
        assertTrue(controller.needsPasswordSetup("alice"));
        verify(users).needsPasswordSetup("alice");
    }

    @Test
    void isAdminDelegatesToAccess() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertTrue(controller.isAdmin("boss"));
        verify(access).isAdmin("boss");
    }

    @Test
    void createFirstAccountSucceeds() throws TopSecretException{
        assertEquals("Account is created!", controller.createFirstAccount("alice", "Pass1", "Pass1"));
        verify(users).createUser("alice");
        verify(users).changePassword("alice", "Pass1", "Pass1");
    }

    @Test
    void createFirstAccountReturnsExceptionMessage() throws Exception {
        when(users.createUser("x")).thenThrow(new TopSecretException("bad name"));
        assertEquals("bad name", controller.createFirstAccount("x", "Pass1", "Pass1"));
    }

    @Test
    void changePasswordSucceeds() throws TopSecretException{
        assertEquals("Password changed", controller.changePassword("alice", "Pass1", "Pass1"));
        verify(users).changePassword("alice", "Pass1", "Pass1");
    }

    @Test
    void changePasswordReturnsExceptionMessage() throws TopSecretException{
        doThrow(new TopSecretException("weak")).when(users).changePassword("alice", "Pass1", "Pass1");
        assertEquals("weak", controller.changePassword("alice", "Pass1", "Pass1"));
    }

    @Test
    void createUserSucceedsForAdmin() throws TopSecretException{
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("", controller.createUser("boss", "alice"));
        verify(users).createUser("alice");
    }

    @Test
    void createUserRefusesNonAdmin() {
        assertEquals("Not allowed", controller.createUser("alice", "bob"));
        verifyNoInteractions(users);
    }

    @Test
    void createUserReturnsExceptionMessage() throws Exception {
        when(access.isAdmin("boss")).thenReturn(true);
        when(users.createUser("alice")).thenThrow(new TopSecretException("taken"));
        assertEquals("taken", controller.createUser("boss", "alice"));
    }

    @Test
    void manageAccessPromotesUser() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(access.promoteUser("boss", "alice")).thenReturn(true);
        assertEquals("alice was promoted", controller.manageAccess("boss", "alice", "promote"));
        verify(access).promoteUser("boss", "alice");
    }

    @Test
    void manageAccessDemotesUser() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(access.demoteUser("boss", "alice")).thenReturn(true);
        assertEquals("alice was demoted", controller.manageAccess("boss", "alice", "demote"));
    }

    @Test
    void manageAccessFalseResultIsNotAllowed() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(access.promoteUser("boss", "alice")).thenReturn(false);
        assertEquals("Not allowed", controller.manageAccess("boss", "alice", "promote"));
    }

    @Test
    void manageAccessRefusesNonAdmin() {
        assertEquals("Not allowed", controller.manageAccess("alice", "bob", "promote"));
    }

    @Test
    void manageAccessRejectsBadAction() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("Invalid option", controller.manageAccess("boss", "alice", "fire"));
    }

    @Test
    void listUsernamesExcludesCaller() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(users.listUsernames()).thenReturn(List.of("boss", "alice", "carol"));
        assertEquals("alice\ncarol", controller.listUsernames("boss"));
    }

    @Test
    void listUsernamesRefusesNonAdmin() {
        assertEquals("Not allowed", controller.listUsernames("alice"));
        verifyNoInteractions(users);
    }

//    @Test
//    void showMainMenuShowsAdminOptions() {
//        when(access.isAdmin("boss")).thenReturn(true);
//        assertEquals("1. Browse briefs\n2. Browse agents\n3. Browse facilities\n4. Browse missions\n"
//                + "5. Browse countries\n6. Change Password\n7. Create a new user\n8. Promote or Demote users\n9. Review audit log\n"
//                + "10. Send or view messages\n11. Assign agent to mission\n12. Exit/logout\nEnter an option number:", controller.showMainMenu("boss"));
//    }

    @Test
    void showMainMenuHidesAdminOptions() {
        assertEquals("1. Browse briefs\n2. Browse agents\n3. Browse facilities\n4. Browse missions\n"
                + "5. Browse countries\n6. Change Password\n10. Send or view messages\n12. Exit/logout\nEnter an option number:", controller.showMainMenu("alice"));
    }

    @Test
    void browseMainMenuShowsAdminOptions() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("1. List briefs\n2. Read briefs\n3. Add briefs\n4. Edit briefs\n"
                + "5. Go back to main menu\nEnter an option number:", controller.browseMainMenu("boss", "briefs"));
    }

    @Test
    void browseMainMenuHidesAdminOptions() {
        assertEquals("1. List briefs\n2. Read briefs\n5. Go back to main menu\nEnter an option number:",
                controller.browseMainMenu("alice", "briefs"));
    }

    @Test
    void browseMainMenuRejectsBadType() {
        assertEquals("Invalid option", controller.browseMainMenu("boss", "bogus"));
    }

    @Test
    void browseMainMenuAcceptsCountries() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("1. List countries\n2. Read countries\n3. Add countries\n4. Edit countries\n"
                + "5. Go back to main menu\nEnter an option number:", controller.browseMainMenu("boss", "countries"));
    }

    @Test
    void listEntriesListsBriefs() {
        when(store.listBriefs()).thenReturn(List.of(
                new Brief(1, 1, "Title", "2026-01-01", "Content"),
                new Brief(2, 1, "Other", "2026-01-02", "More")));
        assertEquals("1 : Title\n2 : Other", controller.listEntries("alice", "briefs"));
    }

    @Test
    void listEntriesListsAgents() {
        when(store.listAgents()).thenReturn(List.of(
                new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes")));
        assertEquals("1 : Ghost", controller.listEntries("alice", "agents"));
    }

    @Test
    void listEntriesListsFacilities() {
        when(store.listFacilities()).thenReturn(List.of(new Facility(1, "HQ", "H", 1)));
        assertEquals("1 : HQ", controller.listEntries("alice", "facilities"));
    }

    @Test
    void listEntriesListsMissions() {
        when(store.listMissions()).thenReturn(List.of(new Mission(1, "Op", 2)));
        assertEquals("1 : Op", controller.listEntries("alice", "missions"));
    }

    @Test
    void listEntriesListsCountries() {
        when(store.listCountries()).thenReturn(List.of(new Country(1, "France")));
        assertEquals("1 : France", controller.listEntries("alice", "countries"));
    }

    @Test
    void listEntriesRejectsBadType() {
        assertEquals("Invalid option", controller.listEntries("alice", "bogus"));
    }


    @Test
    void readEntryPrintsFacilityWithNoCountry() {
        when(store.getFacility(1)).thenReturn(new Facility(1, "HQ", "H", 0));
        assertTrue(controller.readEntry("alice", "facilities", 1).startsWith("Name: HQ\nAbbreviation: H\nCountry: None"));
    }

    @Test
    void readEntryPrintsMission() {
        when(store.getMission(1)).thenReturn(new Mission(1, "Op", 2));
        assertTrue(controller.readEntry("alice", "missions", 1).startsWith("Title: Op"));
    }

    @Test
    void readEntryPrintsCountry() {
        when(store.getCountry(1)).thenReturn(new Country(1, "France"));
        assertTrue(controller.readEntry("alice", "countries", 1).startsWith("Name: France"));
    }

    @Test
    void readEntryHandlesUnknownId() {
        assertEquals("No brief found with this id", controller.readEntry("alice", "briefs", 99));
    }

    @Test
    void readEntryRejectsBadType() {
        assertEquals("Invalid option", controller.readEntry("alice", "bogus", 1));
    }

    @Test
    void addEntryAddsBriefAuditsAndRelists() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.addBrief(any(Brief.class))).thenReturn(new Brief(7, 1, "Title", "2026-01-01", "Content"));
        when(store.listBriefs()).thenReturn(List.of(new Brief(7, 1, "Title", "2026-01-01", "Content")));
        when(users.getUserByUsername("boss")).thenReturn(new User(1, "boss"));
        assertEquals("7 : Title", controller.addEntry("boss", "briefs",
                List.of("1", "Title", "2026-01-01", "Content")));
        ArgumentCaptor<Brief> briefCaptor = ArgumentCaptor.forClass(Brief.class);
        verify(store).addBrief(briefCaptor.capture());
        assertEquals(1, briefCaptor.getValue().getMissionID());
        assertEquals("Title", briefCaptor.getValue().getTitle());
        ArgumentCaptor<LoggedAction> logCaptor = ArgumentCaptor.forClass(LoggedAction.class);
        verify(audit).log(logCaptor.capture());
        assertEquals(LoggedAction.CREATE, logCaptor.getValue().getAction());
        assertEquals("briefs 7", logCaptor.getValue().getRecord());
    }

    @Test
    void addEntryAddsAgentWithoutMission() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.addAgent(any(Agent.class))).thenReturn(
                new Agent(9, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes"));
        when(store.listAgents()).thenReturn(List.of(
                new Agent(9, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes")));
        assertEquals("9 : Ghost", controller.addEntry("boss", "agents",
                List.of("2", "Ghost", "1990-01-01", "2000-01-01", "Notes")));
        ArgumentCaptor<Agent> captor = ArgumentCaptor.forClass(Agent.class);
        verify(store).addAgent(captor.capture());
        assertEquals(2, captor.getValue().getFacilityID());
        assertEquals("Ghost", captor.getValue().getName());
    }

    @Test
    void addEntryAddsMission() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.addMission(any(Mission.class))).thenReturn(new Mission(3, "Op", 2));
        when(store.listMissions()).thenReturn(List.of(new Mission(3, "Op", 2)));
        assertEquals("3 : Op", controller.addEntry("boss", "missions", List.of("Op", "2")));
        ArgumentCaptor<Mission> captor = ArgumentCaptor.forClass(Mission.class);
        verify(store).addMission(captor.capture());
        assertEquals("Op", captor.getValue().getTitle());
        assertEquals(2, captor.getValue().getFacilityID());
    }

    @Test
    void addEntryAddsFacility() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.addFacility(any(Facility.class))).thenReturn(new Facility(1, "HQ", "H", 1));
        when(store.listFacilities()).thenReturn(List.of(new Facility(1, "HQ", "H", 1)));
        assertEquals("1 : HQ", controller.addEntry("boss", "facilities", List.of("HQ", "H", "1")));
        ArgumentCaptor<Facility> captor = ArgumentCaptor.forClass(Facility.class);
        verify(store).addFacility(captor.capture());
        assertEquals("HQ", captor.getValue().getName());
        assertEquals("H", captor.getValue().getAbbreviation());
        assertEquals(1, captor.getValue().getCountryID());
    }

    @Test
    void addEntryAddsFacilityWithCountry() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.addFacility(any(Facility.class))).thenReturn(new Facility(1, "HQ", "H", 1));
        when(store.listFacilities()).thenReturn(List.of(new Facility(1, "HQ", "H", 1)));
        assertEquals("1 : HQ", controller.addEntry("boss", "facilities", List.of("HQ", "H", "1")));
        ArgumentCaptor<Facility> captor = ArgumentCaptor.forClass(Facility.class);
        verify(store).addFacility(captor.capture());
        assertEquals("HQ", captor.getValue().getName());
        assertEquals(1, captor.getValue().getCountryID());
    }

    @Test
    void addEntryAddsCountry() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.addCountry(any(Country.class))).thenReturn(new Country(1, "France"));
        when(store.listCountries()).thenReturn(List.of(new Country(1, "France")));
        assertEquals("1 : France", controller.addEntry("boss", "countries", List.of("France")));
        ArgumentCaptor<Country> captor = ArgumentCaptor.forClass(Country.class);
        verify(store).addCountry(captor.capture());
        assertEquals("France", captor.getValue().getName());
    }

    @Test
    void addEntryRefusesNonAdmin() {
        assertEquals("Not allowed", controller.addEntry("alice", "briefs",
                List.of("1", "Title", "2026-01-01", "Content")));
        verifyNoInteractions(store);
    }

    @Test
    void addEntryRejectsBadType() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("Invalid option", controller.addEntry("boss", "bogus", List.of()));
    }

    @Test
    void addEntryReturnsBadNumberMessage() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("For input string: \"abc\"", controller.addEntry("boss", "briefs",
                List.of("abc", "Title", "2026-01-01", "Content")));
    }

    @Test
    void addEntryShortListIsNotEnoughFields() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("Not enough fields", controller.addEntry("boss", "briefs", List.of("1", "Title")));
        verifyNoInteractions(store);
    }

    @Test
    void addEntryReturnsStoreMessage() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.addBrief(any(Brief.class))).thenThrow(new IllegalArgumentException("No mission 99"));
        assertEquals("No mission 99", controller.addEntry("boss", "briefs",
                List.of("99", "Title", "2026-01-01", "Content")));
    }

    @Test
    void editEntryEditsBriefAndRelists() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.getBrief(1)).thenReturn(new Brief(1, 1, "Old", "2026-01-01", "Content"));
        when(store.listBriefs()).thenReturn(List.of(new Brief(1, 1, "New", "2026-02-01", "Updated")));
        assertEquals("1 : New", controller.editEntry("boss", "briefs", 1,
                List.of("1", "New", "2026-02-01", "Updated")));
        ArgumentCaptor<Brief> captor = ArgumentCaptor.forClass(Brief.class);
        verify(store).updateBrief(captor.capture());
        assertEquals(1, captor.getValue().getBriefID());
        assertEquals(1, captor.getValue().getMissionID());
        assertEquals("New", captor.getValue().getTitle());
    }

    @Test
    void editEntryKeepsAgentMission() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.getAgentByID(1)).thenReturn(new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes"));
        when(store.listAgents()).thenReturn(List.of(
                new Agent(1, 3, "Ghost2", "1990-01-01", "2000-01-01", "Notes2")));
        assertEquals("1 : Ghost2", controller.editEntry("boss", "agents", 1,
                List.of("3", "Ghost2", "1990-01-01", "2000-01-01", "Notes2")));
        ArgumentCaptor<Agent> captor = ArgumentCaptor.forClass(Agent.class);
        verify(store).updateAgent(captor.capture());
        assertEquals(1, captor.getValue().getAgentID());
        assertEquals(3, captor.getValue().getFacilityID());
    }

    @Test
    void editEntryRefusesNonAdmin() {
        assertEquals("Not allowed", controller.editEntry("alice", "briefs", 1,
                List.of("1", "Title", "2026-01-01", "Content")));
        verifyNoInteractions(store);
    }

    @Test
    void editEntryHandlesUnknownId() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("No brief found with this id", controller.editEntry("boss", "briefs", 99,
                List.of("1", "Title", "2026-01-01", "Content")));
    }

    @Test
    void editEntryRejectsBadType() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("Invalid option", controller.editEntry("boss", "bogus", 1, List.of()));
    }

    @Test
    void editEntryShortListIsNotEnoughFields() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("Not enough fields", controller.editEntry("boss", "briefs", 1, List.of("1", "Title")));
        verifyNoInteractions(store);
    }

    @Test
    void editEntryEditsFacility() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.getFacility(1)).thenReturn(new Facility(1, "Old", "O", 1));
        when(store.listFacilities()).thenReturn(List.of(new Facility(1, "New", "N", 1)));
        assertEquals("1 : New", controller.editEntry("boss", "facilities", 1, List.of("New", "N", "1")));
        ArgumentCaptor<Facility> captor = ArgumentCaptor.forClass(Facility.class);
        verify(store).updateFacility(captor.capture());
        assertEquals(1, captor.getValue().getFacilityID());
        assertEquals("New", captor.getValue().getName());
        assertEquals(1, captor.getValue().getCountryID());
    }

    @Test
    void editEntryEditsCountry() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.getCountry(1)).thenReturn(new Country(1, "Old"));
        when(store.listCountries()).thenReturn(List.of(new Country(1, "France")));
        assertEquals("1 : France", controller.editEntry("boss", "countries", 1, List.of("France")));
        ArgumentCaptor<Country> captor = ArgumentCaptor.forClass(Country.class);
        verify(store).updateCountry(captor.capture());
        assertEquals(1, captor.getValue().getCountryID());
        assertEquals("France", captor.getValue().getName());
    }

    @Test
    void editEntryEditsBriefAuditsEdit() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.getBrief(1)).thenReturn(new Brief(1, 1, "Old", "2026-01-01", "Content"));
        when(users.getUserByUsername("boss")).thenReturn(new User(1, "boss"));
        controller.editEntry("boss", "briefs", 1, List.of("1", "New", "2026-02-01", "Updated"));
        ArgumentCaptor<LoggedAction> captor = ArgumentCaptor.forClass(LoggedAction.class);
        verify(audit).log(captor.capture());
        assertEquals(LoggedAction.EDIT, captor.getValue().getAction());
        assertEquals("briefs 1", captor.getValue().getRecord());
        assertEquals("boss", captor.getValue().getUser().getUsername());
    }

    @Test
    void editEntryReturnsStoreMessage() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.getBrief(1)).thenReturn(new Brief(1, 1, "Old", "2026-01-01", "Content"));
        when(store.updateBrief(any(Brief.class))).thenThrow(new IllegalArgumentException("No brief 1"));
        assertEquals("No brief 1", controller.editEntry("boss", "briefs", 1,
                List.of("1", "Title", "2026-01-01", "Content")));
    }

    @Test
    void reviewAuditLogFormatsEntries() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(audit.getLog()).thenReturn(List.of(
                new LoggedAction(LoggedAction.READ, new User(1, "alice"), "2026-10-29", "briefs 1"),
                new LoggedAction(LoggedAction.CREATE, new User(1, "boss"), "2026-10-30", "agents 2")));
        assertEquals("2026-10-29 : alice : read : briefs 1\n2026-10-30 : boss : create : agents 2",
                controller.reviewAuditLog("boss"));
    }

    @Test
    void reviewAuditLogRefusesNonAdmin() {
        assertEquals("Not allowed", controller.reviewAuditLog("alice"));
    }

    @Test
    void reviewAuditLogEmptyIsEmpty() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("", controller.reviewAuditLog("boss"));
    }

    @Test
    void assignAgentToMissionSucceeds() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("Agent 1 assigned to mission 2", controller.assignAgentToMission("boss", 1, 2));
        verify(store).assignAgentToMission(1, 2);
    }

    @Test
    void assignAgentToMissionRefusesNonAdmin() {
        assertEquals("Not allowed", controller.assignAgentToMission("alice", 1, 2));
    }

    @Test
    void assignAgentToMissionReturnsStoreMessage() {
        when(access.isAdmin("boss")).thenReturn(true);
        doThrow(new UnsupportedOperationException("Not implemented yet"))
                .when(store).assignAgentToMission(1, 2);
        assertEquals("Not implemented yet", controller.assignAgentToMission("boss", 1, 2));
    }

    @Test
    void sendMessageFindsAgentCaseInsensitively() throws TopSecretException {
        Agent ghost = new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes");
        when(store.listAgents()).thenReturn(List.of(ghost));
        when(users.getUserByUsername("alice")).thenReturn(new User(1, "alice"));
        assertEquals("Message sent to Ghost", controller.sendMessage("alice", "ghost", "Hello"));
        ArgumentCaptor<Agent> agentCaptor = ArgumentCaptor.forClass(Agent.class);
        verify(messages).createMessage(any(User.class), agentCaptor.capture(), any(String.class));
        assertEquals("Ghost", agentCaptor.getValue().getName());
    }

    @Test
    void sendMessageHandlesUnknownAgent() {
        assertEquals("No agent found with this name", controller.sendMessage("alice", "Nobody", "Hello"));
    }

    @Test
    void viewMessagesShowsOwnMessages() throws TopSecretException {
        Agent ghost = new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes");
        when(store.listAgents()).thenReturn(List.of(ghost));
        when(messages.retrieveMessages(any(Agent.class))).thenReturn(List.of(
                new Message(new User(1, "ghost"), ghost, "Hello"),
                new Message(new User(1, "ghost"), ghost, "World")));
        assertEquals("Hello\nWorld", controller.viewMessages("ghost"));
    }

    @Test
    void viewMessagesWithoutAgentIsEmpty() {
        assertEquals("No messages", controller.viewMessages("alice"));
    }

    @Test
    void viewMessagesEmptyListIsEmpty() {
        Agent ghost = new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes");
        when(store.listAgents()).thenReturn(List.of(ghost));
        assertEquals("No messages", controller.viewMessages("ghost"));
    }

    // HW7 tests, week 2: form fields, search, messages,
    // and web sessions, on their real behaviour.
    @Test
    void getFormFieldsReturnsNewBriefFields() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.listMissions()).thenReturn(List.of(new Mission(1, "Op", 2)));
        List<ControllerInterface.FormField> fields = controller.getFormFields("boss", "briefs", 0);
        assertEquals(4, fields.size());
        assertEquals("missionID", fields.get(0).field());
        assertEquals(List.of("1 : Op"), fields.get(0).options());
        assertEquals("", fields.get(0).value());
        assertEquals("number", fields.get(0).type());
        assertEquals(List.of("title", "date", "content"),
                List.of(fields.get(1).field(), fields.get(2).field(), fields.get(3).field()));
    }

    @Test
    void getFormFieldsReturnsEditBriefFields() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.getBrief(1)).thenReturn(new Brief(1, 1, "Title", "2026-01-01", "Content"));
        when(store.listMissions()).thenReturn(List.of(new Mission(1, "Op", 2)));
        List<ControllerInterface.FormField> fields = controller.getFormFields("boss", "briefs", 1);
        assertEquals("1", fields.get(0).value());
        assertEquals("Title", fields.get(1).value());
        assertEquals("2026-01-01", fields.get(2).value());
        assertEquals("Content", fields.get(3).value());
    }

    @Test
    void getFormFieldsReturnsNewAgentFields() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.listFacilities()).thenReturn(List.of(new Facility(2, "HQ", "H", 1)));
        List<ControllerInterface.FormField> fields = controller.getFormFields("boss", "agents", 0);
        assertEquals(5, fields.size());
        assertEquals(List.of("2 : HQ"), fields.get(0).options());
        assertEquals("date_of_birth", fields.get(2).field());
    }

    @Test
    void getFormFieldsReturnsNewFacilityFields() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.listCountries()).thenReturn(List.of(new Country(1, "France")));
        List<ControllerInterface.FormField> fields = controller.getFormFields("boss", "facilities", 0);
        assertEquals(3, fields.size());
        assertEquals("countryID", fields.get(2).field());
        assertEquals(List.of("1 : France"), fields.get(2).options());
        assertEquals("", fields.get(2).value());
    }

    @Test
    void getFormFieldsReturnsNewMissionFields() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.listFacilities()).thenReturn(List.of(new Facility(2, "HQ", "H", 1)));
        List<ControllerInterface.FormField> fields = controller.getFormFields("boss", "missions", 0);
        assertEquals(2, fields.size());
        assertEquals(List.of("2 : HQ"), fields.get(1).options());
    }

    @Test
    void getFormFieldsReturnsNewCountryFields() {
        when(access.isAdmin("boss")).thenReturn(true);
        List<ControllerInterface.FormField> fields = controller.getFormFields("boss", "countries", 0);
        assertEquals(1, fields.size());
        assertEquals("name", fields.get(0).field());
        assertEquals("Country name", fields.get(0).label());
        assertEquals("", fields.get(0).value());
    }

    @Test
    void getFormFieldsReturnsEditCountryFields() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(store.getCountry(1)).thenReturn(new Country(1, "France"));
        List<ControllerInterface.FormField> fields = controller.getFormFields("boss", "countries", 1);
        assertEquals("France", fields.get(0).value());
    }

    @Test
    void getFormFieldsUnknownIdReturnsEmpty() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertTrue(controller.getFormFields("boss", "briefs", 99).isEmpty());
    }

    @Test
    void getFormFieldsRefusesNonAdmin() {
        when(access.isAdmin("alice")).thenReturn(false);
        assertTrue(controller.getFormFields("alice", "briefs", 0).isEmpty());
    }

    @Test
    void getFormFieldsUnknownTypeReturnsEmpty() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertTrue(controller.getFormFields("boss", "nope", 0).isEmpty());
    }

    @Test
    void getFormFieldsNoUserType() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertTrue(controller.getFormFields("boss", "users", 0).isEmpty());
    }

    @Test
    void searchEntriesFindsBriefByTitle() {
        when(store.listBriefs()).thenReturn(List.of(
                new Brief(1, 1, "Title", "2026-01-01", "Content"),
                new Brief(2, 1, "Other", "2026-01-02", "More")));
        assertEquals("briefs 1 : Title", controller.searchEntries("alice", "Title"));
    }

    @Test
    void searchEntriesIsCaseInsensitive() {
        when(store.listAgents()).thenReturn(List.of(
                new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes")));
        assertEquals("agents 1 : Ghost", controller.searchEntries("alice", "ghost"));
    }

    @Test
    void searchEntriesFindsAcrossTypes() {
        when(store.listBriefs()).thenReturn(List.of(new Brief(1, 1, "Ghost story", "2026-01-01", "Content")));
        when(store.listAgents()).thenReturn(List.of(new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes")));
        assertEquals("briefs 1 : Ghost story\nagents 1 : Ghost", controller.searchEntries("alice", "ghost"));
    }

    @Test
    void searchEntriesReturnsNoMatches() {
        assertEquals("No matches", controller.searchEntries("alice", "Ghost"));
    }

    @Test
    void searchEntriesEmptyQueryReturnsNoMatches() {
        assertEquals("No matches", controller.searchEntries("alice", ""));
    }

    @Test
    void listMessagesShowsOwnMessages() throws TopSecretException {
        Agent ghost = new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes");
        when(store.listAgents()).thenReturn(List.of(ghost));
        when(messages.retrieveMessages(any(Agent.class))).thenReturn(List.of(
                new Message(3, new User(1, "boss"), ghost, "Hello", false),
                new Message(4, new User(1, "boss"), ghost, "World", true)));
        assertEquals("3 : Hello (unread)\n4 : World", controller.listMessages("ghost"));
    }

    @Test
    void listMessagesUnknownUserIsEmpty() {
        assertEquals("No messages", controller.listMessages("alice"));
    }

    @Test
    void listMessagesEmptyListIsEmpty() {
        Agent ghost = new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes");
        when(store.listAgents()).thenReturn(List.of(ghost));
        assertEquals("No messages", controller.listMessages("ghost"));
    }

    @Test
    void listMessagesReturnsExceptionMessage() throws TopSecretException {
        Agent ghost = new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes");
        when(store.listAgents()).thenReturn(List.of(ghost));
        when(messages.retrieveMessages(any(Agent.class))).thenThrow(new TopSecretException("store down"));
        assertEquals("store down", controller.listMessages("ghost"));
    }

    @Test
    void readMessageMarksFoundMessageRead() throws TopSecretException {
        Agent ghost = new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes");
        Message hello = new Message(3, new User(1, "boss"), ghost, "Hello", false);
        when(store.listAgents()).thenReturn(List.of(ghost));
        when(messages.retrieveMessages(any(Agent.class))).thenReturn(List.of(hello));
        assertEquals("3 : Hello", controller.readMessage("ghost", 3));
        verify(messages).readMessage(hello);
    }

    @Test
    void readMessageUnknownIdReturnsNotFound() throws TopSecretException{
        Agent ghost = new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes");
        when(store.listAgents()).thenReturn(List.of(ghost));
        when(messages.retrieveMessages(any(Agent.class))).thenReturn(List.of());
        assertEquals("No message found with this id", controller.readMessage("ghost", 3));
    }

    @Test
    void readMessageUnknownUserReturnsNotFound() {
        assertEquals("No message found with this id", controller.readMessage("ghost", 1));
    }

    @Test
    void listWebSessionsListsSessions() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(sessions.listSessions()).thenReturn(List.of(
                new SessionManager.Session("token-1", "alice", Instant.parse("2026-11-13T00:00:00Z"))));
        assertEquals("token-1 : alice : 2026-11-13T00:00:00Z", controller.listWebSessions("boss"));
    }

    @Test
    void listWebSessionsEmptyIsEmpty() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("", controller.listWebSessions("boss"));
    }

    @Test
    void listWebSessionsRefusesNonAdmin() {
        assertEquals("Not allowed", controller.listWebSessions("alice"));
    }

    @Test
    void deleteWebSessionRevokesAndRelists() {
        when(access.isAdmin("boss")).thenReturn(true);
        when(sessions.revokeSession("token-1")).thenReturn(true);
        when(sessions.listSessions()).thenReturn(List.of(
                new SessionManager.Session("token-2", "alice", Instant.parse("2026-11-13T00:00:00Z"))));
        assertEquals("token-2 : alice : 2026-11-13T00:00:00Z", controller.deleteWebSession("boss", "token-1"));
        verify(sessions).revokeSession("token-1");
    }

    @Test
    void deleteWebSessionUnknownTokenReturnsNotFound() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("No session found with this token", controller.deleteWebSession("boss", "token-1"));
    }

    @Test
    void deleteWebSessionRefusesNonAdmin() {
        assertEquals("Not allowed", controller.deleteWebSession("alice", "token-1"));
    }
}
