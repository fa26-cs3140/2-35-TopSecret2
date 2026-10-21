import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.List;

// Command-line interface for TopSecret.
public class UserInterface implements UserInterfaceInterface {
    private final UserManagerInterface users;
    private final AccessControlInterface access;
    private final DataStoreInterface store;
    private final AuditLogInterface audit;
    private final Scanner sc;
    private String currentUser;
    private User currentUserObj;

    public UserInterface(UserManagerInterface users,
                         AccessControlInterface access,
                         DataStoreInterface store,
                         AuditLogInterface audit) {
        this.users = users;
        this.access = access;
        this.store = store;
        this.audit = audit;
        this.sc = new Scanner(System.in);
    }


    @Override
    public int run() {
        if (!loginStep()){
            return 1;
        }
        else {
            showMainMenu();
            return 0;
        }
    }

    @Override
    public boolean loginStep() {
        if (!users.hasUser()){ //Setting up the first account
            System.out.print("Creating first account. Create a username: ");
            String username = sc.hasNextLine() ? sc.nextLine() : null;
            if (username == null){
                return false;
            }
            try {
                User newUser = users.createUser(username);
                System.out.print("Create a password: ");
                String password = sc.hasNextLine() ? sc.nextLine() : null;
                System.out.print("Confirm password: ");
                String passwordConfirm = sc.hasNextLine() ? sc.nextLine() : null;
                try {
                    users.changePassword(username, password, passwordConfirm);
                } catch (TopSecretException e) {
                    System.out.println(e.getMessage());
                    return false;
                }
                currentUser = username;
                currentUserObj = newUser;
                System.out.println("Account is created!");
                return true;
            } catch (TopSecretException e){
                System.out.println(e.getMessage());
                return false;
            }
        }
        System.out.print("Enter your username:"); //User enters username
        String username = sc.hasNextLine() ? sc.nextLine() : null;
        if (username == null){
            return false;
        }
        if (users.needsPasswordSetup(username)){ //If they haven't gotten a password yet
            System.out.print("You need to set up your password. \n Enter password: ");
            String password = sc.hasNextLine() ? sc.nextLine() : null;
            System.out.print("Confirm password: ");
            String passwordConfirm = sc.hasNextLine() ? sc.nextLine() : null;
            try {
                users.changePassword(username, password, passwordConfirm);
            } catch (TopSecretException e){
                System.out.println(e.getMessage());
                return false;
            }
            currentUser = username;
            currentUserObj = users.getUserByUsername(username);
            return true;

        } //Business as usual:
        System.out.print("Enter your Password");
        String password = sc.hasNextLine() ? sc.nextLine() : null;
        if (!users.verifyLogin(username, password)){
            System.out.println("Incorrect username or password");
            return false;
        }
        currentUser = username;
        currentUserObj = users.getUserByUsername(username);
        return true;

    }

    @Override
    public void showMainMenu() {
        boolean admin = access.isAdmin(currentUser);
        while (true){
            System.out.println("1. Browse briefs");
            System.out.println("2. Browse agents");
            System.out.println("3. Browse facilities");
            System.out.println("4. Search Missions");
            System.out.println("5. Change Password");
            if (admin) {
                System.out.println("6. Create a new user");
                System.out.println("7. Promote or Demote users");
                System.out.println("8. Review audit log");
            }
            System.out.println("9. Exit/logout");
            System.out.print("Enter an option number:");
            String entered = sc.hasNextLine() ? sc.nextLine() : null;
            int choice;
            if (entered == null){
                return;
            }
            try {
                choice = Integer.parseInt(entered.trim());
            } catch (NumberFormatException e) {
                choice = -1;
            }
            if (choice == 1) {
                browseMenu("briefs");
            } else if (choice == 2) {
                browseMenu("agents");

            }else if (choice == 3) {
                browseMenu("facilities");
            }else if (choice == 4) {
                searchMissions();
            }else if (choice == 5) {
                changePassword();
            }
            else if (choice == 6 && admin){
                createUser();
            }else if (choice ==7 && admin){
                manageAccess();
            }else if (choice == 8 && admin){
                reviewAuditLog();
            }else if (choice == 9){
                return;
            }else {
                System.out.println("Invalid Option, try again.");
            }
        }
    }

