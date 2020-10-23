package ru.nsu.fit.markelov.translation.googlejson;

import java.util.ArrayList;
import java.util.List;

public class TokenGenerator {
    private static final String SB = "+-a^+6";
    private static final String ZB = "+-3^+b+-f";

    public static String generateToken(String tkk, String text) {
        String[] d = tkk.split("\\.");
        long b = Long.parseLong(d[0]);
        List<Integer> e = new ArrayList<>();
        for (int g = 0; g < text.length(); g++) {
            char m = text.charAt(g);
            if (128 > m) {
                e.add((int) m);
            } else {
                if (2048 > m) {
                    e.add(m >> 6 | 192);
                } else {
                    if (55296 == (m & 64512) && g + 1 < text.length() && 56320 == (text.charAt(g + 1) & 64512)) {
                        m = (char) (65536 + ((m & 1023) << 10) + (text.charAt(++g) & 1023));
                        e.add(m >> 18 | 240);
                        e.add(m >> 12 & 63 | 128);
                    } else {
                        e.add(m >> 12 | 224);
                    }
                    e.add(m >> 6 & 63 | 128);
                }
                e.add(m & 63 | 128);
            }
        }
        long ab = b;
        for (Integer anE : e) {
            ab += anE;
            ab = qM(ab, SB);
        }
        ab = qM(ab, ZB);
        ab = ((int) ab) ^ (int) Long.parseLong(d[1]);
        if (0 > ab) {
            ab = ((int) ab & (int) 2147483647L) + 2147483648L;
        }
        ab %= 1E6;
        return (ab + "." + ((int) ab ^ (int) b));
    }

    private static long qM(long a, String b) {
        int limit = (b != null ? b.length() : 0) - 2;
        long aa = a;
        long d;
        for (int i = 0; i < limit; i += 3) {
            d = b.charAt(i + 2);
            d = d >= 'a' ? d - 87 : d - 48;
            d = b.charAt(i + 1) == '+' ? ((int) aa) >>> d : ((int) aa) << d;
            aa = b.charAt(i) == '+' ? ((int) (aa +  d)) & ((int) 4294967295L) : ((int) aa) ^ ((int) d);
        }
        return aa;
    }
}
