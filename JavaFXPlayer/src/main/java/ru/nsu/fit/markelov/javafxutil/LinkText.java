package ru.nsu.fit.markelov.javafxutil;

import javafx.application.HostServices;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.text.Text;

public class LinkText {

    private static final String STANDARD_LINK_STYLE = "-fx-fill: #0645ad;";
    private static final String HOVERED_LINK_STYLE = STANDARD_LINK_STYLE +
        "-fx-cursor: hand; -fx-underline: true;";

    private final Text text;

    public LinkText(String text, String uri, HostServices hostServices) {
        this.text = new Text(text);

        this.text.setOnMouseClicked(mouseEvent -> {
            try {
                hostServices.showDocument(uri);
            } catch (Exception ignored) {}
        });

        this.text.styleProperty().bind(Bindings.when(this.text.hoverProperty())
            .then(new SimpleStringProperty(HOVERED_LINK_STYLE))
            .otherwise(new SimpleStringProperty(STANDARD_LINK_STYLE))
        );
    }

    /**
     * Returns linkText.
     *
     * @return linkText.
     */
    public Text getText() {
        return text;
    }
}
