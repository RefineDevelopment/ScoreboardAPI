package xyz.refinedev.api.scoreboard.utils;

public class FastLegacyConverter {

    private static final String[] LEGACY_MAP = new String[256];

    static {
        LEGACY_MAP['0'] = "<black>";
        LEGACY_MAP['1'] = "<dark_blue>";
        LEGACY_MAP['2'] = "<dark_green>";
        LEGACY_MAP['3'] = "<dark_aqua>";
        LEGACY_MAP['4'] = "<dark_red>";
        LEGACY_MAP['5'] = "<dark_purple>";
        LEGACY_MAP['6'] = "<gold>";
        LEGACY_MAP['7'] = "<gray>";
        LEGACY_MAP['8'] = "<dark_gray>";
        LEGACY_MAP['9'] = "<blue>";
        LEGACY_MAP['a'] = "<green>";
        LEGACY_MAP['b'] = "<aqua>";
        LEGACY_MAP['c'] = "<red>";
        LEGACY_MAP['d'] = "<light_purple>";
        LEGACY_MAP['e'] = "<yellow>";
        LEGACY_MAP['f'] = "<white>";
        LEGACY_MAP['k'] = "<obfuscated>";
        LEGACY_MAP['l'] = "<bold>";
        LEGACY_MAP['m'] = "<strikethrough>";
        LEGACY_MAP['n'] = "<underlined>";
        LEGACY_MAP['o'] = "<italic>";
        LEGACY_MAP['r'] = "<reset>";
    }

    public static String convertToMiniMessage(String input) {
        if (input == null || input.isEmpty()) return input;

        // Quick exit if no legacy codes are present
        if (input.indexOf('§') == -1 && input.indexOf('&') == -1) {
            return input;
        }

        char[] chars = input.toCharArray();
        StringBuilder builder = new StringBuilder(input.length() + 32);

        // A tiny stack to keep track of active formatting tags.
        // There are only 5 valid formatting codes (k, l, m, n, o).
        char[] formatStack = new char[5];
        int stackSize = 0;

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if ((c == '§' || c == '&') && i + 1 < chars.length) {
                char code = Character.toLowerCase(chars[i + 1]);

                // 1. Handle BungeeCord Hex: &#RRGGBB
                if (code == '#' && i + 7 < chars.length) {
                    // Hex acts as a color, so we must close active formatting
                    while (stackSize > 0) {
                        builder.append("</").append(LEGACY_MAP[formatStack[--stackSize]].substring(1));
                    }

                    builder.append("<#");
                    for (int j = 2; j <= 7; j++) {
                        builder.append(chars[i + j]);
                    }
                    builder.append(">");
                    i += 7; // Skip the hex string
                    continue;
                }

                // 2. Handle Spigot Hex: &x&r&r&g&g&b&b
                if (code == 'x' && i + 13 < chars.length) {
                    // Close active formatting
                    while (stackSize > 0) {
                        builder.append("</").append(LEGACY_MAP[formatStack[--stackSize]].substring(1));
                    }

                    builder.append("<#")
                            .append(chars[i + 3])
                            .append(chars[i + 5])
                            .append(chars[i + 7])
                            .append(chars[i + 9])
                            .append(chars[i + 11])
                            .append(chars[i + 13])
                            .append(">");
                    i += 13; // Skip the entire spigot hex chain
                    continue;
                }

                // 3. Handle Standard Legacy Colors and Formatting
                boolean isColor = (code >= '0' && code <= '9') || (code >= 'a' && code <= 'f');
                boolean isFormat = (code >= 'k' && code <= 'o');
                boolean isReset = (code == 'r');

                if (isColor || isReset) {
                    // A new color or reset code cancels all previous formatting.
                    // We pop tags off the stack in reverse order to satisfy MiniMessage's strict tree hierarchy.
                    while (stackSize > 0) {
                        // Dynamically generates the closing tag. (e.g. "<bold>" -> "</bold>")
                        builder.append("</").append(LEGACY_MAP[formatStack[--stackSize]].substring(1));
                    }
                    builder.append(LEGACY_MAP[code]);
                    i++;
                    continue;
                }

                if (isFormat) {
                    // Ensure we don't push duplicate formats to the stack (e.g. "&l&l")
                    boolean exists = false;
                    for (int j = 0; j < stackSize; j++) {
                        if (formatStack[j] == code) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        formatStack[stackSize++] = code;
                        builder.append(LEGACY_MAP[code]);
                    }
                    i++;
                    continue;
                }
            }
            // Not a color code, just append the character
            builder.append(c);
        }

        // 4. End of string: close any remaining open formatting tags cleanly
        while (stackSize > 0) {
            builder.append("</").append(LEGACY_MAP[formatStack[--stackSize]].substring(1));
        }

        return builder.toString();
    }
}