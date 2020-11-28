package ru.nsu.fit.markelov.javafxutil;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Window;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static ru.nsu.fit.markelov.Constants.NEW_LINE;

public class AlertBuilder {

    public static final String DEFAULT_ERROR_HEADER = "Unknown error";
    public static final String DEFAULT_ERROR_CONTENT =
        "Unknown error has occurred. Please contact the developer.";

    public static final String APPLICATION_LAUNCH_ERROR_HEADER = "Failed to launch the application";
    public static final String SCENE_SWITCHING_ERROR_HEADER = "Failed to switch the scene";
    public static final String LAYOUT_LOADING_ERROR_HEADER = "Failed to load the layout";
    public static final String VLC_ERROR_HEADER = "Failed to launch the VLC media player";
    public static final String FILE_OPENING_ERROR_HEADER = "Failed to open the file";
    public static final String SUBTITLES_PARSING_ERROR_HEADER = "Failed to parse the subtitles";

    public static final String VLC_ERROR_CONTENT_PREFIX =
        "Make sure you have the latest version of VLC media player installed." +
            NEW_LINE + NEW_LINE + "You can download VLC media player from ";
    public static final String VLC_ERROR_CONTENT_POSTFIX = ".";
    public static final String FILE_OPENING_ERROR_CONTENT_PREFIX =
        "The next file cannot be opened: ";
    public static final String SUBTITLES_PARSING_ERROR_CONTENT_PREFIX =
        "The next file cannot be parsed: ";

    private AlertType alertType = AlertType.ERROR;
    private ButtonType[] buttons = {};
    private String headerText = DEFAULT_ERROR_HEADER;
    private String contentText = DEFAULT_ERROR_CONTENT;
    private Node header;
    private Node content;
    private Exception exception;
    private double dialogMinHeight = Region.USE_PREF_SIZE;
    private Window owner;

    public AlertBuilder setAlertType(AlertType alertType) {
        this.alertType = alertType;
        return this;
    }

    public AlertBuilder setButtons(ButtonType... buttons) {
        this.buttons = buttons;
        return this;
    }

    public AlertBuilder setHeaderText(String headerText) {
        this.headerText = headerText;
        return this;
    }

    public AlertBuilder setContentText(String contentText) {
        this.contentText = contentText;
        return this;
    }

    public AlertBuilder setHeader(Node header) {
        this.header = header;
        return this;
    }

    public AlertBuilder setContent(Node content) {
        this.content = content;
        return this;
    }

    public AlertBuilder setException(Exception exception) {
        this.exception = exception;
        return this;
    }

    public AlertBuilder setDialogMinHeight(double dialogMinHeight) {
        this.dialogMinHeight = dialogMinHeight;
        return this;
    }

    public AlertBuilder setOwner(Window owner) {
        this.owner = owner;
        return this;
    }

    public Alert build() {
        Alert alert = new Alert(alertType, "", buttons);

        if (header != null) {
            alert.getDialogPane().setHeader(header);
        } else {
            alert.setHeaderText(headerText);
        }

        if (content != null) {
            alert.getDialogPane().setContent(content);
        } else {
            alert.setContentText(contentText);
        }

        if (exception != null) {
            try (StringWriter stringWriter = new StringWriter();
                 PrintWriter printWriter = new PrintWriter(stringWriter))
            {
                exception.printStackTrace(printWriter);
                String exceptionText = stringWriter.toString();

                Label label = new Label("The exception stacktrace:");

                TextArea textArea = new TextArea(exceptionText);
                textArea.setEditable(false);
                textArea.setWrapText(true);

                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);

                GridPane expandableContent = new GridPane();
                expandableContent.setMaxWidth(Double.MAX_VALUE);
                expandableContent.add(label, 0, 0);
                expandableContent.add(textArea, 0, 1);

                alert.getDialogPane().setExpandableContent(expandableContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        alert.getDialogPane().setMinHeight(dialogMinHeight);

        try {
            alert.initOwner(owner);
        } catch (RuntimeException ignored) {}

        return alert;
    }
}
