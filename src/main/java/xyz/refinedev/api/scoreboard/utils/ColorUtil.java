package xyz.refinedev.api.scoreboard.utils;

import com.google.common.collect.ImmutableMap;

import lombok.experimental.UtilityClass;
import lombok.val;

import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ColorUtil {

    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().build();

    private static final Map<ChatColor, Color> COLOR_MAPPINGS = ImmutableMap.<ChatColor, Color>builder()
            .put(ChatColor.BLACK, new Color(0, 0, 0))
            .put(ChatColor.DARK_BLUE, new Color(0, 0, 170))
            .put(ChatColor.DARK_GREEN, new Color(0, 170, 0))
            .put(ChatColor.DARK_AQUA, new Color(0, 170, 170))
            .put(ChatColor.DARK_RED, new Color(170, 0, 0))
            .put(ChatColor.DARK_PURPLE, new Color(170, 0, 170))
            .put(ChatColor.GOLD, new Color(255, 170, 0))
            .put(ChatColor.GRAY, new Color(170, 170, 170))
            .put(ChatColor.DARK_GRAY, new Color(85, 85, 85))
            .put(ChatColor.BLUE, new Color(85, 85, 255))
            .put(ChatColor.GREEN, new Color(85, 255, 85))
            .put(ChatColor.AQUA, new Color(85, 255, 255))
            .put(ChatColor.RED, new Color(255, 85, 85))
            .put(ChatColor.LIGHT_PURPLE, new Color(255, 85, 255))
            .put(ChatColor.YELLOW, new Color(255, 255, 85))
            .put(ChatColor.WHITE, new Color(255, 255, 255))
            .build();

    private static final Pattern hexPattern = Pattern.compile("&#[A-Fa-f0-9]{6}");

    /**
     * Get last colors from a string in form of {@link Color}
     *
     * @param input {@link String} The string from which we extract color
     * @return      {@link Color}
     */
    public ChatColor getLastColors(String input) {
        String prefixColor = ChatColor.getLastColors(color(input));

        if (prefixColor.isEmpty()) return null;

        ChatColor color;

        // Hex Color Support
        if (canHex()) {
            try {
                // Convert bukkit's &x based hex color to something bungee color can read (#FFFFFF)
                String hexColor = prefixColor.replace("§", "").replace("x", "#");
                val md5Color = net.md_5.bungee.api.ChatColor.of(hexColor); // Parse into a Bungee Color
                color = getClosestChatColor(md5Color.getColor());
            } catch (Exception e) {
                // If the color is not a hex color, then it's a normal color code
                ChatColor bukkitColor = ChatColor.getByChar(prefixColor.substring(prefixColor.length() - 1).charAt(0));
                if (bukkitColor == null) {
                    return null;
                }
                color = bukkitColor;
            }
        } else {
            // Obviously in older versions, hex color does not exist, so we just parse it normally
            ChatColor bukkitColor = ChatColor.getByChar(prefixColor.substring(prefixColor.length() - 1).charAt(0));
            if (bukkitColor == null) {
                return null;
            }
            color = bukkitColor;
        }
        return color;
    }

    /**
     * Translate '&' based color codes into bukkit ones
     *
     * @param text {@link String} Input Text
     * @return     {@link String} Output Text (with HexColor Support)
     */
    public String color(String text) {
        if (text == null) return "";

        if (canHex()) {
            Matcher matcher = hexPattern.matcher(text);
            while (matcher.find()) {
                try {
                    String color = matcher.group();
                    String hexColor = color
                            .replace("&", "")
                            .replace("x", "#");

                    val bungeeColor = net.md_5.bungee.api.ChatColor.of(hexColor);
                    text = text.replace(color, bungeeColor.toString());
                } catch (Exception ignored) {
                    // Errors about unknown group, can be safely ignored!
                }
            }
        }

        text = ChatColor.translateAlternateColorCodes('&', text);

        return text;
    }

    /**
     * Converts a simple string to a {@link Component}
     *
     * @param string {@link String}
     * @return       {@link Component}
     */
    public Component translate(String string) {
        return legacySerializer.deserialize(color(string));
    }

    public String getRaw(String string) {
        return string.replace("§", "&");
    }

    public ChatColor getClosestChatColor(Color color) {
        ChatColor closest = null;
        int mark = 0;
        for (Map.Entry<ChatColor, Color> entry : COLOR_MAPPINGS.entrySet()) {
            ChatColor key = entry.getKey();
            Color value = entry.getValue();

            int diff = getDiff(value, color);
            if (closest == null || diff < mark) {
                closest = key;
                mark = diff;
            }
        }

        return closest;
    }

    private int getDiff(Color color, Color compare) {
        int a = color.getAlpha() - compare.getAlpha(),
                r = color.getRed() - compare.getRed(),
                g = color.getGreen() - compare.getGreen(),
                b = color.getBlue() - compare.getBlue();
        return a * a + r * r + g * g + b * b;
    }

    public static String VERSION;
    public static int MINOR_VERSION;
    public static boolean canHex() {
        return MINOR_VERSION >= 16;
    }

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
}