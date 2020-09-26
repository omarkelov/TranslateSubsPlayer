package ru.nsu.fit.markelov.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ru.nsu.fit.markelov.managers.SceneManager;
import ru.nsu.fit.markelov.util.validation.IllegalInputException;

import static ru.nsu.fit.markelov.util.validation.IllegalInputException.requireNonNull;

/**
 * MenuController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a menu scene.
 *
 * @author Oleg Markelov
 */
public class MenuController implements Controller {

    private static final String FXML_FILE_NAME = "menu.fxml";

    @FXML private Button playerButton;

    private final SceneManager sceneManager;

    /**
     * Creates new MenuController with specified SceneManager.
     *
     * @param sceneManager scene manager.
     * @throws IllegalInputException if one of the input parameters is null.
     */
    public MenuController(SceneManager sceneManager) throws IllegalInputException {
        this.sceneManager = requireNonNull(sceneManager);
    }

    @FXML
    private void initialize() {
        playerButton.setOnAction(actionEvent -> sceneManager.switchToPlayer());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFileName() {
        return FXML_FILE_NAME;
    }
}
