package xyz.refinedev.api.scoreboard.adapter;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * <p>
 * This Project is property of Refine Development.<br>
 * Copyright © 2024, All Rights Reserved.<br>
 * Redistribution of this Project is not allowed.<br>
 * </p>
 *
 * @author Drizzy
 * @version ScoreboardAPI
 * @since 10/2/2024
 */

public interface ScoreboardAdapter {

    /**
     * Gets the scoreboard title.
     *
     * @param player who's title is being displayed.
     * @return title.
     */
    List<String> getTitle(Player player);

    /**
     * Gets the scoreboard lines.
     *
     * @param player who's lines are being displayed.
     * @return lines
     */
    List<String> getLines(Player player);

}

