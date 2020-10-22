package ru.nsu.fit.markelov.translation.entities;

import java.util.List;

public class TranslationVariant {

    private String word;
    private List<String> translations;

    /**
     * Returns word.
     *
     * @return word.
     */
    public String getWord() {
        return word;
    }

    /**
     * Sets word.
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * Returns translations.
     *
     * @return translations.
     */
    public List<String> getTranslations() {
        return translations;
    }

    /**
     * Sets translations.
     */
    public void setTranslations(List<String> translations) {
        this.translations = translations;
    }
}
