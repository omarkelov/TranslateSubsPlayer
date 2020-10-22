package ru.nsu.fit.markelov.translation.entities;

import java.util.List;
import java.util.StringJoiner;

public class TranslationResult {

    private String translation;
    private List<TranslationGroup> translationGroups;

    /**
     * Returns translation.
     *
     * @return translation.
     */
    public String getTranslation() {
        return translation;
    }

    /**
     * Sets translation.
     */
    public void setTranslation(String translation) {
        this.translation = translation;
    }

    /**
     * Returns translationGroups.
     *
     * @return translationGroups.
     */
    public List<TranslationGroup> getTranslationGroups() {
        return translationGroups;
    }

    /**
     * Sets translationGroups.
     */
    public void setTranslationGroups(List<TranslationGroup> translationGroups) {
        this.translationGroups = translationGroups;
    }
}
