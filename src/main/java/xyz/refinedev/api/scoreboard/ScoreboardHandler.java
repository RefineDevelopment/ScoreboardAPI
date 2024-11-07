package xyz.refinedev.api.scoreboard;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.refinedev.api.scoreboard.adapter.ScoreboardAdapter;
import xyz.refinedev.api.scoreboard.component.DefaultScoreboardComponent;
import xyz.refinedev.api.scoreboard.thread.ScoreboardTickThread;

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
    private ScoreboardTickThread thread;

    public void init() {
        this.scoreboardLibrary = Bukkit.getServicesManager().load(ScoreboardLibrary.class);
        Preconditions.checkArgument(this.scoreboardLibrary != null, "ScoreboardLibrary is not registered!");

        this.setup();
    }

    private void setup() {
        // Ensure that the thread has stopped running.
        if (this.thread != null) {
            this.thread.stopExecuting();
            this.thread.interrupt();
            this.thread = null;
        }

        // Register new boards for existing online players.
        for (Player player : Bukkit.getOnlinePlayers()) {
            getBoards().putIfAbsent(player.getUniqueId(), new DefaultScoreboardComponent(adapter, scoreboardLibrary.createSidebar(), player));
        }

        // Start Thread.
        this.thread = new ScoreboardTickThread(this);
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
