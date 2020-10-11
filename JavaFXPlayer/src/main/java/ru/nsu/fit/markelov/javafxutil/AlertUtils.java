package ru.nsu.fit.markelov.javafxutil;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

/**
 * AlertUtils class is used for showing an error dialog pane.
 *
 * @author Oleg Markelov
 */
public class AlertUtils {

    private static final String DEFAULT_ERROR_HEADER = "Unknown error";
    private static final String DEFAULT_ERROR_CONTENT =
        "Unknown error has occurred. Please contact the developer.";

    public static void showDefaultError() {
        showError(null, null);
    }

    /**
     * Shows an error dialog pane with specified header and content text.
     *
     * @param header  the header of this dialog pane.
     * @param content the content text of this dialog pane.
     */
    public static void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setHeaderText(header == null || header.isEmpty() ? DEFAULT_ERROR_HEADER : header);
        alert.setContentText(content == null || content.isEmpty() ? DEFAULT_ERROR_CONTENT : content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.showAndWait();
    }
}
