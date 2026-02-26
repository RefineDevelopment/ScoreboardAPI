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

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if ((c == '§' || c == '&') && i + 1 < chars.length) {
                char code = Character.toLowerCase(chars[i + 1]);

                // 1. Handle BungeeCord Hex: &#RRGGBB
                if (code == '#' && i + 7 < chars.length) {
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

                // 3. Handle Standard Legacy: &a, &l, etc.
                String replacement = code < 256 ? LEGACY_MAP[code] : null;
                if (replacement != null) {
                    builder.append(replacement);
                    i++; // Skip the color code character
                    continue;
                }
            }
            
            // Not a color code, just append the character
            builder.append(c);
        }

        return builder.toString();
    }
}