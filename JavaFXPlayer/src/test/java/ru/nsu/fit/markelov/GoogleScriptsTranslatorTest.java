package ru.nsu.fit.markelov;

import org.junit.Assert;
import org.junit.Test;
import ru.nsu.fit.markelov.translation.Translator;
import ru.nsu.fit.markelov.translation.entities.TranslationResult;
import ru.nsu.fit.markelov.translation.googlescripts.GoogleScriptsTranslator;

public class GoogleScriptsTranslatorTest {

    private final Translator translator = new GoogleScriptsTranslator(2);

    @Test
    public void testEnRuSingleWord() throws InterruptedException {
        TranslationResult translationResult = translator.translate("en", "ru", "Lovely");

        Assert.assertEquals("Прекрасный", translationResult.getTranslation());
    }

    @Test
    public void testRuEnSingleWord() throws InterruptedException {
        TranslationResult translationResult = translator.translate("ru", "en", "Прекрасный");

        Assert.assertEquals("Beautiful", translationResult.getTranslation());
    }

    @Test
    public void testEnRuMultipleWords() throws InterruptedException {
        TranslationResult translationResult = translator.translate("en", "ru", "Lovely cat");

        Assert.assertEquals("Прекрасный кот", translationResult.getTranslation());
    }

    @Test
    public void testRuEnMultipleWords() throws InterruptedException {
        TranslationResult translationResult = translator.translate("ru", "en", "Прекрасный кот");

        Assert.assertEquals("Lovely cat", translationResult.getTranslation());
    }

    @Test
    public void testEnRuMultipleSentences() throws InterruptedException {
        TranslationResult translationResult = translator.translate("en", "ru", "Lovely cat. Good dog.");

        Assert.assertEquals("Прекрасный кот. Хороший пес.", translationResult.getTranslation());
    }

    @Test
    public void testRuEnMultipleSentences() throws InterruptedException {
        TranslationResult translationResult = translator.translate("ru", "en", "Прекрасный кот. Хороший пес.");

        Assert.assertEquals("Lovely cat. Good dog.", translationResult.getTranslation());
    }
}
