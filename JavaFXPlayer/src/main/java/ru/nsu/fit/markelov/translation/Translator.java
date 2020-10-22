package ru.nsu.fit.markelov.translation;

import ru.nsu.fit.markelov.translation.entities.TranslationResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public abstract class Translator {

    public TranslationResult translate(
        String sourceLanguage, String targetLanguage, String text) throws InterruptedException
    {
        TranslationResult translationResult = new TranslationResult();

        try {
            updateTranslationResult(translationResult, sourceLanguage, targetLanguage, text);
        } catch (URISyntaxException | IOException e) {
            System.out.println(e.getMessage()); // todo! print stack trace
        }

        return translationResult;
    }

    abstract protected void updateTranslationResult(TranslationResult translationResult,
        String sourceLanguage, String targetLanguage, String text)
        throws URISyntaxException, IOException, InterruptedException;

    protected String encode(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }
}
