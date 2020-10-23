package ru.nsu.fit.markelov.translation.entities;

import java.util.List;
import java.util.StringJoiner;

public class TranslationResult {

    private String translation;
    private List<TranslationGroup> translationGroups;

    @Override
    public String toString() {
        if (translationGroups != null) {
            StringJoiner partOfSpeechJoiner = new StringJoiner(System.lineSeparator());
            for (TranslationGroup translationGroup : translationGroups) {
                StringJoiner variantJoiner = new StringJoiner(System.lineSeparator() + "  ", translationGroup.getPartOfSpeech() + System.lineSeparator() + "  ", "");
                for (TranslationVariant translationVariant : translationGroup.getVariants()) {
                    StringJoiner wordJoiner = new StringJoiner(", ", translationVariant.getWord() + " (", ")");
                    for (String translation : translationVariant.getTranslations()) {
                        wordJoiner.add(translation);
                    }
                    variantJoiner.add(wordJoiner.toString());
                }
                partOfSpeechJoiner.add(variantJoiner.toString());
            }

            return partOfSpeechJoiner.toString();
        } else if (translation != null) {
            return translation;
        } else {
            return "No translation...";
        }
    }

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
