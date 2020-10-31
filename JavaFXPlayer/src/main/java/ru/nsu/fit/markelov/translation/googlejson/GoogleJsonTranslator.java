package ru.nsu.fit.markelov.translation.googlejson;

import org.json.JSONArray;
import org.json.JSONException;
import ru.nsu.fit.markelov.translation.Translator;
import ru.nsu.fit.markelov.translation.entities.TranslationGroup;
import ru.nsu.fit.markelov.translation.entities.TranslationResult;
import ru.nsu.fit.markelov.translation.entities.TranslationVariant;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.nsu.fit.markelov.translation.googlejson.TokenGenerator.generateToken;
import static ru.nsu.fit.markelov.util.http.HttpUtil.getContent;

public class GoogleJsonTranslator extends Translator {

    private static final String MAIN_PAGE_URL = "https://translate.google.com/";
    private static final String TRANSLATION_URL_FORMAT = MAIN_PAGE_URL +
        "translate_a/single?client=webapp&sl=%s&tl=%s&hl=%2$s&dt=at&dt=bd&dt=ex&dt=ld&dt=md&" +
        "dt=qca&dt=rw&dt=rm&dt=sos&dt=ss&dt=t&otf=1&pc=1&ssel=0&tsel=0&kc=2&tk=%s&q=%s";

    private String tokenKey;

    public GoogleJsonTranslator(int attempts) {
        super(attempts);
    }

    @Override
    protected void updateTranslationResult(TranslationResult translationResult,
        String sourceLanguage, String targetLanguage, String text)
        throws URISyntaxException, IOException, InterruptedException
    {
        if (tokenKey == null) {
            tokenKey = requestTokenKey();
        }

        String token = generateToken(tokenKey, text);
        String json = requestJson(sourceLanguage, targetLanguage, token, text);

        updateTranslationResultFromJson(translationResult, json);

        if (translationResult.isEmpty()) {
            tokenKey = null;
        }
    }

    private String requestTokenKey() throws IOException, URISyntaxException, InterruptedException {
        String content = getContent(MAIN_PAGE_URL);

        Pattern pattern = Pattern.compile("tkk:'\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(content);

        if (!matcher.find()) {
            throw new IOException("Token key is not found:" + System.lineSeparator() + content);
        }

        return matcher.group().substring(5);
    }

    private String requestJson(String sourceLanguage, String targetLanguage, String token,
        String text) throws IOException, URISyntaxException, InterruptedException
    {
        String jsonUrl = String.format(TRANSLATION_URL_FORMAT,
            sourceLanguage, targetLanguage, token, encode(text));

        return getContent(jsonUrl);
    }

    private void updateTranslationResultFromJson(TranslationResult translationResult, String json) {
        try {
            // trying to set mainTranslation
            JSONArray outerArray = new JSONArray(json);
            JSONArray mainArray = outerArray.getJSONArray(0);

            StringBuilder translationBuilder = new StringBuilder();
            for (int i = 0; i < mainArray.length(); i++) {
                JSONArray mainTranslationArray = mainArray.getJSONArray(i);
                String translationPart = mainTranslationArray.optString(0);

                translationBuilder.append(translationPart);
            }

            if (translationBuilder.length() == 0) {
                throw new JSONException("No translation");
            }

            translationResult.setTranslation(translationBuilder.toString());

            // trying to set translationGroups
            JSONArray groupsArray = outerArray.getJSONArray(1);
            List<TranslationGroup> translationGroups = new ArrayList<>();
            for (int i = 0; i < groupsArray.length(); i++) {
                JSONArray groupArray = groupsArray.getJSONArray(i);

                TranslationGroup translationGroup = new TranslationGroup();
                translationGroup.setPartOfSpeech(groupArray.optString(0));

                JSONArray variantsArray = groupArray.getJSONArray(2);
                List<TranslationVariant> translationVariants = new ArrayList<>();
                for (int j = 0; j < variantsArray.length(); j++) {
                    JSONArray variantArray = variantsArray.getJSONArray(j);

                    TranslationVariant translationVariant = new TranslationVariant();
                    translationVariant.setWord(variantArray.optString(0));

                    JSONArray translationArray = variantArray.getJSONArray(1);
                    List<String> translations = new ArrayList<>();
                    for (int k = 0; k < translationArray.length(); k++) {
                        translations.add(translationArray.optString(k));
                    }
                    translationVariant.setTranslations(translations);

                    translationVariants.add(translationVariant);
                }
                translationGroup.setVariants(translationVariants);

                translationGroups.add(translationGroup);
            }

            translationResult.setTranslationGroups(translationGroups);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}
