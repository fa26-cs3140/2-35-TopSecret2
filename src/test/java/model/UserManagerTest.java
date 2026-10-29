package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Member D (Jaime) -- HW5 unit tests: model.UserManager.
// The store is a Mockito mock backed by a real list, so user records
// behave like the database without touching one. Passwords use the
// real credentials.cip in the working directory (the model.UserManager
// design), deleted before and after each test so nothing leaks.
class UserManagerTest {

    private List<User> backing;
    private UserManager users;

    @BeforeEach
    void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("credentials.cip"));
        backing = new ArrayList<>();
        DataStoreInterface store = mock(DataStoreInterface.class);
        when(store.listUsers()).thenAnswer(inv -> new ArrayList<>(backing));
        when(store.addUser(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            backing.add(u);
            return u;
        });
        when(store.getUser(anyInt())).thenAnswer(inv -> {
            int id = inv.getArgument(0);
            for (User u : backing) {
                if (u.getUserID() == id) {
                    return u;
                }
            }
            return null;
        });
        users = new UserManager(store);
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(Paths.get("credentials.cip"));
    }

    // hasUser(): false on an empty store, true after a create.
    @Test
    void hasUserTracksCreates() throws Exception {
        assertFalse(users.hasUser());
        users.createUser("boss");
        assertTrue(users.hasUser());
    }

    // createUser(): returns the new user; IDs count up from 1.
    @Test
    void createUserReturnsSequentialUsers() throws Exception {
        User first = users.createUser("boss");
        assertEquals(1, first.getUserID());
        assertEquals("boss", first.getUsername());
        User second = users.createUser("worker");
        assertEquals(2, second.getUserID());
    }

    // createUser(): bad usernames throw TopSecretException.
    @Test
    void createUserRejectsInvalidUsernames() {
        assertThrows(TopSecretException.class, () -> users.createUser("ab"));
        assertThrows(TopSecretException.class,
                () -> users.createUser("bad name"));
        assertThrows(TopSecretException.class, () -> users.createUser(null));
    }

    // createUser(): a duplicate username throws TopSecretException.
    @Test
    void createUserRejectsDuplicate() throws Exception {
        users.createUser("boss");
        assertThrows(TopSecretException.class, () -> users.createUser("boss"));
    }

    // Username rules: 3+ chars, letters/numbers/hyphens/underscores only.
    @Test
    void usernameValidationRules() {
        assertTrue(users.isUsernameValid("abc"));
        assertTrue(users.isUsernameValid("a_b-c9"));
        assertTrue(users.isUsernameValid("Boss")); //uppercase is allowed
        assertFalse(users.isUsernameValid("ab"));
        assertFalse(users.isUsernameValid("a b"));
        assertFalse(users.isUsernameValid("a$b"));
        assertFalse(users.isUsernameValid(""));
        assertFalse(users.isUsernameValid(null));
    }

    // Password rules: 5+ chars with an uppercase and a lowercase letter.
    @Test
    void passwordValidationRules() {
        assertTrue(users.isPasswordValid("Pass1"));
        assertFalse(users.isPasswordValid("pass1")); //no uppercase
        assertFalse(users.isPasswordValid("PASS1")); //no lowercase
        assertFalse(users.isPasswordValid("Pas1"));  //too short
        assertFalse(users.isPasswordValid(""));
        assertFalse(users.isPasswordValid(null));
    }

    // getUserByID(): finds the user; a missing ID returns null.
    @Test
    void getUserByIDFindsAndMisses() throws Exception {
        users.createUser("boss");
        assertEquals("boss", users.getUserByID(1).getUsername());
        assertNull(users.getUserByID(99));
    }

    // getUserByUsername(): finds the user; missing or null returns null.
    @Test
    void getUserByUsernameFindsAndMisses() throws Exception {
        users.createUser("boss");
        assertEquals(1, users.getUserByUsername("boss").getUserID());
        assertNull(users.getUserByUsername("ghost"));
        assertNull(users.getUserByUsername(null));
    }

    // listUsernames(): every created username, in creation order.
    @Test
    void listUsernamesReturnsAll() throws Exception {
        assertTrue(users.listUsernames().isEmpty());
        users.createUser("boss");
        users.createUser("worker");
        assertEquals(List.of("boss", "worker"), users.listUsernames());
    }

    // A fresh account needs password setup; after changePassword it doesn't.
    @Test
    void needsPasswordSetupUntilPasswordSet() throws Exception {
        users.createUser("boss");
        assertTrue(users.needsPasswordSetup("boss"));
        users.changePassword("boss", "Pass123", "Pass123");
        assertFalse(users.needsPasswordSetup("boss"));
    }

    // changePassword() + verifyLogin(): the set password logs in,
    // a wrong password and an unknown user don't.
    @Test
    void changePasswordThenVerifyLogin() throws Exception {
        users.createUser("boss");
        users.changePassword("boss", "Pass123", "Pass123");
        assertTrue(users.verifyLogin("boss", "Pass123"));
        assertFalse(users.verifyLogin("boss", "Wrong1"));
        assertFalse(users.verifyLogin("ghost", "Pass123"));
    }

    // Changing the password again replaces the old one.
    @Test
    void changePasswordReplacesOldPassword() throws Exception {
        users.createUser("boss");
        users.changePassword("boss", "Pass123", "Pass123");
        users.changePassword("boss", "NewPass9", "NewPass9");
        assertFalse(users.verifyLogin("boss", "Pass123"));
        assertTrue(users.verifyLogin("boss", "NewPass9"));
    }

    // verifyLogin(): no credentials file at all means no login.
    @Test
    void verifyLoginFalseWithNoCredentialsFile() throws Exception {
        users.createUser("boss");
        assertFalse(users.verifyLogin("boss", "Pass123"));
    }

    // changePassword(): mismatched confirmation throws.
    @Test
    void changePasswordRejectsMismatch() throws Exception {
        users.createUser("boss");
        assertThrows(TopSecretException.class,
                () -> users.changePassword("boss", "Pass123", "Pass124"));
    }

    // changePassword(): a password breaking the rules throws.
    @Test
    void changePasswordRejectsInvalidPassword() throws Exception {
        users.createUser("boss");
        assertThrows(TopSecretException.class,
                () -> users.changePassword("boss", "alllower", "alllower"));
    }

    // changePassword(): an unknown user throws.
    @Test
    void changePasswordRejectsUnknownUser() {
        assertThrows(TopSecretException.class,
                () -> users.changePassword("ghost", "Pass123", "Pass123"));
    }
}
