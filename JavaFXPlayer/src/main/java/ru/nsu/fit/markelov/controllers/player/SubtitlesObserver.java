package ru.nsu.fit.markelov.controllers.player;

import ru.nsu.fit.markelov.subtitles.SubtitleLine;

import java.util.List;

public interface SubtitlesObserver {
    void onSubtitlesTextPressed();
    void onSubtitlesInitialized(List<SubtitleLine> lines);
}
