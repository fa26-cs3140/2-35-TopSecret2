public class LoggedAction {
    // Action constants
    public static final int READ = 0;
    public static final int CREATE = 1;

    private int action;
    private User user;
    // Probably redefine dateTime and record to better types later
    private String dateTime;
    // Record is the item being acted upon
    private String record;

    public LoggedAction(int action,
                        User user,
                        String dateTime,
                        String record) {
        this.action = action;
        this.user = user;
        this.dateTime = dateTime;
        this.record = record;
    }
}
