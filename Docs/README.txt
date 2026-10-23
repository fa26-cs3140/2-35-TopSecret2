opSecret

TopSecret is an interactive command-line utility for storing and reviewing secret
mission data -- briefs, agents, and facilities -- in a SQLite database.
It supports multiple user accounts with standard and admin access levels, and it
keeps an audit log of every record that is read or created.

Features:
    Supports multiple user accounts
    The first account created is automatically an admin; later accounts are
        standard users by default
    Admins can create entries, create new users, promote or demote users,
        and review the audit log
    Standard users can browse and read entries and change their own password
    Users only see the menu options they have access to
    Browse, list, and read briefs, agents, and facilities; admins can add
        new entries, which are immediately visible in the lists
    Every read and create is written to an audit log stored in the database,
        recording the user, the action, the record, and the time
    Passwords are stored ciphered in a credentials file; only the account
        owner can set or change their password
    Usernames must be at least 3 characters and may contain letters, numbers,
        hyphens, and underscores
    Passwords must be at least 5 characters and include uppercase and
        lowercase letters
    Runs through Gradle or as a standalone JAR

Requirements:
    Java
    Gradle (the Gradle wrapper is included)

Usage
    Run with Gradle
        ./gradlew run

    On Windows:
        .\gradlew run

    Run the JAR
        ./gradlew build
        java -jar TopSecret.jar

    First run:
        If no accounts exist, the program prompts you to create the first
        account (username, then a new password entered twice). This first
        account is an admin. Users created later by an admin set their own
        password the first time they log in.

    Main menu:
        1. Browse briefs
        2. Browse agents
        3. Browse facilities
        4. Change Password
        5. Create a new user        (admin only)
        6. Promote or Demote users   (admin only)
        7. Review audit log          (admin only)
        8. Exit

        Admin-only options are not shown to standard users.

    Browse menu (for each of briefs, agents, facilities):
        1. List entries
        2. Read an entry by ID
        3. Add an entry              (admin only)
        4. Back

    Record properties:
        Briefs:     title, date, text
        Agents:     name, date of birth, date of death, notes
        Facilities: name, abbreviation

Project Structure:
    Source code is in src/main/java/ and tests are in src/test/java/
    Briefs, agents, facilities, users, and audit log records are stored in a
        SQLite database, created automatically on first run and seeded with
        the sample dataset in src/main/resources/dataset/
    User passwords are stored ciphered in credentials.cip in the working
        directory, using the cipher key in src/main/resources/ciphers/
    Admin and standard roles are stored in roles.txt in the working
        directory, so promotions and demotions persist between runs
    Documentation is in the docs directory, including team.txt (team members,
        roles, and GitHub IDs), the system UML class diagram, and the
        database ERD

Testing:
    The project uses JUnit and Mockito for unit tests and integration tests.

    Run the test suite with:
        ./gradlew test

    On Windows:
        .\gradlew test

Build:
    Create the JAR with:
        ./gradlew build

    The generated JAR contains all dependencies and can then be run using:
        java -jar TopSecret.jar