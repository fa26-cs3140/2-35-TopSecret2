package control;

import java.nio.file.Path;
import java.util.List;
import model.AccessControlInterface;
import model.Agent;
import model.AuditLogInterface;
import model.Message;
import model.SQLiteDataStore;
import model.UserManagerInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

// Integration tests: a real MessageManager over a real
// SQLiteDataStore on a test database, behind the full
// Controller. Users, access, and audit stay mocked: this file
// tests the controller-to-messages wiring only, that is,
// sending and viewing through real persisted messages. The
// manager's user side stays mocked; it is stored but never
// used by create or retrieve.
// Run: .\gradlew test --tests "control.ControllerMessageManagerIntegrationTest" --console=plain
public class ControllerMessageManagerIntegrationTest {
    @TempDir
    Path tempDir;

    private SQLiteDataStore store;
    private Agent courier;
    private Controller controller;

    @BeforeEach
    void setUp() throws Exception {
        String databaseUrl = "jdbc:sqlite:" + tempDir.resolve("test.db");
        store = new SQLiteDataStore(databaseUrl);
        courier = store.addAgent(new Agent(1, "Courier", "1990-05-05", null, "Test agent"));
        MessageManager messages = new MessageManager(mock(UserManagerInterface.class), store);
        controller = new Controller(mock(UserManagerInterface.class),
                mock(AccessControlInterface.class), store,
                mock(AuditLogInterface.class), messages);
    }

    @AfterEach
    void tearDown() throws Exception {
        store.close();
    }

    @Test
    void sendMessagePersistsRealMessage() {
        assertEquals("Message sent to Courier",
                controller.sendMessage("boss", "Courier", "Meet at dawn"));
        List<Message> found = store.listMessagesForAgent(courier);
        assertEquals(1, found.size());
        assertEquals("Meet at dawn", found.get(0).getMessageContent());
    }

    @Test
    void viewMessagesReturnsRealMessage() {
        controller.sendMessage("boss", "Courier", "Meet at dawn");
        assertEquals("Meet at dawn", controller.viewMessages("courier"));
    }

    @Test
    void viewMessagesReturnsNoMessagesWhenNone() {
        assertEquals("No messages", controller.viewMessages("courier"));
    }

    @Test
    void viewMessagesReturnsNoMessagesForUnknownUser() {
        assertEquals("No messages", controller.viewMessages("nobody"));
    }

    @Test
    void sendMessageRejectsUnknownAgent() {
        assertEquals("No agent found with this name",
                controller.sendMessage("boss", "Nobody", "Hello"));
        assertTrue(store.listMessagesForAgent(courier).isEmpty());
    }

    @Test
    void sendMessageFindsSeededAgentByName() {
        assertEquals("Message sent to Ghost",
                controller.sendMessage("boss", "ghost", "Seeded hello"));
        assertEquals("Seeded hello", controller.viewMessages("GHOST"));
    }

    @Test
    void viewMessagesJoinsMultipleMessages() {
        controller.sendMessage("boss", "Courier", "First");
        controller.sendMessage("boss", "Courier", "Second");
        assertEquals("First\nSecond", controller.viewMessages("courier"));
    }
}
