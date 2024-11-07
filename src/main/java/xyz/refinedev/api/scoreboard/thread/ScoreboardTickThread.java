package xyz.refinedev.api.scoreboard.thread;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.api.scoreboard.ScoreboardHandler;
import xyz.refinedev.api.scoreboard.component.DefaultScoreboardComponent;

import java.util.logging.Logger;
import java.util.logging.Level;

public class ScoreboardTickThread extends Thread {

    private final ScoreboardHandler scoreboardHandler;
    private final Logger logger;
    private volatile boolean running = true;

    /**
     * ScoreboardHandler Thread.
     *
     * @param scoreboardHandler instance.
     */
    public ScoreboardTickThread(ScoreboardHandler scoreboardHandler) {
        super(scoreboardHandler.getPlugin().getName() + " - Scoreboard Thread");
        this.scoreboardHandler = scoreboardHandler;
        this.logger = scoreboardHandler.getPlugin().getLogger();
        this.start();
    }

    @Override
    public void run() {
        while (running) {
            this.tick();

            try {
                Thread.sleep(scoreboardHandler.getTicks() * 50);
            } catch (InterruptedException e) {
                this.stopExecuting();
            }
        }
    }

    public void stopExecuting() {
        this.running = false;
    }

    /**
     * Tick logic for thread.
     */
    private void tick() {
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            try {
                DefaultScoreboardComponent board = this.scoreboardHandler.getBoards().get(player.getUniqueId());

                // This shouldn't happen, but just in case.
                if (board == null || board.getSidebar().closed()) {
                    continue;
                }

                board.tick();
            } catch (Exception e) {
                if (this.scoreboardHandler.isDebug()) {
                    logger.log(Level.SEVERE, "There was an error updating " + player.getName() + "'s scoreboard.");
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }

}
