package ru.nsu.fit.markelov.subtitles;

import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public interface SubtitlesObserver {
    void onWordClicked(MouseEvent actionEvent, Text clickedText);
}
