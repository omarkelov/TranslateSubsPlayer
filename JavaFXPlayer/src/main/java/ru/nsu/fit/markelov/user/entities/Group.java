package ru.nsu.fit.markelov.user.entities;

import java.util.List;

public class Group {

    private final String partOfSpeech;
    private final List<String> variants;

    public Group(String partOfSpeech, List<String> variants) {
        this.partOfSpeech = partOfSpeech;
        this.variants = variants;
    }
}
