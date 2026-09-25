public class TopSecret {
    public static void main(String[] args) {
        ProgramControl control = new ProgramControl();
        UserInterface userInterface = new UserInterface(control);
        int exitCode = userInterface.run(args);
        if (exitCode != 0){
            System.exit(exitCode);
        }

        // TODO: Implement the main method
        // TODO: make sure we can run with gradle
        // TODO: make sure we can run from terminal
        // TODO: make sure we can run using the generated fat JAR file
        // TODO: make sure all test cases pass
        // TODO: validate/double check application requirements and app functinallity
        System.out.println("I am in Main method");
    }
}
