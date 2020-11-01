package ru.nsu.fit.markelov.translation.entities;

import java.util.List;

public class TranslationResult {

    private String translation;
    private List<TranslationGroup> translationGroups;

    public boolean isEmpty() {
        return translation == null && translationGroups == null;
    }

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
