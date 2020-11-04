package ru.nsu.fit.markelov.managers;

import javafx.application.HostServices;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;
import ru.nsu.fit.markelov.controllers.Controller;
import ru.nsu.fit.markelov.controllers.MenuController;
import ru.nsu.fit.markelov.controllers.PlayerController;
import ru.nsu.fit.markelov.controllers.player.VlcException;
import ru.nsu.fit.markelov.javafxutil.AlertBuilder;
import ru.nsu.fit.markelov.javafxutil.LinkText;
import ru.nsu.fit.markelov.util.validation.IllegalInputException;

import java.io.IOException;

import static ru.nsu.fit.markelov.Constants.VLC_PRETTY_URI;
import static ru.nsu.fit.markelov.Constants.VLC_URI;
import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.LAYOUT_LOADING_ERROR_HEADER;
import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.SCENE_SWITCHING_ERROR_HEADER;
import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.VLC_ERROR_CONTENT_POSTFIX;
import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.VLC_ERROR_CONTENT_PREFIX;
import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.VLC_ERROR_HEADER;
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
    private final HostServices hostServices;
    private final FileChooserManager fileChooserManager;
    private Controller controller;

    /**
     * Creates new SceneManager with specified JavaFX stage and file chooser manager.
     *
     * @param stage              JavaFX stage.
     * @param fileChooserManager file chooser manager.
     * @throws IllegalInputException if one of the input parameters is null.
     */
    public SceneManager(Stage stage, HostServices hostServices,
                        FileChooserManager fileChooserManager) throws IllegalInputException {
        this.stage = requireNonNull(stage);
        this.hostServices = requireNonNull(hostServices);
        this.fileChooserManager = requireNonNull(fileChooserManager);

        initStage();
    }

    private void initStage() {
        stage.setTitle(DEFAULT_TITLE);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

    /**
     * Returns current window owner.
     *
     * @return current window owner.
     */
    public Window getWindowOwner() {
        return stage;
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
     * Creates new MenuController and switches the scene to a menu.
     */
    public void switchToMenu() {
        try {
            switchScene(new MenuController(this));
        } catch (IllegalInputException e) {
            new AlertBuilder()
                .setHeaderText(SCENE_SWITCHING_ERROR_HEADER).setException(e).setOwner(stage)
                .build().showAndWait();
        }
    }

    /**
     * Creates new PlayerController and switches the scene to a player.
     */
    public void switchToPlayer() {
        try {
            switchScene(new PlayerController(this, fileChooserManager));
        } catch (IllegalInputException e) {
            new AlertBuilder()
                .setHeaderText(SCENE_SWITCHING_ERROR_HEADER).setException(e).setOwner(stage)
                .build().showAndWait();
        } catch (VlcException e) {
            LinkText linkText = new LinkText(VLC_PRETTY_URI, VLC_URI, hostServices);
            TextFlow contentTextFlow = new TextFlow(new Text(VLC_ERROR_CONTENT_PREFIX),
                linkText.getText(), new Text(VLC_ERROR_CONTENT_POSTFIX));

            new AlertBuilder()
                .setHeaderText(VLC_ERROR_HEADER)
                .setContent(contentTextFlow)
                .setException(e)
                .setOwner(stage)
                .build()
                .showAndWait();
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

            controller.runAfterSceneSet(stage.getScene());
        } catch (IOException e) {
            new AlertBuilder()
                .setHeaderText(LAYOUT_LOADING_ERROR_HEADER).setException(e).setOwner(stage)
                .build().showAndWait();
        } catch (IllegalInputException e) {
            new AlertBuilder()
                .setException(e).setOwner(stage)
                .build().showAndWait();
        }
    }
}
