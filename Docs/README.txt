TopSecret

TopSecret is an interactive command-line utility for securely accessing and searching secret mission briefs stored in a SQLite database.
It provides an authenticated interface for browsing mission records, viewing brief contents, and searching for specific words or phrases.

Features:
    Authenticates users with a username and password
    Supports creating credentials on the first run and changing passwords
    Stores mission briefs in a SQLite database
    Lists available mission briefs with corresponding numbers
    Displays the contents of a selected mission brief
    Searches mission briefs by word or phrase, ignoring case
    Returns a message when no search matches are found
    Provides an interactive menu that remains active until the user exits
    Supports encrypted credential files using the default cipher key
    Supports an alternate cipher key when specified
    Runs through Gradle or as a standalone JAR

Requirements:
    Java
    Gradle

Usage
    Run with Gradle
        ./gradlew run

    On Windows:
        .\gradlew run

    Run the JAR
        java -jar TopSecret.jar



    Options:
        1. Run the program without arguments to display the available options:
            Available Options:

            1. List missions
            2. Search missions
            3. View mission
            4. Change password
            5. Exit
            ------------------------------
        *. enter option number 1 to list the missions.
        *. enter option number 2 to search for a particular phrase in the mission briefs
        *. enter option number 3 to view a mission by entering the Id in the following prompt
        *. enter option number 4 to change password

        *. enter option number 5 to exit program




Project Structure:
    Mission briefs are stored in a SQLite database, initialized automatically if it does not exist.
    User credentials are stored in a separate encrypted .cip file
    Cipher keys are stored in the ciphers directory:  src/main/resources/ciphers
    documentations are stored in the Docs directory at the project level
    Tests are in src/test/java/


Testing:
    The project uses JUnit for automated unit testing.

    Run the test suite with:
        ./gradlew test

Build:
    Create the JAR with:
        ./gradlew build

    The generated JAR can then be run using:
        java -jar TopSecret.jar

