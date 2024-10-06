package xyz.refinedev.api.scoreboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.refinedev.api.scoreboard.adapter.ScoreboardAdapter;
import xyz.refinedev.api.scoreboard.component.DefaultScoreboardComponent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

@Getter
@RequiredArgsConstructor
public class ScoreboardHandler {

    private final Map<UUID, DefaultScoreboardComponent> boards = new ConcurrentHashMap<>();

    private final JavaPlugin plugin;
    private final ScoreboardAdapter adapter;

    @Setter private long ticks = 10;
    @Setter private boolean debug;

    private ScoreboardLibrary scoreboardLibrary;

    public void init() {
        try {
            this.scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin);
        } catch (NoPacketAdapterAvailableException e) {
            // If no packet adapter was found, you can fall back to the no-op implementation:
            this.scoreboardLibrary = new NoopScoreboardLibrary();
            this.plugin.getLogger().warning("No scoreboard packet adapter available!");
        }
    }

    public void reload() {
        for ( DefaultScoreboardComponent component : this.boards.values() ) {
            component.reload();
        }
    }

    public void unload() {
        if (this.scoreboardLibrary == null) return;

        this.scoreboardLibrary.close();
    }

    public void registerScoreboard(Player player, DefaultScoreboardComponent component) {
        this.boards.put(player.getUniqueId(), component);
    }

    public DefaultScoreboardComponent unregisterScoreboard(Player player) {
        return this.boards.remove(player.getUniqueId());
    }
}
