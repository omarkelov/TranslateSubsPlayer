package ru.nsu.fit.markelov.subtitles;

import uk.co.caprica.vlcj.subs.parser.SpuParseException;
import uk.co.caprica.vlcj.subs.parser.SrtParser;

import static ru.nsu.fit.markelov.util.CharsetConverter.convertToUtf8;

public class BOMSrtParser extends SrtParser {
    @Override
    protected void process(String line) throws SpuParseException {
        super.process(convertToUtf8(line).replace("\uFEFF", ""));
    }
}
