package xyz.refinedev.api.scoreboard.thread;

import xyz.refinedev.api.scoreboard.ScoreboardHandler;
import xyz.refinedev.api.scoreboard.component.ScoreboardComponent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ScoreboardTickThread {

    private final ScoreboardHandler scoreboardHandler;
    private final ScheduledExecutorService executorService;
    private final Logger logger;

    /**
     * ScoreboardHandler Thread.
     *
     * @param scoreboardHandler instance.
     */
    public ScoreboardTickThread(ScoreboardHandler scoreboardHandler) {
        String name = scoreboardHandler.getPlugin().getName() + " - Scoreboard Thread";
        this.executorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, name));
        this.executorService.scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.MILLISECONDS);

        this.scoreboardHandler = scoreboardHandler;
        this.logger = scoreboardHandler.getPlugin().getLogger();
    }

    public void stopExecuting() {
        this.executorService.shutdown();
    }

    /**
     * Tick logic for thread.
     */
    private void tick() {
        for ( ScoreboardComponent board : this.scoreboardHandler.getBoards().values() ) {
            try {
                // This shouldn't happen, but just in case.
                if (board == null || board.getSidebar().closed()) {
                    continue;
                }

                if (board.isHasTicked()) {
                    board.tickAnimation();
                }

                board.tickScoreboard();
            } catch (Exception e) {
                if (this.scoreboardHandler.isDebug()) {
                    logger.log(Level.SEVERE, "There was an error updating " + board.getPlayer().getName() + "'s scoreboard.");
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }
    }
