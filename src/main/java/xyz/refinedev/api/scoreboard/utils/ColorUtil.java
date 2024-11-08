package xyz.refinedev.api.scoreboard.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import lombok.val;
import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ColorUtil {

    public static final LegacyComponentSerializer legacy = LegacyComponentSerializer
            .builder()
            .character(LegacyComponentSerializer.SECTION_CHAR)
            .build();

    private static final Pattern hexPattern = Pattern.compile("&#[A-Fa-f0-9]{6}");
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
    public static Component translate(Player player, String string) {
        PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
        ClientVersion clientVersion = playerManager.getClientVersion(player);
        if (clientVersion.isOlderThan(ClientVersion.V_1_16)) {
            return Component.text(color(string));
        }
        return legacy.deserialize(translateAlternateColorCodes('&', string));
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        Preconditions.checkArgument(textToTranslate != null, "Cannot translate null text");
        char[] b = textToTranslate.toCharArray();

        for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx#".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
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