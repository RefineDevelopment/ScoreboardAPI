# ScoreboardAPI
Refine's ScoreboardAPI | Uses MegavexNetwork's scoreboard-library 

# Example Listener

```java
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.refinedev.api.scoreboard.ScoreboardHandler;
import xyz.refinedev.api.scoreboard.component.ScoreboardComponent;

@Getter
@Ignore
@RequiredArgsConstructor
public class ScoreboardListener implements Listener {

    private final ScoreboardHandler scoreboardHandler;

    @EventHandler(priority = EventPriority.HIGHEST) // Create later, after everything has been setup
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Sidebar sidebar = this.scoreboardHandler.getScoreboardLibrary().createSidebar();
        ScoreboardComponent board = new ScoreboardComponent(this.scoreboardHandler.getAdapter(), sidebar, player);

        sidebar.addPlayer(player);

        board.setup();
        board.tickScoreboard();

        this.scoreboardHandler.registerScoreboard(player, board);

        // Completely optional, I do this so any bukkit-based objectives are per-player only.
        // Don't do this if you do not use any type of objectives like health display under name.
        if (player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ScoreboardComponent board = this.scoreboardHandler.unregisterScoreboard(player);
        if (board != null) {
            board.getSidebar().close();
        }
    }

}
```
