package ru.nsu.fit.markelov.translation;

import ru.nsu.fit.markelov.translation.entities.TranslationResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public abstract class Translator {

    private static final int SLEEP_TIME_BETWEEN_ATTEMPTS = 750;

    private final int attempts;

    public Translator(int attempts) {
        this.attempts = attempts;
    }

    public TranslationResult translate(
        String sourceLanguage, String targetLanguage, String text) throws InterruptedException
    {
        TranslationResult translationResult = new TranslationResult();
        int attempts = this.attempts;

        while (attempts > 0) {
            try {
                updateTranslationResult(translationResult, sourceLanguage, targetLanguage, text);

                if (translationResult.isEmpty()) {
                    throw new IOException("TranslationResult is empty");
                }

                break;
            } catch (URISyntaxException | IOException e) {
                System.out.println(e.getMessage()); // todo! print stack trace

                Thread.sleep(SLEEP_TIME_BETWEEN_ATTEMPTS);
                attempts--;
            }
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
