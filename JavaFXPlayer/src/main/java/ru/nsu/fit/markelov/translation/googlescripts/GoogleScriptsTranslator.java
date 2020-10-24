package ru.nsu.fit.markelov.translation.googlescripts;

import ru.nsu.fit.markelov.translation.Translator;
import ru.nsu.fit.markelov.translation.entities.TranslationResult;

import java.io.IOException;
import java.net.URISyntaxException;

import static ru.nsu.fit.markelov.util.http.HttpUtil.getContent;

public class GoogleScriptsTranslator extends Translator {
    // todo scripts from multiple emails

    private static final String GOOGLE_SCRIPTS_URL_FORMAT = "https://script.google.com/macros/s/" +
        "AKfycbzEbZdkPbqj7IrDD1kooNU_Px0eYTAnK5apnIskX3eG7_FzerM/exec?source=%s&target=%s&q=%s";

    public GoogleScriptsTranslator(int attempts) {
        super(attempts);
    }

    @Override
    protected void updateTranslationResult(TranslationResult translationResult,
        String sourceLanguage, String targetLanguage, String text)
        throws URISyntaxException, IOException, InterruptedException
    {
        String url = String.format(GOOGLE_SCRIPTS_URL_FORMAT,
            sourceLanguage, targetLanguage, encode(text));

        String content = getContent(url);

        if (content.startsWith("<!DOCTYPE html>")) {
            throw new IOException("Google scripts' exception has occurred");
        }

        translationResult.setTranslation(getContent(url));
    }
}
