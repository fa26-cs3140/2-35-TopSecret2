import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;

public class UserInterfaceUserManagerIntegrationTest {
    private UserManagerInterface users;
    private AccessControl access;
    private DataStoreInterface store;
    private AuditLogInterface audit;
    private PrintStream origOut;
    private InputStream origIn;
    private ByteArrayOutputStream outBuf;

    @BeforeEach
    void setUp() {
        users = mock(UserManagerInterface.class);
        access = new AccessControl(new HashSet<>(Set.of("boss")),
                new HashSet<>(Set.of("boss", "worker")));
        store = mock(DataStoreInterface.class);
        audit = mock(AuditLogInterface.class);
        origOut = System.out;
        origIn = System.in;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
        when(users.hasUser()).thenReturn(true);
        when(users.needsPasswordSetup("boss")).thenReturn(false);
        when(users.needsPasswordSetup("worker")).thenReturn(false);
        when(users.verifyLogin("boss", "x")).thenReturn(true);
        when(users.verifyLogin("worker", "x")).thenReturn(true);
        when(users.getUserByUsername("boss")).thenReturn(new User(1, "boss"));
        when(users.getUserByUsername("worker")).thenReturn(new User(2, "worker"));
        when(users.listUsernames()).thenReturn(Arrays.asList("boss", "worker"));
    }
}
