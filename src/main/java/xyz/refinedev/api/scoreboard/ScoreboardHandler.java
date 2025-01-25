package xyz.refinedev.api.scoreboard;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.refinedev.api.scoreboard.adapter.ScoreboardAdapter;
import xyz.refinedev.api.scoreboard.component.ScoreboardComponent;
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

    private final Map<UUID, ScoreboardComponent> boards = new ConcurrentHashMap<>();

    private final JavaPlugin plugin;
    private final ScoreboardAdapter adapter;

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
            this.thread = null;

            this.boards.forEach((uuid, component) -> {
                component.getSidebar().close();
            });
            this.boards.clear();
        }

        // Register new boards for existing online players.
        for (Player player : Bukkit.getOnlinePlayers()) {
            getBoards().putIfAbsent(player.getUniqueId(), new ScoreboardComponent(adapter, scoreboardLibrary.createSidebar(), player));
        }

        // Start Thread.
        this.thread = new ScoreboardTickThread(this);
    }

    public void reload() {
        for ( ScoreboardComponent component : this.boards.values() ) {
            component.reload();
        }
    }

    public void unload() {
        if (this.scoreboardLibrary == null) return;

        this.scoreboardLibrary.close();
    }

    public void registerScoreboard(Player player, ScoreboardComponent component) {
        this.boards.put(player.getUniqueId(), component);
    }

    public ScoreboardComponent unregisterScoreboard(Player player) {
        return this.boards.remove(player.getUniqueId());
    }
}
