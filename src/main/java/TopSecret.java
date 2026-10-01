public class TopSecret {
    public static void main(String[] args) {
        ProgramControlInterface control = new ProgramControl();
        UserInterface userInterface = new UserInterface(control);
        int exitCode = userInterface.run(args);
        if (exitCode != 0){
            System.exit(exitCode);
        }
    }
}
