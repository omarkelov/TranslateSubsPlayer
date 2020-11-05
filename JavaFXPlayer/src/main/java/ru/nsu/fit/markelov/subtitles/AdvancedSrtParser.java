package ru.nsu.fit.markelov.subtitles;

import uk.co.caprica.vlcj.subs.parser.SpuParseException;
import uk.co.caprica.vlcj.subs.parser.SrtParser;

import java.util.HashSet;
import java.util.Set;

public class AdvancedSrtParser extends SrtParser {

    private final Set<String> permittedTags;
    private final boolean isBomSupported;

    private AdvancedSrtParser(boolean isBomSupported, Set<String> permittedTags) {
        this.permittedTags = permittedTags;
        this.isBomSupported = isBomSupported;
    }

    @Override
    protected void process(String line) throws SpuParseException {
        if (isBomSupported) {
            line = line.replace("\uFEFF", "");
        }

        line = TagFilter.filter(line, permittedTags);

        super.process(line);
    }

    public static class Builder {
        private final Set<String> permittedTags = new HashSet<>();
        private boolean isBomSupported = false;

        public Builder addTag(String tag) {
            permittedTags.add(tag);
            return this;
        }

        public Builder enableBomSupport() {
            isBomSupported = true;
            return this;
        }

        public AdvancedSrtParser build() {
            return new AdvancedSrtParser(isBomSupported, permittedTags);
        }
    }
}
