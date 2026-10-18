import java.util.List;

public interface AuditLogInterface {
    // Pass a LoggedAction object to be added to the audit log
    LoggedAction log(LoggedAction action);

    // Get the contents of the audit log
    List<LoggedAction> getLog();
}
