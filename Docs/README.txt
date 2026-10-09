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



    List Available Files:
        Run the program without arguments to display the available mission files:
            01 file1.txt
            02 file2.txt
            03 file3.txt


    View a File:
        Pass the file number as an argument:
            java -jar TopSecret.jar 01
            The contents of the corresponding file will be displayed in the terminal before the program exits.


    Use an Alternate Cipher Key
        An alternate cipher key can be provided as a second argument:
            java -jar TopSecret.jar 01 alternate-key.txt
            (If no alternate key is provided, the default cipher key is used.)


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

