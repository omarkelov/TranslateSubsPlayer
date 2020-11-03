package ru.nsu.fit.markelov.managers;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ru.nsu.fit.markelov.controllers.Controller;
import ru.nsu.fit.markelov.controllers.MenuController;
import ru.nsu.fit.markelov.controllers.PlayerController;
import ru.nsu.fit.markelov.controllers.player.VlcException;
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

    private static final String DEFAULT_ERROR_HEADER = "Unknown error";
    private static final String DEFAULT_ERROR_CONTENT =
        "Unknown error has occurred. Please contact the developer.";

    private static final String VLC_ERROR_HEADER = "VLC media player";
    private static final String VLC_ERROR_CONTENT = "Make sure you have the latest version of " +
        "VLC media player installed (can be downloaded from videolan.org).";

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

    public ReadOnlyDoubleProperty getStageWidthProperty() {
        return stage.widthProperty();
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
     * Shows an error dialog pane with default header and content text.
     */
    public void showDefaultError() {
        showError(null, null);
    }

    /**
     * Shows an error dialog pane with specified header and content text.
     *
     * @param header  the header of this dialog pane.
     * @param content the content text of this dialog pane.
     */
    public void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setHeaderText(header == null || header.isEmpty() ? DEFAULT_ERROR_HEADER : header);
        alert.setContentText(content == null || content.isEmpty() ? DEFAULT_ERROR_CONTENT : content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        try {
            alert.initOwner(stage);
        } catch (RuntimeException ignored) {}

        alert.showAndWait();
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
        } catch (VlcException e) {
            showError(VLC_ERROR_HEADER, VLC_ERROR_CONTENT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (controller != null) {
            controller.close();
        }
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

            controller.runAfterSceneSet();
        } catch (IOException e) {
            e.printStackTrace();
            buildErrorAlert("layout loading").showAndWait();
        }
    }
}
