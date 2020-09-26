package ru.nsu.fit.markelov.javafxutil;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

/**
 * AlertBuilder class is used for building error and confirmation JavaFX alerts.
 *
 * @author Oleg Markelov
 */
public class AlertBuilder {

    /**
     * Returns error alert with header and content text containing default value "game".
     *
     * @return error alert.
     */
    public static Alert buildErrorAlert() {
        return buildErrorAlert(null);
    }

    /**
     * Returns error alert with header and content text containing specified task.
     *
     * If 'task' is null or empty, default value "game" is used.
     *
     * @param task description of the task.
     * @return error alert.
     */
    public static Alert buildErrorAlert(String task) {
        if (task == null || task.isEmpty()) {
            task = "game";
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setHeaderText(capitalize(task) + " error");
        alert.setContentText(
            "Sorry, something went wrong during " + task + ". Please contact the developer.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        return alert;
    }

    /**
     * Returns confirmation alert with specified header and question.
     *
     * @param header   header text.
     * @param question question text.
     * @return confirmation alert.
     */
    public static Alert buildConfirmationAlert(String header, String question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, question, ButtonType.YES, ButtonType.NO);

        alert.setHeaderText(header);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        return alert;
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
