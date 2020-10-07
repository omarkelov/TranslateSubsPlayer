package ru.nsu.fit.markelov.managers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import ru.nsu.fit.markelov.controllers.Controller;
import ru.nsu.fit.markelov.controllers.MenuController;
import ru.nsu.fit.markelov.controllers.PlayerController;
import ru.nsu.fit.markelov.util.validation.IllegalInputException;

import java.io.IOException;

import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.buildErrorAlert;
import static ru.nsu.fit.markelov.util.validation.IllegalInputException.requireNonNull;

/**
 * SceneManager class is used for managing JavaFX stage.
 *
 * @author Oleg Markelov
 */
public class SceneManager implements AutoCloseable {

    private static final String DEFAULT_TITLE = "Translate Subs Player";
    private static final String FXML_DIRECTORY = "/ru/nsu/fit/markelov/fxml/";

    private final Stage stage;
    private final FileChooserManager fileChooserManager;
    private Controller controller;

    /**
     * Creates new SceneManager with specified JavaFX stage and file chooser manager.
     *
     * @param stage              JavaFX stage.
     * @param fileChooserManager file chooser manager.
     * @throws IllegalInputException if one of the input parameters is null.
     */
    public SceneManager(Stage stage,
                        FileChooserManager fileChooserManager) throws IllegalInputException {
        this.stage = requireNonNull(stage);
        this.fileChooserManager = requireNonNull(fileChooserManager);

        initStage();
    }

    private void initStage() {
        stage.setTitle(DEFAULT_TITLE);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

    /**
     * Sets a new title for the stage.
     *
     * @param title a new title.
     */
    public void setTitle(String title) {
        stage.setTitle(title);
    }

    /**
     * Sets a default title for the stage.
     */
    public void setDefaultTitle() {
        stage.setTitle(DEFAULT_TITLE);
    }

    /**
     * Toggles current stage fullscreen state.
     */
    public void toggleFullScreen() {
        stage.setFullScreen(!isFullScreen());
    }

    /**
     * Returns whether current stage is fullscreen.
     *
     * @return whether current stage is fullscreen.
     */
    public boolean isFullScreen() {
        return stage.isFullScreen();
    }

    /**
     * Creates new MenuController and switches the scene to a menu.
     */
    public void switchToMenu() {
        try {
            switchScene(new MenuController(this));
        } catch (IllegalInputException e) {
            buildErrorAlert("scene switching").showAndWait();
        }
    }

    /**
     * Creates new PlayerController and switches the scene to a player.
     */
    public void switchToPlayer() {
        try {
            switchScene(new PlayerController(this, fileChooserManager));
        } catch (IllegalInputException e) {
            buildErrorAlert("scene switching").showAndWait();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        controller.close();
    }

    private void switchScene(Controller controller) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(
                FXML_DIRECTORY + controller.getFXMLFileName()));
            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();

            if (this.controller != null) {
                this.controller.close();
            }
            this.controller = controller;

            if (stage.getScene() == null) {
                stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
            } else {
                stage.getScene().setRoot(root);
            }

            controller.runAfterSceneSet(root);
        } catch (IOException e) {
            e.printStackTrace();
            buildErrorAlert("layout loading").showAndWait();
        } catch (IllegalInputException e) {
            e.printStackTrace();
            buildErrorAlert().showAndWait();
        }
    }
}
