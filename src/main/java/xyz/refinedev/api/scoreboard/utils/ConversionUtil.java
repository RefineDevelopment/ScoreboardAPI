package xyz.refinedev.api.scoreboard.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;

/**
 * <p>
 * This Project is property of Refine Development.<br>
 * Copyright © 2024, All Rights Reserved.<br>
 * Redistribution of this Project is not allowed.<br>
 * </p>
 *
 * @author Drizzy
 * @version NameTagAPI
 * @since 5/9/2024
 */
public class ConversionUtil {

    public static NamedTextColor bungeeToNamedTextColor(ChatColor bungeeColor) {
        if (bungeeColor == ChatColor.BLACK) {
            return NamedTextColor.BLACK;
        } else if (bungeeColor == ChatColor.DARK_BLUE) {
            return NamedTextColor.DARK_BLUE;
        } else if (bungeeColor == ChatColor.DARK_GREEN) {
            return NamedTextColor.DARK_GREEN;
        } else if (bungeeColor == ChatColor.DARK_AQUA) {
            return NamedTextColor.DARK_AQUA;
        } else if (bungeeColor == ChatColor.DARK_RED) {
            return NamedTextColor.DARK_RED;
        } else if (bungeeColor == ChatColor.DARK_PURPLE) {
            return NamedTextColor.DARK_PURPLE;
        } else if (bungeeColor == ChatColor.GOLD) {
            return NamedTextColor.GOLD;
        } else if (bungeeColor == ChatColor.GRAY) {
            return NamedTextColor.GRAY;
        } else if (bungeeColor == ChatColor.DARK_GRAY) {
            return NamedTextColor.DARK_GRAY;
        } else if (bungeeColor == ChatColor.BLUE) {
            return NamedTextColor.BLUE;
        } else if (bungeeColor == ChatColor.GREEN) {
            return NamedTextColor.GREEN;
        } else if (bungeeColor == ChatColor.AQUA) {
            return NamedTextColor.AQUA;
        } else if (bungeeColor == ChatColor.RED) {
            return NamedTextColor.RED;
        } else if (bungeeColor == ChatColor.LIGHT_PURPLE) {
            return NamedTextColor.LIGHT_PURPLE;
        } else if (bungeeColor == ChatColor.YELLOW) {
            return NamedTextColor.YELLOW;
        } else if (bungeeColor == ChatColor.WHITE) {
            return NamedTextColor.WHITE;
        } else {
            return NamedTextColor.WHITE; // Default to white if no match
        }
    }

}
