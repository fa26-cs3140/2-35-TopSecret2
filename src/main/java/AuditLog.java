import java.util.List;

public class AuditLog implements AuditLogInterface {
    private DataStoreInterface dataStore;

    public AuditLog(DataStoreInterface dataStore) {
        this.dataStore = dataStore;
    }

    public LoggedAction log(LoggedAction action) {
        try {
            dataStore.addLog(action);
            return action;
        } catch (IllegalArgumentException e) {
            return null;
        }

    }

    public List<LoggedAction> getLog() {
        try {
            List<LoggedAction> logList = dataStore.listLogs();
            return logList;
        } catch (IllegalStateException e) {
            return null;
        }
    }

}
