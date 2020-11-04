package ru.nsu.fit.markelov.subtitles;

import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static ru.nsu.fit.markelov.Constants.BIG_BOLD_FONT;
import static ru.nsu.fit.markelov.Constants.NEW_LINE;
import static ru.nsu.fit.markelov.Constants.SPACE;
import static ru.nsu.fit.markelov.Constants.STANDARD_COLOR;

public class JavaFxSubtitles {

    private final List<Text> textList;

    public JavaFxSubtitles(String str) {
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
        text.setFont(BIG_BOLD_FONT);
        text.setFill(STANDARD_COLOR);

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
