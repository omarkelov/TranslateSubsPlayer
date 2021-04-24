package ru.nsu.fit.markelov.subtitles;

import uk.co.caprica.vlcj.subs.Spu;

public class SubtitleLine {

    private long start;
    private long end;
    private String text;

    public SubtitleLine(Spu<?> spu) {
        start = spu.start();
        end = spu.end();
        text = (String) spu.value();
    }
}
