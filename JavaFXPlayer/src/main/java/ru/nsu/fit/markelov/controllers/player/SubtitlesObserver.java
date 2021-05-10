package ru.nsu.fit.markelov.controllers.player;

public interface SubtitlesObserver {
    void onSubtitlesTextPressed();
    void onSubtitlesInitialized(String hashSum, String linesJson);
}
