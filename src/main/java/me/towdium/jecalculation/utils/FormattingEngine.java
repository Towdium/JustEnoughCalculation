package me.towdium.jecalculation.utils;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.IntUnaryOperator;

/**
 * This class is developed by 3TUSK. I changed formatting and removed all the comments. See source code at
 * https://github.com/3TUSK/PanI18n/blob/bleeding/src/main/java/info/tritusk/pani18n/FormattingEngine.java
 * I claim no ownership of any code in this class. It will be removed once that mod becomes stable release.
 */
public final class FormattingEngine {
    private static final String COLOR_CODE = "123456789abcdef";
    private static final String FORMATTING_CODE = "klmno";

    public static List<String> wrapStringToWidth(final String str, final int wrapWidth, final IntUnaryOperator charWidthGetter, final Locale currentLocale) {
        BreakIterator lineBreakEngine = BreakIterator.getLineInstance(currentLocale);
        lineBreakEngine.setText(str);
        ArrayList<String> lines = new ArrayList<>(8);
        String cachedFormat = "";
        char color = '0', format = 'r';
        int start = 0;
        int width = 0;
        boolean boldMode = false;
        for (int index = 0; index < str.length(); index++) {
            char c = str.charAt(index);

            if (c == '\n') {
                lines.add(cachedFormat + str.substring(start, index));
                start = index + 1;
                width = 0;
                if (format != 'r') cachedFormat = new String(new char[]{'\u00A7', color, '\u00A7', format});
                else cachedFormat = new String(new char[]{'\u00A7', color});
                continue;
            } else if (c == '\u00A7') {
                index++;
                char f = Character.toLowerCase(str.charAt(index));
                if (f == 'r' || f == 'R') {
                    color = '0';
                    format = 'r';
                } else if (FORMATTING_CODE.indexOf(f) != -1) {
                    format = f;
                    boldMode = f == 'l';
                } else if (COLOR_CODE.indexOf(f) != -1) {
                    color = f;
                    format = 'r';
                    boldMode = false;
                }
                continue;
            } else {
                width += charWidthGetter.applyAsInt(c);
                if (boldMode) width++;
            }

            if (width > wrapWidth) {
                int end = lineBreakEngine.preceding(index);
                if (lineBreakEngine.isBoundary(index)) end = Math.max(end, index);
                String result;
                if (end <= start) {
                    result = cachedFormat + str.substring(start, index);
                    start = index;
                } else {
                    result = cachedFormat + str.substring(start, end);
                    start = end;
                    index = start;
                }
                lines.add(result);
                index--;
                width = 0;
                if (format != 'r') cachedFormat = new String(new char[]{'\u00A7', color, '\u00A7', format});
                else cachedFormat = new String(new char[]{'\u00A7', color});
            }
        }
        String lastPiece = str.substring(start);
        if (!lastPiece.isEmpty()) lines.add(cachedFormat + str.substring(start));
        lines.trimToSize();
        return lines;
    }
}