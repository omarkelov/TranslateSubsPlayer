package ru.nsu.fit.markelov;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.nsu.fit.markelov.javafxutil.AlertBuilder;
import ru.nsu.fit.markelov.managers.FileChooserManager;
import ru.nsu.fit.markelov.managers.SceneManager;
import ru.nsu.fit.markelov.user.UserManager;
import ru.nsu.fit.markelov.util.validation.IllegalInputException;

import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.APPLICATION_LAUNCH_ERROR_HEADER;

/**
 * PlayerApplication is a player with special features made with JavaFX.
 *
 * @author Oleg Markelov
 */
public class PlayerApplication extends Application {

    private SceneManager sceneManager;

    /**
     * Launches JavaFX application.
     *
     * @param args command-line options.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Creates a new SceneManager with FileChooserManager and shows a menu window.
     *
     * Called when JavaFX starts the application.
     *
     * @param primaryStage stage created by JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            sceneManager = new SceneManager(primaryStage, getHostServices(),
                new FileChooserManager(primaryStage), new UserManager());
            sceneManager.switchToPlayer();
            primaryStage.show();
        } catch (IllegalInputException e) {
            new AlertBuilder()
                .setHeaderText(APPLICATION_LAUNCH_ERROR_HEADER).setException(e)
                .build().showAndWait();
        }
    }

    /**
     * Closes scene manager, relinquishing any underlying resources.
     *
     * Called when JavaFX stops the application.
     */
    @Override
    public void stop() {
        if (sceneManager != null) {
            sceneManager.close();
        }
    }
}
