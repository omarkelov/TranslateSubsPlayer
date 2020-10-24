package ru.nsu.fit.markelov;

import org.junit.Assert;
import org.junit.Test;
import ru.nsu.fit.markelov.translation.Translator;
import ru.nsu.fit.markelov.translation.entities.TranslationResult;
import ru.nsu.fit.markelov.translation.googlejson.GoogleJsonTranslator;

public class GoogleJsonTranslatorTest {

    private final Translator translator = new GoogleJsonTranslator(2);

    @Test
    public void testEnRuSingleWord() throws InterruptedException {
        TranslationResult translationResult = translator.translate("en", "ru", "Lovely");

        Assert.assertEquals("Прекрасный", translationResult.getTranslation());
        Assert.assertNotNull(translationResult.getTranslationGroups());
    }

    @Test
    public void testRuEnSingleWord() throws InterruptedException {
        TranslationResult translationResult = translator.translate("ru", "en", "Прекрасный");

        Assert.assertEquals("Beautiful", translationResult.getTranslation());
        Assert.assertNotNull(translationResult.getTranslationGroups());
    }

    @Test
    public void testEnRuMultipleWords() throws InterruptedException {
        TranslationResult translationResult = translator.translate("en", "ru", "Lovely cat");

        Assert.assertEquals("Прекрасный кот", translationResult.getTranslation());
        Assert.assertNull(translationResult.getTranslationGroups());
    }

    @Test
    public void testRuEnMultipleWords() throws InterruptedException {
        TranslationResult translationResult = translator.translate("ru", "en", "Прекрасный кот");

        Assert.assertEquals("Lovely cat", translationResult.getTranslation());
        Assert.assertNull(translationResult.getTranslationGroups());
    }

    @Test
    public void testEnRuMultipleSentences() throws InterruptedException {
        TranslationResult translationResult = translator.translate("en", "ru", "Lovely cat. Good dog.");

        Assert.assertEquals("Прекрасный кот. Хороший пес.", translationResult.getTranslation());
        Assert.assertNull(translationResult.getTranslationGroups());
    }

    @Test
    public void testRuEnMultipleSentences() throws InterruptedException {
        TranslationResult translationResult = translator.translate("ru", "en", "Прекрасный кот. Хороший пес.");

        Assert.assertEquals("Lovely cat. Good dog.", translationResult.getTranslation());
        Assert.assertNull(translationResult.getTranslationGroups());
    }
}
