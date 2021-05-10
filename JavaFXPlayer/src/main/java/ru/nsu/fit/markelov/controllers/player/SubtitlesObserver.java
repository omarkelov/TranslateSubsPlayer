package ru.nsu.fit.markelov.controllers.player;

import ru.nsu.fit.markelov.translation.entities.TranslationResult;

public interface SubtitlesObserver {
    void onSubtitlesTextPressed();
    void onSubtitlesInitialized(String hashSum, String linesJson);
    void onPhraseTranslated(String hashSum, int lineId, String phrase, TranslationResult translationResult);
}