    @Override
    public void browseMenu(String itemType) {
        boolean admin = access.isAdmin(currentUser);
        while (true) {
            System.out.println("1. List " + itemType);
            System.out.println("2. Read " + itemType);
            if (admin) {
                System.out.println("3. Add " + itemType);
            }
            System.out.println("4. Go back to main menu");
            System.out.print("Enter an option number:");
            String entered = sc.hasNextLine() ? sc.nextLine() : null;
            int choice;
            if (entered == null){
                return;
            }
            try {
                choice = Integer.parseInt(entered.trim());
            } catch (NumberFormatException e) {
                choice = -1;}
            if (choice == 1){
                listEntries(itemType);
            } else if (choice == 2) {
                readEntry(itemType);
            } else if (choice == 3 && admin){
                addEntry(itemType);
            } else if (choice == 4){
                return;
            } else {
                System.out.println("Invalid option, try again");
            }

        }
    }

    @Override
    public void listEntries(String itemType) {
        if (itemType.equals("briefs")){
            for (Brief i : store.listBriefs()){
                System.out.println(i.getBriefID() +" : " + i.getTitle());
            }
        }
        else if (itemType.equals("agents")){
            for (Agent i : store.listAgents()){
                System.out.println(i.getAgentID() +" : " + i.getName());
            }
        }
        else if (itemType.equals("facilities")){
            for (Facility i : store.listFacilities()){
                System.out.println(i.getFacilityID() +" : " + i.getName());
            }
        } else {
            System.out.println("Invalid option");
        }
    }

    @Override
    public void readEntry(String itemType) {
        System.out.print("Enter the " + itemType + " id: ");
        String entered = sc.hasNextLine() ? sc.nextLine() : null;
        int choice;
        if (entered == null) {
            return;
        }
        try {
            choice = Integer.parseInt(entered.trim());
        } catch (NumberFormatException e) {
            choice = -1;
        }
        if (itemType.equals("briefs")) {
            Brief requested = store.getBrief(choice);
            if (requested != null) {
                System.out.println("Title: " + requested.getTitle());
                System.out.println("Date: " + requested.getDate());
                System.out.println("Content: " + requested.getContent());
                audit.log(new LoggedAction(LoggedAction.READ, currentUserObj, LocalDateTime.now().toString(), itemType + " " + choice));
            } else {
                System.out.println("No brief found with this id");
            }
        } else if (itemType.equals("agents")) {
            Agent requested = store.getAgent(choice);
            if (requested != null) {
                System.out.println("Name: " + requested.getName());
                System.out.println("DoB: " + requested.getDate_of_birth());
                System.out.println("DoDeath: " + requested.getDate_of_death());
                System.out.println("Notes: " + requested.getNotes());
                audit.log(new LoggedAction(LoggedAction.READ, currentUserObj, LocalDateTime.now().toString(), itemType + " " + choice));
            } else {
                System.out.println("No agent found with this id");
            }
        } else if (itemType.equals("facilities")) {
            Facility requested = store.getFacility(choice);
            if (requested != null) {
                System.out.println("Name: " + requested.getName());
                System.out.println("DoB: " + requested.getAbbreviation());
                audit.log(new LoggedAction(LoggedAction.READ, currentUserObj, LocalDateTime.now().toString(), itemType + " " + choice));

            } else {
                System.out.println("No facility found with this id");
            }
        }
        else {
            System.out.println("This really should never happen, \n but just in case you passed a impossible itemType");
            }

        }



