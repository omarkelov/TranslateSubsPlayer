package ru.nsu.fit.markelov.util;

import ru.nsu.fit.markelov.javafxutil.AlertBuilder;

import java.io.UnsupportedEncodingException;

public class CharsetConverter {

    private static final String TARGET_ENCODING = "UTF-8";

    public static String convertToUtf8(String str) {
        String sourceEncoding = System.getProperty("file.encoding");

        if (sourceEncoding.equals(TARGET_ENCODING)) {
            return str;
        }

        try {
            return new String(str.getBytes(sourceEncoding), TARGET_ENCODING);
        } catch (UnsupportedEncodingException e) {
            new AlertBuilder()
                .setException(e)
                .build().showAndWait();
        }

        return str;
    }
}
