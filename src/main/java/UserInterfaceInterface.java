public interface UserInterfaceInterface {
    int run();
    boolean loginStep();
    void showMainMenu();
    void browseMenu(String itemType);
    void listEntries(String itemType);
    void readEntry(String itemType);
    void addEntry(String itemType);
    void reviewAuditLog();
    void changePassword();
    void createUser();
    void manageAccess();
}
