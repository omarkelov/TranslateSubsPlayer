package ru.nsu.fit.markelov.user.entities;

public class Phrase {

    private final int lineId;
    private final String phrase;
    private final Translation translation;

    public Phrase(int lineId, String phrase, Translation translation) {
        this.lineId = lineId;
        this.phrase = phrase;
        this.translation = translation;
    }
}
