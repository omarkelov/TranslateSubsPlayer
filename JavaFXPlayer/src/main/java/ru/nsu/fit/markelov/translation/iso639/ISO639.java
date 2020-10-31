package ru.nsu.fit.markelov.translation.iso639;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class ISO639 {

    private static final Map<String, String> LANGUAGE_CODES = new TreeMap<>() {{
        put("Afrikaans", "af");
        put("Albanian", "sq");
        put("Amharic", "am");
        put("Arabic", "ar");
        put("Armenian", "hy");
        put("Azerbaijani", "az");
        put("Basque", "eu");
        put("Belarusian", "be");
        put("Bengali", "bn");
        put("Bosnian", "bs");
        put("Bulgarian", "bg");
        put("Catalan", "ca");
        put("Cebuano", "ceb");
        put("Chinese (Simplified)", "zh");
        put("Chinese (Traditional)", "zh-TW");
        put("Corsican", "co");
        put("Croatian", "hr");
        put("Czech", "cs");
        put("Danish", "da");
        put("Dutch", "nl");
        put("English", "en");
        put("Esperanto", "eo");
        put("Estonian", "et");
        put("Finnish", "fi");
        put("French", "fr");
        put("Frisian", "fy");
        put("Galician", "gl");
        put("Georgian", "ka");
        put("German", "de");
        put("Greek", "el");
        put("Gujarati", "gu");
        put("Haitian Creole", "ht");
        put("Hausa", "ha");
        put("Hawaiian", "haw");
        put("Hebrew", "he or iw");
        put("Hindi", "hi");
        put("Hmong", "hmn");
        put("Hungarian", "hu");
        put("Icelandic", "is");
        put("Igbo", "ig");
        put("Indonesian", "id");
        put("Irish", "ga");
        put("Italian", "it");
        put("Japanese", "ja");
        put("Javanese", "jv");
        put("Kannada", "kn");
        put("Kazakh", "kk");
        put("Khmer", "km");
        put("Kinyarwanda", "rw");
        put("Korean", "ko");
        put("Kurdish", "ku");
        put("Kyrgyz", "ky");
        put("Lao", "lo");
        put("Latin", "la");
        put("Latvian", "lv");
        put("Lithuanian", "lt");
        put("Luxembourgish", "lb");
        put("Macedonian", "mk");
        put("Malagasy", "mg");
        put("Malay", "ms");
        put("Malayalam", "ml");
        put("Maltese", "mt");
        put("Maori", "mi");
        put("Marathi", "mr");
        put("Mongolian", "mn");
        put("Myanmar (Burmese)", "my");
        put("Nepali", "ne");
        put("Norwegian", "no");
        put("Nyanja (Chichewa)", "ny");
        put("Odia (Oriya)", "or");
        put("Pashto", "ps");
        put("Persian", "fa");
        put("Polish", "pl");
        put("Portuguese (Portugal, Brazil)", "pt");
        put("Punjabi", "pa");
        put("Romanian", "ro");
        put("Russian", "ru");
        put("Samoan", "sm");
        put("Scots Gaelic", "gd");
        put("Serbian", "sr");
        put("Sesotho", "st");
        put("Shona", "sn");
        put("Sindhi", "sd");
        put("Sinhala (Sinhalese)", "si");
        put("Slovak", "sk");
        put("Slovenian", "sl");
        put("Somali", "so");
        put("Spanish", "es");
        put("Sundanese", "su");
        put("Swahili", "sw");
        put("Swedish", "sv");
        put("Tagalog (Filipino)", "tl");
        put("Tajik", "tg");
        put("Tamil", "ta");
        put("Tatar", "tt");
        put("Telugu", "te");
        put("Thai", "th");
        put("Turkish", "tr");
        put("Turkmen", "tk");
        put("Ukrainian", "uk");
        put("Urdu", "ur");
        put("Uyghur", "ug");
        put("Uzbek", "uz");
        put("Vietnamese", "vi");
        put("Welsh", "cy");
        put("Xhosa", "xh");
        put("Yiddish", "yi");
        put("Yoruba", "yo");
        put("Zulu", "zu");
    }};

    public static Collection<String> getLanguages() {
        return LANGUAGE_CODES.keySet();
    }

    public static String getLanguageCode(String language) {
        return LANGUAGE_CODES.get(language);
    }
}
