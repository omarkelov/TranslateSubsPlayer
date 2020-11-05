package ru.nsu.fit.markelov.controllers;

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
     */
    default void runAfterSceneSet() {}

    /**
     * {@inheritDoc}
     */
    @Override
    default void close() {}
}
