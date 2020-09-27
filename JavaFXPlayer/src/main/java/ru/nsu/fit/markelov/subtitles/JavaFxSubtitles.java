package ru.nsu.fit.markelov.subtitles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;

public class JavaFxSubtitles {

    private static final Font SUBTITLES_FONT = new Font(30);
    private static final String SPACE = " ";
    private static final String NEW_LINE = System.lineSeparator();

    private final List<Text> subtitles;
    private double textFlowWidth;

    public JavaFxSubtitles(String str) {
        subtitles = new ArrayList<>();

        populateSubtitles(str);
    }

    private void populateSubtitles(String str) {
        String[] lines = str.split("(\r\n|\n)");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                continue;
            }

            double lineWidth = 0;

            String[] words = lines[i].split("\\s+");
            for (int j = 0; j < words.length; j++) {
                Text word = createText(words[j]);
                subtitles.add(word);
                lineWidth += word.getBoundsInLocal().getWidth();

                if (j < words.length - 1) {
                    Text delimiter = createText(SPACE);
                    subtitles.add(delimiter);
                    lineWidth += delimiter.getBoundsInLocal().getWidth();
                }
            }

            if (i < lines.length - 1) {
                subtitles.add(createText(NEW_LINE));
            }

            if (lineWidth > textFlowWidth) {
                textFlowWidth = lineWidth;
            }
        }
    }

    private Text createText(String str) {
        Text text = new Text(str);
        text.setFont(SUBTITLES_FONT);
        text.setFill(Color.WHITE);

        if (!str.equals(SPACE) && !str.equals(NEW_LINE)) {
            text.setOnMouseClicked(actionEvent -> System.out.println(((Text) actionEvent.getSource()).getText()));
        }

        return text;
    }

    public void updateTextFlow(TextFlow textFlow) {
        textFlow.getChildren().clear();

        textFlow.setMinWidth(textFlowWidth + SUBTITLES_FONT.getSize());
        textFlow.setMaxWidth(textFlowWidth + SUBTITLES_FONT.getSize());

        textFlow.getChildren().addAll(subtitles);
    }
}
