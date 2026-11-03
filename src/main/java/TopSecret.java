import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import control.Controller;
import control.ControllerInterface;
import control.MessageManager;
import control.WebServer;
import model.AccessControl;
import model.AccessControlInterface;
import model.AuditLog;
import model.AuditLogInterface;
import model.DataStoreInterface;
import model.SQLiteDataStore;
import model.User;
import model.UserManager;
import model.UserManagerInterface;
import view.TerminalView;

public class TopSecret {
    public static void main(String[] args) {
        DataStoreInterface store = new SQLiteDataStore();
        UserManagerInterface users = new UserManager(store);
        Set<String> userSet = new HashSet<>();
        Set<String> adminSet = new HashSet<>();
        for (User u : store.listUsers()) {
            userSet.add(u.getUsername());
            if (adminSet.isEmpty()) {
                adminSet.add(u.getUsername());
            }
        }
        AccessControlInterface access = new AccessControl(adminSet, userSet, Paths.get("roles.txt"));
        AuditLogInterface audit = new AuditLog(store);
        MessageManager messages = new MessageManager(users, store);
        ControllerInterface controller = new Controller(users, access, store, audit, messages);

        boolean isWeb = false;
        for (String arg : args) {
            if (arg.equals("-web")) {
                isWeb = true;
            }
        }

        if (isWeb) {
            if (!controller.hasUsers()) {
                System.out.println("No user accounts exist. Create a user in the terminal first.");
                System.exit(1);
            }
            WebServer web = new WebServer(controller, 3002);
            try {
                web.start();
            } catch (IOException e) {
                System.out.println("Could not start the web server: " + e.getMessage());
                System.exit(1);
            }
            System.out.println("TopSecret web server started at http://localhost:3002/");
            Runtime.getRuntime().addShutdownHook(new Thread(web::stop));
            try {
                new CountDownLatch(1).await();
            } catch (InterruptedException e) {
                web.stop();
                Thread.currentThread().interrupt();
            }
            return;
        }

        TerminalView terminal = new TerminalView(controller);
        System.exit(terminal.run());
    }
}
