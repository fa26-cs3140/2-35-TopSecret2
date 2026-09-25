public class TopSecret {
    public static void main(String[] args) {
        ProgramControl control = new ProgramControl();
        UserInterface userInterface = new UserInterface(control);
        int exitCode = userInterface.run(args);
        if (exitCode != 0){
            System.exit(exitCode);
        }
    }
}
