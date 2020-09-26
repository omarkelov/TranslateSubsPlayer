package ru.nsu.fit.markelov;

/**
 * Main class is used for launching SnakeGame inside a .jar file.
 *
 * In case main class extends javafx.application.Application, Java launcher requires the JavaFX
 * runtime available as modules (not as jars).
 *
 * @author Oleg Markelov
 */
public class Main {
    /**
     * Launches SnakeGame calling its main() method.
     *
     * @param args command-line options.
     */
    public static void main(String[] args) {
        PlayerApplication.main(args);
    }
}
