package client.main;

/**
 * Launcher that does not extend Application, used to run the client
 * from the classpath (executable jar, IDE without module configuration).
 */
public class Launcher {

    public static void main(String[] args) {
        UTubeApplication.main(args);
    }
}
