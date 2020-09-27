package ru.nsu.fit.markelov;

import ru.nsu.fit.markelov.subtitles.BOMSrtParser;
import uk.co.caprica.vlcj.subs.Spu;
import uk.co.caprica.vlcj.subs.Spus;
import uk.co.caprica.vlcj.subs.handler.SpuHandler;
import uk.co.caprica.vlcj.subs.parser.SpuParseException;
import uk.co.caprica.vlcj.subs.parser.SpuParser;

import java.io.FileReader;
import java.io.IOException;

public class SpuHandlerTest {

    public static void main(String[] args) throws IOException, SpuParseException {
        SpuParser parser = new BOMSrtParser();
        Spus spus;
        try (FileReader fileReader = new FileReader("F:\\Multimedia\\Series\\Westworld\\1\\Subtitles\\Westworld.[S01E01].HD720.DUB.[qqss44].eng.srt")) {
            spus = parser.parse(fileReader);
        }
        if (spus == null) {
            System.out.println("BAD");
            return;
        }

        final SpuHandler handler = new SpuHandler(spus);
        handler.setOffset(250);
        handler.addSpuEventListener(spu -> {
            if (spu != null) {
                System.out.println(format(spu.value().toString()));
            } else {
                System.out.println("<clear>");
            }
        });

        for (int i = 60000; i < 180000; i += 1000) {
            System.out.printf("Time %d%n", i);
            handler.setTime(i);
        }
    }

    private static String format(String val) {
        return val.replaceAll("(\r\n|\n)", System.lineSeparator());
    }
}
