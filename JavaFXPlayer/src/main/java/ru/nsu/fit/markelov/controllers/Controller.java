package ru.nsu.fit.markelov.controllers;

import javafx.scene.Scene;
import ru.nsu.fit.markelov.util.validation.IllegalInputException;

/**
 * Controller interface is used by JavaFX in javafx.fxml.FXMLLoader for controlling a view and a
 * model.
 *
 * @author Oleg Markelov
 */
public interface Controller extends AutoCloseable {
    /**
     * Returns .fxml file name.
     *
     * @return .fxml file name.
     */
    String getFXMLFileName();

    /**
     * This method is designed to run after the parent node is loaded by javafx.fxml.FXMLLoader.
     *
     * @param scene current JavaFX scene.
     * @throws IllegalInputException if one of input parameters is null.
     */
    default void runAfterSceneSet(Scene scene) throws IllegalInputException {}

    /**
     * {@inheritDoc}
     */
    @Override
    default void close() {}
}
