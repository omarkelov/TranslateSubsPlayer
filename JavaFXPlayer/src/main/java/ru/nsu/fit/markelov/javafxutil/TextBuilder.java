package ru.nsu.fit.markelov.javafxutil;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static ru.nsu.fit.markelov.Constants.STANDARD_COLOR;

public class TextBuilder {

    private String text;
    private Font font;
    private Color color = STANDARD_COLOR;

    public TextBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public TextBuilder setFont(Font font) {
        this.font = font;
        return this;
    }

    public TextBuilder setColor(Color color) {
        this.color = color;
        return this;
    }

    public Text build() {
        Text text = new Text();

        if (this.text != null) text.setText(this.text);
        if (font != null) text.setFont(font);
        if (color != null) text.setFill(color);

        return text;
    }
}
