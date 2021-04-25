package ru.nsu.fit.markelov.javafxutil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.util.function.Consumer;
import java.util.prefs.Preferences;

public class LoginDialog {

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final Preferences PREFERENCES = Preferences.userRoot().node(LoginDialog.class.getName());
    private static final Color ERROR_COLOR = Color.MAROON;

    public static void show(Consumer<Pair<String, String>> action) {
        show(
            new Label("Enter your login data"),
            new Pair<>(PREFERENCES.get(USERNAME_KEY, ""), PREFERENCES.get(PASSWORD_KEY, "")),
            action
        );
    }

    public static void show(String error, Pair<String, String> usernamePassword,
                            Consumer<Pair<String, String>> action) {

        Label errorLabel = new Label(error);
        errorLabel.setTextFill(ERROR_COLOR);

        show(errorLabel, usernamePassword, action);
    }

    private static void show(Label infoLabel, Pair<String, String> usernamePassword,
                             Consumer<Pair<String, String>> action) {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.initStyle(StageStyle.UTILITY);

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, loginButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        GridPane.setHalignment(infoLabel, HPos.CENTER);

        TextField usernameField = new TextField(usernamePassword.getKey());
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setText(usernamePassword.getValue());
        passwordField.setPromptText("Password");

        CheckBox rememberCheckBox = new CheckBox("Remember me");

        grid.add(infoLabel, 0, 0, 2, 1);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(rememberCheckBox, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(usernameField::requestFocus);

        dialog.getDialogPane().lookupButton(loginButtonType).addEventFilter(ActionEvent.ACTION, actionEvent -> {
            if (usernameField.getText().isBlank()) {
                infoLabel.setText("Username cannot be empty");
                infoLabel.setTextFill(ERROR_COLOR);
                usernameField.requestFocus();

                actionEvent.consume();
                return;
            }

            if (passwordField.getText().isBlank()) {
                infoLabel.setText("Password cannot be empty");
                infoLabel.setTextFill(ERROR_COLOR);
                passwordField.requestFocus();

                actionEvent.consume();
                return;
            }

            if (rememberCheckBox.isSelected()) {
                PREFERENCES.put(USERNAME_KEY, usernameField.getText().trim());
                PREFERENCES.put(PASSWORD_KEY, passwordField.getText().trim());
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(usernameField.getText().trim(), passwordField.getText().trim());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(action);
    }
}
