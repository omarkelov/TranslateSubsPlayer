package ru.nsu.fit.markelov.subtitles;

import javafx.scene.text.Text;
import ru.nsu.fit.markelov.javafxutil.TextBuilder;

import java.util.ArrayList;
import java.util.List;

import static ru.nsu.fit.markelov.Constants.BIG_BOLD_FONT;
import static ru.nsu.fit.markelov.Constants.NEW_LINE;
import static ru.nsu.fit.markelov.Constants.SPACE;

public class JavaFxSubtitles {

    private static final TextBuilder TEXT_BUILDER = new TextBuilder() {{
        setFont(BIG_BOLD_FONT);
    }};

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
                textList.add(TEXT_BUILDER.setText(words[j]).build());

                if (j < words.length - 1) {
                    textList.add(TEXT_BUILDER.setText(SPACE).build());
                }
            }

            if (i < lines.length - 1) {
                textList.add(TEXT_BUILDER.setText(NEW_LINE).build());
            }
        }
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
