TopSecret

TopSecret is an interactive utility for storing and reviewing secret
mission data -- briefs, agents, facilities, and missions -- in a SQLite
database. It supports multiple user accounts with standard and admin
access levels, keeps an audit log of every record that is read or
created, and lets agents exchange terminal-only messages.

Features:
    Supports multiple user accounts
    The first account created is automatically an admin; later accounts are
        standard users by default
    Admins can create entries, create new users, promote or demote users,
        and review the audit log
    Standard users can browse and read entries and change their own password
    Users only see the menu options they have access to
    Browse, list, and read briefs, agents, facilities, and missions;
        admins can add new entries and edit existing ones, which are
        immediately visible in the lists
    A mission is run by one facility; agents belong to one facility and
        may be assigned to multiple missions, from any facility
    Messages are terminal-only: a user addresses a message to an agent
        by name, and an agent reads it by logging in with a matching
        username (matching is case-insensitive, no admin needed)
    Every read and create is written to an audit log stored in the
        database, recording the user, the action, the record, and the time
    Passwords are stored ciphered in a credentials file; only the account
        owner can change their own password

Team:
    Group Member | Name              | Github ID
    Member A     | Srikar Kolli      | Srikar-Kolli
    Member B     | Sebastian Nowicki | sgnowicki-gif
    Member C     | Eltaf Sameem      | Eltaf-Sameem
    Member D     | Jaime Lopez       | MrTwoMile

Packages:
    control - Controller, ControllerInterface, WebServer, MessageManager
    model   - entities, UserManager, AccessControl, AuditLog, SQLiteDataStore
    view    - TerminalView, WebView, HTMLUtils, Utility
    TopSecret (default package) - application entry point and wiring

Running the terminal version (default):
    .\gradlew run

Running the web version:
    .\gradlew run --args="-web"
    Then open http://localhost:3002/ in a browser. The first page is a
    username and password form. Valid credentials set a session cookie
    that expires when the browser is closed; every page except the login
    form requires that session, and unknown pages show an error page
    instead of crashing. The web view is read-only: records and the audit
    log can be browsed and read, but not created or edited. The web
    version does not start until at least one user account exists;
    create one in the terminal first.

Building a runnable jar:
    .\gradlew build
    java -jar build/libs/TopSecret.jar
    java -jar build/libs/TopSecret.jar -web

Accounts:
    Usernames must be at least 3 characters: letters, numbers, hyphens,
        and underscores
    Passwords must be at least 5 characters with an uppercase and a
        lowercase letter
    The first account is created on first run and is automatically admin
    Users created by an admin set their own password at first login
    Passwords are stored ciphered in credentials.cip; access roles are
        stored in roles.txt

Data:
    Records live in a SQLite database (data.db) through SQLiteDataStore
    Seed data ships in src/main/resources/dataset/ (facilities, missions,
        briefs, agents) and is loaded when a database is created
    The cipher key lives in src/main/resources/ciphers/key.txt

Tests:
    Run everything with:
        .\gradlew test --console=plain
    Run one suite with:
        .\gradlew test --tests "control.ControllerTest" --console=plain
    Suites:
        control - ControllerTest (58 unit tests, Mockito mocks),
            ControllerUserManagerTest (real UserManager over a real
            test database), WebServerTest, WebServerIntegrationTest
            (real server on port 0, mocked controller, real HttpClient)
        model   - entity, UserManager, AccessControl, AuditLog,
            SQLiteDataStore, Cipher, FileHandler, Message tests
        view    - TerminalViewTest, WebViewTest, WebViewHTMLUtilsTest,
            HTMLUtilsTest, UtilityTest

Files created at runtime in the working directory:
    credentials.cip - ciphered username/password lines
    roles.txt       - admin and user role lists
    data.db         - the SQLite database
