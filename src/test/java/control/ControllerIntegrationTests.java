package control;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import model.AccessControlInterface;
import model.AuditLogInterface;
import model.MessageInterface;
import model.SQLiteDataStore;
import model.UserManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

// Integration tests: a real SQLiteDataStore on a test database
// and a real UserManager behind the Controller stub. Access,
// audit, and messages stay mocked: this file tests the
// controller-to-store and controller-to-accounts wiring only.
// Two prerequisites, both flagged in the updates list:
// - C's one-word change: SQLiteDataStore(String) must be public.
//   If C refuses, move this file to src/test/java/model instead.
// - ciphers/key.txt must be on the test classpath (proven in HW5).
// credentials.cip is deleted before and after each test, as in
// HW5, so a stray file cannot hijack the first-run flow.
// Run: .\gradlew test --tests "controller.ControllerIntegrationTest" --console=plain
public class ControllerIntegrationTest {
    @TempDir
    Path tempDir;

    private SQLiteDataStore store;
    private UserManager users;
    private Controller controller;

    @BeforeEach
    void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("credentials.cip"));
        String databaseUrl = "jdbc:sqlite:" + tempDir.resolve("test.db");
        store = new model.SQLiteDataStore(databaseUrl);
        users = new UserManager(store);
        controller = new Controller(users, mock(AccessControlInterface.class),
                store, mock(AuditLogInterface.class), mock(MessageInterface.class));
    }

    @AfterEach
    void tearDown() throws Exception {
        store.close();
        Files.deleteIfExists(Paths.get("credentials.cip"));
    }

    @Test
    void hasUsersFalseOnFreshDatabase() {
        assertFalse(controller.hasUsers());
    }

    @Test
    void hasUsersTrueAfterUserCreated() throws Exception {
        users.createUser("alice");
        assertTrue(controller.hasUsers());
    }

    @Test
    void verifyLoginSucceedsWithRealUserManager() throws Exception {
        users.createUser("bob");
        users.changePassword("bob", "Pass1", "Pass1");
        assertTrue(controller.verifyLogin("bob", "Pass1"));
    }

    @Test
    void verifyLoginFailsWithWrongPassword() throws Exception {
        users.createUser("bob");
        users.changePassword("bob", "Pass1", "Pass1");
        assertFalse(controller.verifyLogin("bob", "Wrong1"));
    }
}

