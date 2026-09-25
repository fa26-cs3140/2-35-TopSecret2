TopSecret

TopSecret is a command-line utility for viewing and deciphering secret mission data stored in text files. It provides a simple interface for browsing available mission files and displaying their contents directly in the terminal.

Features:
    Lists available mission data files
    Displays the contents of a selected file
    Supports encrypted mission files
    Automatically decrypts files using the default cipher key
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
    data files are stored in the data directory, while cipher keys are stored in the ciphers directory.
    src/main/resources/data                                       src/main/resources/ciphers

    documentations are stored in the Docs directory at the project level

    Tests are in src/test/java/


Testing:
    The project uses JUnit for automated unit testing. Tests cover the command-line interface, file handling, program control, and cipher functionality.

    Run the test suite with:
        ./gradlew test

Build:
    Create the JAR with:
        ./gradlew build

    The generated JAR can then be run using:
        java -jar TopSecret.jar

