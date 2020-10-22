package ru.nsu.fit.markelov.translation.entities;

import java.util.List;

public class TranslationGroup {

    private String partOfSpeech;
    private List<TranslationVariant> translationVariants;

    /**
     * Returns partOfSpeech.
     *
     * @return partOfSpeech.
     */
    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    /**
     * Sets partOfSpeech.
     */
    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    /**
     * Returns variants.
     *
     * @return variants.
     */
    public List<TranslationVariant> getVariants() {
        return translationVariants;
    }

    /**
     * Sets variants.
     */
    public void setVariants(List<TranslationVariant> translationVariants) {
        this.translationVariants = translationVariants;
    }
}
