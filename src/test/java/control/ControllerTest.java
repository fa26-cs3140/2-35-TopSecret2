package control;

import java.util.List;
import model.AccessControlInterface;
import model.Agent;
import model.AuditLogInterface;
import model.Brief;
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
    private Controller controller;

    @BeforeEach
    void setUp() {
        users = mock(UserManagerInterface.class);
        access = mock(AccessControlInterface.class);
        store = mock(DataStoreInterface.class);
        audit = mock(AuditLogInterface.class);
        messages = mock(MessageManager.class);
        controller = new Controller(users, access, store, audit, messages);
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

    @Test
    void showMainMenuShowsAdminOptions() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("1. Browse briefs\n2. Browse agents\n3. Browse facilities\n4. Browse missions\n"
                + "5. Change Password\n6. Create a new user\n7. Promote or Demote users\n8. Review audit log\n"
                + "9. Send or view messages\n10. Assign agent to mission\n11. Exit/logout\nEnter an option number:", controller.showMainMenu("boss"));
    }

    @Test
    void showMainMenuHidesAdminOptions() {
        assertEquals("1. Browse briefs\n2. Browse agents\n3. Browse facilities\n4. Browse missions\n"
                + "5. Change Password\n9. Send or view messages\n11. Exit/logout\nEnter an option number:", controller.showMainMenu("alice"));
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
        when(store.listFacilities()).thenReturn(List.of(new Facility(1, "HQ", "H")));
        assertEquals("1 : HQ", controller.listEntries("alice", "facilities"));
    }

    @Test
    void listEntriesListsMissions() {
        when(store.listMissions()).thenReturn(List.of(new Mission(1, "Op", 2)));
        assertEquals("1 : Op", controller.listEntries("alice", "missions"));
    }

    @Test
    void listEntriesRejectsBadType() {
        assertEquals("Invalid option", controller.listEntries("alice", "bogus"));
    }

    @Test
    void readEntryPrintsBriefAndAudits() {
        when(store.getBrief(1)).thenReturn(new Brief(1, 1, "Title", "2026-01-01", "Content"));
        when(users.getUserByUsername("alice")).thenReturn(new User(1, "alice"));
        assertEquals("Title: Title\nDate: 2026-01-01\nContent: Content",
                controller.readEntry("alice", "briefs", 1));
        ArgumentCaptor<LoggedAction> captor = ArgumentCaptor.forClass(LoggedAction.class);
        verify(audit).log(captor.capture());
        assertEquals(LoggedAction.READ, captor.getValue().getAction());
        assertEquals("briefs 1", captor.getValue().getRecord());
        assertEquals("alice", captor.getValue().getUser().getUsername());
    }

    @Test
    void readEntryPrintsAgent() {
        when(store.getAgent(1)).thenReturn(new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes"));
        assertEquals("Name: Ghost\nDoB: 1990-01-01\nDoDeath: 2000-01-01\nNotes: Notes",
                controller.readEntry("alice", "agents", 1));
    }

    @Test
    void readEntryPrintsFacility() {
        when(store.getFacility(1)).thenReturn(new Facility(1, "HQ", "H"));
        assertEquals("Name: HQ\nAbbreviation: H", controller.readEntry("alice", "facilities", 1));
    }

    @Test
    void readEntryPrintsMission() {
        when(store.getMission(1)).thenReturn(new Mission(1, "Op", 2));
        assertEquals("Title: Op\nFacility ID: 2", controller.readEntry("alice", "missions", 1));
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
        when(store.listMissions()).thenReturn(List.of(new Mission(3, "Op", 2)));
        assertEquals("3 : Op", controller.addEntry("boss", "missions", List.of("Op", "2")));
        ArgumentCaptor<Mission> captor = ArgumentCaptor.forClass(Mission.class);
        verify(store).addMission(captor.capture());
        assertEquals("Op", captor.getValue().getTitle());
        assertEquals(2, captor.getValue().getFacilityID());
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
        assertEquals("Invalid option", controller.addEntry("alice", "bogus", List.of()));
    }

    @Test
    void addEntryReturnsBadNumberMessage() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertEquals("For input string: \"abc\"", controller.addEntry("boss", "briefs",
                List.of("abc", "Title", "2026-01-01", "Content")));
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
        when(store.getAgent(1)).thenReturn(new Agent(1, 2, "Ghost", "1990-01-01", "2000-01-01", "Notes"));
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
        assertEquals("Message sent to Ghost", controller.sendMessage("alice", "ghost", "Hello"));
        ArgumentCaptor<Agent> agentCaptor = ArgumentCaptor.forClass(Agent.class);
        verify(messages).createMessage(agentCaptor.capture(), any(String.class));
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
                new Message(ghost, "Hello"), new Message(ghost, "World")));
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
}