    @Override
    public void addEntry(String itemType) {
        try{
            if (itemType.equals("briefs")){
                System.out.print("Mission ID: ");
                String id = sc.hasNextLine() ? sc.nextLine() : null;
                System.out.print("Title: ");
                String title = sc.hasNextLine() ? sc.nextLine() : null;
                System.out.println("Date: ");
                String date = sc.hasNextLine() ? sc.nextLine() : null;
                System.out.print("Content: ");
                String content = sc.hasNextLine() ? sc.nextLine() : null;
                Brief newBrief = store.addBrief(new Brief(Integer.parseInt(id.trim()),title,date,content));
                audit.log(new LoggedAction(LoggedAction.CREATE, currentUserObj,
                        LocalDateTime.now().toString(), "briefs " + newBrief.getBriefID()));
            } else if (itemType.equals("agents")){
                System.out.print("Mission ID: ");
                String mid = sc.hasNextLine() ? sc.nextLine() : null;
                System.out.print("Facility ID: ");
                String fid = sc.hasNextLine() ? sc.nextLine() : null;
                System.out.print("Name: ");
                String name = sc.hasNextLine() ? sc.nextLine() : null;
                System.out.print("Date of birth: ");
                String dob = sc.hasNextLine() ? sc.nextLine() : null;
                System.out.print("Date of death: ");
                String dod = sc.hasNextLine() ? sc.nextLine() : null;
                System.out.print("Notes: ");
                String notes = sc.hasNextLine() ? sc.nextLine() : null;

                Agent newAgent = store.addAgent(new Agent(Integer.parseInt(mid.trim()), Integer.parseInt(fid.trim()), name, dob, dod, notes));
                audit.log(new LoggedAction(LoggedAction.CREATE, currentUserObj, LocalDateTime.now().toString(), "agents " + newAgent.getAgentID()));
            }else if (itemType.equals("facilities")) {
                System.out.print("Name: ");
                String name = sc.hasNextLine() ? sc.nextLine(): null;
                System.out.print("Abbrievation: ");
                String abrev = sc.hasNextLine() ? sc.nextLine(): null;
                Facility newFacility = store.addFacility(new Facility(name, abrev));
                audit.log(new LoggedAction(LoggedAction.CREATE, currentUserObj, LocalDateTime.now().toString(), "facilities " + newFacility.getFacilityID()));
            }
            else {
                System.out.println("This really should never happen, \n but just in case you passed a impossible itemType");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        listEntries(itemType);
    }

    @Override
    public void reviewAuditLog() {
        for (LoggedAction log : audit.getLog()){
            String type = log.getAction() == LoggedAction.READ ? "read": "create";
            System.out.println(log.getDateTime() + " : " + log.getUser().getUsername() + " : " + type + " : " + log.getRecord());

        }
    }

    @Override
    public void changePassword() {
        System.out.print("Enter your new password: ");
        String password = sc.hasNextLine() ? sc.nextLine() : null;
        System.out.print("Confirm password: ");
        String passwordConfirm = sc.hasNextLine() ? sc.nextLine() : null;
        try {
            users.changePassword(currentUser, password, passwordConfirm);
            System.out.println("Password changed");
        } catch (TopSecretException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void createUser() {
        System.out.println("New username: ");
        String newUsername = sc.hasNextLine()? sc.nextLine() : null;
        try {
            users.createUser(newUsername);
        } catch (TopSecretException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void manageAccess() {
        System.out.println("Manageable Users");
        for (String name : users.listUsernames()) {
            if (!name.equals(currentUser)) {
                System.out.println(name);
            }
        }
        System.out.print("Enter \"Promote\" or \"Demote\"");
        String promOrDemo = sc.hasNextLine()? sc.nextLine() : null;
        System.out.print("Which user? ");
        String changedUser = sc.hasNextLine()? sc.nextLine() : null;
        if (promOrDemo == null || changedUser == null){
            return;
        }
        boolean noerror;
        if (promOrDemo.equalsIgnoreCase("promote")){
            noerror = access.promoteUser(currentUser, changedUser);
        } else if (promOrDemo.equalsIgnoreCase("demote")){
            noerror = access.demoteUser(currentUser, changedUser);
        } else {
            System.out.println("Invalid option");
            return;
        }
        System.out.println(noerror ? changedUser + " was " + promOrDemo.toLowerCase() + "d": "Not allowed");

    }

    @Override
    public void searchMissions() {
        System.out.print("Search phrase: ");
        String searchingFor = sc.hasNextLine() ? sc.nextLine() : null;
        if (searchingFor == null) {
            return;
        }
        List<Mission> results = new Search(store).search(searchingFor);
        for (Mission m : results) {
            System.out.println(m.getId() + " : " + m.getTitle());
        }
        if (results.isEmpty()) {
            System.out.println("No matches.");
        }

    }

}
