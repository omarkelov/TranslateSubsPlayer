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
        "translate_a/single?client=webapp&sl=%s&tl=%s&hl=ru&dt=at&dt=bd&dt=ex&dt=ld&dt=md&" +
        "dt=qca&dt=rw&dt=rm&dt=sos&dt=ss&dt=t&otf=1&pc=1&ssel=0&tsel=0&kc=2&tk=%s&q=%s";

    public GoogleJsonTranslator(int attempts) {
        super(attempts);
    }

    @Override
    protected void updateTranslationResult(TranslationResult translationResult,
        String sourceLanguage, String targetLanguage, String text)
        throws URISyntaxException, IOException, InterruptedException
    {
        String tokenKey = requestTokenKey(); // todo! do not get every time, but try to get 3 times if needed
        String token = generateToken(tokenKey, text);
        String json = requestJson(sourceLanguage, targetLanguage, token, text);

        updateTranslationResultFromJson(translationResult, json);
    }

    private String requestTokenKey() throws IOException, URISyntaxException, InterruptedException {
        String content = getContent(MAIN_PAGE_URL);

        Pattern pattern = Pattern.compile("tkk:'\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(content);

        if (!matcher.find()) {
            throw new IOException("Token key is not found");
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
            //json = "[[[\"прекрасный\",\"lovely\",null,null,3,null,null,[[]],[[[\"cce7c67b3f2439089dd6b428e0b83b88\",\"en_ru_2020q2.md\"]]]],[null,null,\"prekrasnyy\",\"ˈləvlē\"]],[[\"имя прилагательное\",[\"прекрасный\",\"красивый\",\"милый\",\"восхитительный\",\"очаровательный\",\"прелестный\",\"чудный\",\"привлекательный\"],[[\"прекрасный\",[\"beautiful\",\"fine\",\"great\",\"glorious\",\"lovely\",\"beauteous\"],null,0.17929012],[\"красивый\",[\"beautiful\",\"handsome\",\"nice\",\"lovely\",\"goodly\",\"fair\"],null,0.038774207],[\"милый\",[\"cute\",\"dear\",\"nice\",\"sweet\",\"lovely\",\"darling\"],null,0.01720595],[\"восхитительный\",[\"delightful\",\"delicious\",\"lovely\",\"adorable\",\"delectable\",\"admirable\"],null,0.00800151],[\"очаровательный\",[\"charming\",\"enchanting\",\"fascinating\",\"glamorous\",\"captivating\",\"lovely\"],null,0.0071725072],[\"прелестный\",[\"charming\",\"lovely\",\"pretty\",\"adorable\",\"exquisite\",\"delectable\"],null,0.003084857],[\"чудный\",[\"marvelous\",\"wonderful\",\"lovely\",\"dreamy\",\"marvellous\"],null,0.0012080474],[\"привлекательный\",[\"attractive\",\"appealing\",\"engaging\",\"inviting\",\"lovable\",\"lovely\"],null,0.00050358905]],\"lovely\",3],[\"имя существительное\",[\"красотка\"],[[\"красотка\",[\"babe\",\"beautiful\",\"cutie\",\"lovely\",\"bombshell\",\"peach\"],null,0.00036843363,null,2]],\"lovely\",1]],\"en\",null,null,[[\"lovely\",null,[[\"прекрасный\",0,true,false]],[[0,6]],\"lovely\",0,0]],0.46797875,[],[[\"en\"],null,[0.46797875],[\"en\"]],null,null,[[\"имя прилагательное\",[[[\"beauteous\"],\"m_en_gbus0597020.007\"],[[\"sightly\",\"pulchritudinous\"],\"m_en_gbus0597020.007\"],[[\"tasty\",\"knockout\",\"stunning\",\"drop-dead gorgeous\",\"adorbs\",\"fit\",\"smashing\",\"cute\",\"foxy\"],\"m_en_gbus0597020.007\"],[[\"comely\",\"fair\"],\"m_en_gbus0597020.007\"],[[\"beautiful\",\"pretty\",\"as pretty as a picture\",\"attractive\",\"good-looking\",\"appealing\",\"handsome\",\"adorable\",\"exquisite\",\"sweet\",\"personable\",\"charming\",\"enchanting\",\"engaging\",\"bewitching\",\"winsome\",\"seductive\",\"gorgeous\",\"alluring\",\"ravishing\",\"glamorous\",\"bonny\"],\"m_en_gbus0597020.007\"],[[\"scenic\",\"picturesque\",\"pleasing\",\"easy on the eye\",\"magnificent\",\"stunning\",\"splendid\"],\"m_en_gbus0597020.007\"],[[\"delightful\",\"very pleasant\",\"very nice\",\"very agreeable\",\"marvelous\",\"wonderful\",\"sublime\",\"superb\",\"fine\",\"magical\",\"enchanting\",\"captivating\",\"terrific\",\"fabulous\",\"fab\",\"heavenly\",\"divine\",\"amazing\",\"glorious\"],\"m_en_gbus0597020.011\"]],\"lovely\"]],[[\"имя прилагательное\",[[\"exquisitely beautiful.\",\"m_en_gbus0597020.007\",\"lovely views\"]],\"lovely\"],[\"имя существительное\",[[\"a glamorous woman or girl.\",\"m_en_gbus0597020.013\",\"a bevy of rock lovelies\"]],\"lovely\"]],[[[\"a <b>lovely</b> house\",null,null,null,3,\"neid_12170\"],[\"<b>lovely</b> views\",null,null,null,3,\"m_en_gbus0597020.007\"],[\"he's a <b>lovely</b> little boy\",null,null,null,3,\"neid_12172\"],[\"I got a <b>lovely</b> letter today\",null,null,null,3,\"neid_12173\"],[\"we've had a <b>lovely</b> day\",null,null,null,3,\"m_en_gbus0597020.011\"],[\"that suit looks <b>lovely</b> on you\",null,null,null,3,\"neid_12170\"],[\"<b>lovely</b>, thanks!\",null,null,null,3,\"neid_12169\"],[\"that's just <b>lovely</b>!\",null,null,null,3,\"neid_12174\"],[\"she's a <b>lovely</b> person\",null,null,null,3,\"m_en_gbus0597020.011\"],[\"you have <b>lovely</b> eyes\",null,null,null,3,\"m_en_gbus0597020.007\"],[\"we had a <b>lovely</b> day\",null,null,null,3,\"neid_12171\"],[\"it was <b>lovely</b> to see him again\",null,null,null,3,\"neid_12171\"]]]]"; // todo delete

            // trying to set mainTranslation
            JSONArray outerArray = new JSONArray(json);
            JSONArray mainArray = new JSONArray(outerArray.get(0).toString());
            JSONArray mainTranslationArray = new JSONArray(mainArray.get(0).toString());

            String translation = mainTranslationArray.get(0).toString(); // todo! parse string from object
            if (translation.equals("null")) {
                throw new JSONException("No translation");
            }

            translationResult.setTranslation(translation);

            // trying to set translationGroups
            JSONArray groupsArray = new JSONArray(outerArray.get(1).toString());
            List<TranslationGroup> translationGroups = new ArrayList<>();
            for (int i = 0; i < groupsArray.length(); i++) {
                JSONArray groupArray = new JSONArray(groupsArray.get(i).toString());

                TranslationGroup translationGroup = new TranslationGroup();
                translationGroup.setPartOfSpeech(groupArray.get(0).toString());

                JSONArray variantsArray = new JSONArray(groupArray.get(2).toString());
                List<TranslationVariant> translationVariants = new ArrayList<>();
                for (int j = 0; j < variantsArray.length(); j++) {
                    JSONArray variantArray = new JSONArray(variantsArray.get(j).toString());

                    TranslationVariant translationVariant = new TranslationVariant();
                    translationVariant.setWord(variantArray.get(0).toString());

                    JSONArray translationArray = new JSONArray(variantArray.get(1).toString());
                    List<String> translations = new ArrayList<>();
                    for (int k = 0; k < translationArray.length(); k++) {
                        translations.add(translationArray.get(k).toString());
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
