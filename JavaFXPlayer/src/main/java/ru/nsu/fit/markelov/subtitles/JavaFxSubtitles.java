package ru.nsu.fit.markelov.subtitles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class JavaFxSubtitles {

    private static final Font SUBTITLES_FONT = new Font(30);
    private static final String SPACE = " ";
    private static final String NEW_LINE = System.lineSeparator();

    private final SubtitlesObserver subtitlesObserver;
    private final List<Text> textList;

    public JavaFxSubtitles(SubtitlesObserver subtitlesObserver, String str) {
        this.subtitlesObserver = subtitlesObserver;
        textList = new ArrayList<>();

        populateSubtitles(str);
    }

    private void populateSubtitles(String str) {
        String[] lines = str.split("(\r\n|\n)");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                continue;
            }

            String[] words = lines[i].split("\\s+");
            for (int j = 0; j < words.length; j++) {
                Text word = createText(words[j]);
                textList.add(word);

                if (j < words.length - 1) {
                    Text delimiter = createText(SPACE);
                    textList.add(delimiter);
                }
            }

            if (i < lines.length - 1) {
                textList.add(createText(NEW_LINE));
            }
        }
    }

    private Text createText(String str) {
        Text text = new Text(str);
        text.setFont(SUBTITLES_FONT);
        text.setFill(Color.WHITE);

        if (!str.equals(SPACE) && !str.equals(NEW_LINE)) {
            text.setOnMouseClicked(actionEvent ->
                subtitlesObserver.onWordClicked(((Text) actionEvent.getSource()).getText()));
        }

        return text;
    }

    /**
     * Returns subtitles.
     *
     * @return subtitles.
     */
    public List<Text> getTextList() {
        return textList;
    }
}
