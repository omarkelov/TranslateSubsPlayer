package ru.nsu.fit.markelov.subtitles;

import uk.co.caprica.vlcj.subs.parser.SpuParseException;
import uk.co.caprica.vlcj.subs.parser.SrtParser;

public class BOMSrtParser extends SrtParser {
    @Override
    protected void process(String line) throws SpuParseException {
        super.process(line.replace("\uFEFF", ""));
    }
}
