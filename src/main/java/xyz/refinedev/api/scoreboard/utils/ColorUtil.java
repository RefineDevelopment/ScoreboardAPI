package xyz.refinedev.api.scoreboard.utils;

import lombok.experimental.UtilityClass;

import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Bukkit;

@UtilityClass
public class ColorUtil {

    private static final MiniMessage mm = MiniMessage.builder()
            .preProcessor(FastLegacyConverter::convertToMiniMessage)
            .build();

    public static String VERSION;
    public static int MINOR_VERSION;

    static {
        try {
            String versionName = Bukkit.getServer().getClass().getPackage().getName();
            VERSION = versionName.length() < 4 ? versionName.split("\\.")[2] : versionName.split("\\.")[3];
            MINOR_VERSION = Integer.parseInt(VERSION.split("_")[1]);
        } catch (Exception var1) {
            VERSION = "v" + Bukkit.getServer().getBukkitVersion().replace("-SNAPSHOT", "").replace("-R0.", "_R").replace(".", "_");
            MINOR_VERSION = Integer.parseInt(VERSION.split("_")[1]);
        }
    }

    public static boolean canHex() {
        return MINOR_VERSION >= 16;
    }

    /**
     * Translate '&' based color codes into Bukkit '§' ones.
     * <p>
     * Extremely fast O(n) parser that natively supports both standard codes
     * and Hex codes (&#RRGGBB) without regex or BungeeCord APIs.
     *
     * @param text Input Text
     * @return Output Text (with HexColor Support)
     */
    public String color(String text) {
        if (text == null || text.isEmpty()) return text;

        char[] chars = text.toCharArray();
        StringBuilder builder = new StringBuilder(chars.length + 16);

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == '&' && i + 1 < chars.length) {
                char next = chars[i + 1];

                // 1. Check for BungeeCord Hex format: &#RRGGBB
                if (next == '#' && canHex() && i + 7 < chars.length) {
                    boolean isHex = true;
                    for (int j = 2; j <= 7; j++) {
                        if (!isHexDigit(chars[i + j])) {
                            isHex = false;
                            break;
                        }
                    }

                    if (isHex) {
                        builder.append('§').append('x');
                        for (int j = 2; j <= 7; j++) {
                            builder.append('§').append(Character.toLowerCase(chars[i + j]));
                        }
                        i += 7; // Skip the hex code string
                        continue;
                    }
                }

                // 2. Standard legacy codes (0-9, a-f, k-o, r) and Spigot hex ('x')
                if (isColorCode(next)) {
                    builder.append('§').append(Character.toLowerCase(next));
                    i++; // Skip the color char
                    continue;
                }
            }

            builder.append(c);
        }

        return builder.toString();
    }

    /**
     * Converts a simple string to a {@link Component}
     *
     * @param string {@link String}
     * @return       {@link Component}
     */
    public static Component translate(String string) {
        return mm.deserialize(color(string));
    }

    private boolean isHexDigit(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private boolean isColorCode(char c) {
        return (c >= '0' && c <= '9') ||
                (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F') ||
                (c >= 'k' && c <= 'o') || (c >= 'K' && c <= 'O') ||
                c == 'r' || c == 'R' ||
                (canHex() && (c == 'x' || c == 'X'));
    }
}