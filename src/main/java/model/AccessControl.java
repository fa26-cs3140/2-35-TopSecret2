package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class    AccessControl implements AccessControlInterface {

    private final Set<String> admins;
    private final Set<String> users;
    private final Path rolesFile; //null = in-memory only (used by tests)

    public AccessControl(Set<String> admins, Set<String> users) {
        this(admins, users, null);
    }

    // Persistent variant, used by main: roles load from rolesFile when it
// exists and save back on every promote/demote. The passed-in sets are
// the first-run bootstrap: users always merge in (main builds them
// fresh from the database), the file's admins win once it exists.
    public AccessControl(Set<String> admins, Set<String> users, Path rolesFile) {
        this.rolesFile = rolesFile;
        Set<String> loadedAdmins = new HashSet<>();
        Set<String> loadedUsers = new HashSet<>();
        boolean loaded = rolesFile != null
                && loadRoles(rolesFile, loadedAdmins, loadedUsers);
        this.users = new HashSet<>(users);
        this.users.addAll(loadedUsers);
        this.admins = loaded && !loadedAdmins.isEmpty()
                ? new HashSet<>(loadedAdmins) : new HashSet<>(admins);
        if (rolesFile != null && !loaded) {
            saveRoles(); //first run: make the bootstrap durable right away
        }
    }

    private boolean loadRoles(Path file, Set<String> adminsOut,
                              Set<String> usersOut) {
        try {
            for (String line : Files.readAllLines(file)) {
                int colon = line.indexOf(':');
                if (colon <= 0) {
                    continue;
                }
                String name = line.substring(colon + 1);
                if (line.substring(0, colon).equals("admin")) {
                    adminsOut.add(name);
                } else if (line.substring(0, colon).equals("user")) {
                    usersOut.add(name);
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void saveRoles() {
        if (rolesFile == null) {
            return;
        }
        List<String> lines = new ArrayList<>();
        for (String name : users) {
            lines.add("user:" + name);
        }
        for (String name : admins) {
            lines.add("admin:" + name);
        }
        try {
            Files.write(rolesFile, lines);
        } catch (IOException e) {
            // Roles stay correct in memory; they just won't survive a restart.
        }
    }


    @Override
    public boolean isAdmin(String username) {
        return username != null && admins.contains(username);
    }

    @Override
    public boolean promoteUser(String adminUsername, String targetUsername) {
        if (!isAdmin(adminUsername)
                || targetUsername == null
                || !users.contains(targetUsername)
                || admins.contains(targetUsername)) {
            return false;
        }

        boolean added = admins.add(targetUsername);
        if (added) {
            saveRoles();
        }
        return added;

    }

    @Override
    public boolean demoteUser(String adminUsername, String targetUsername) {
        if (!isAdmin(adminUsername)
                || targetUsername == null
                || adminUsername.equals(targetUsername)
                || !admins.contains(targetUsername)) {
            return false;
        }

        boolean removed = admins.remove(targetUsername);
        if (removed) {
            saveRoles();
        }
        return removed;

    }

    @Override
    public boolean hasPermission(String username, String action) {
        if (username == null || action == null || !users.contains(username)) {
            return false;
        }

        if (action.equalsIgnoreCase("read")) {
            return true;
        }

        if (isAdmin(username)) {
            return action.equalsIgnoreCase("create")
                    || action.equalsIgnoreCase("create_entry")
                    || action.equalsIgnoreCase("create_user")
                    || action.equalsIgnoreCase("audit_log");
        }

        return false;
    }
}
