package xyz.refinedev.api.scoreboard.component;

import com.google.common.base.Preconditions;

import lombok.Getter;

import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.SidebarAnimation;

import org.bukkit.entity.Player;

import xyz.refinedev.api.scoreboard.adapter.ScoreboardAdapter;
import xyz.refinedev.api.scoreboard.animation.ScoreboardAnimation;
import xyz.refinedev.api.scoreboard.utils.ColorUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class ScoreboardComponent {

    private final List<ScoreboardAnimation> animations = new ArrayList<>();

    private final Sidebar sidebar;
    private final Player player;
    private final ScoreboardAdapter adapter;

    private boolean hasTicked;
    private SidebarComponent title;
    private ScoreboardAnimation titleAnimation;

    private int ticks, tickSpeed;

    @Getter private final AtomicBoolean closed = new AtomicBoolean(false);


    /**
     * Create a scoreboard for the given player.
     *
     * @param adapter {@link ScoreboardAdapter} The adapter to use for this scoreboard.
     * @param sidebar {@link Sidebar} The sidebar to display the scoreboard on.
     * @param player  {@link Player} The player to display the scoreboard to.
     */
    public ScoreboardComponent(ScoreboardAdapter adapter, Sidebar sidebar, Player player) {
        this.sidebar = sidebar;
        this.adapter = adapter;
        this.player = player;

        this.setup();
    }

    public void setup() {
        this.tickSpeed = this.adapter.getLineUpdateTicks(player);
        TitleComponent titleComponent = this.adapter.getTitle(this.player);
        List<String> titleLines = titleComponent.getTitleLines();
        Preconditions.checkArgument(!titleLines.isEmpty(), "Title lines cannot be empty");

        if (titleComponent.isTitleAnimated()) {
            int titleAnimationSpeed = titleComponent.getAnimationSpeed();
            int titleReplayDelay = titleComponent.getReplayDelay();

            this.titleAnimation = ScoreboardAnimation.create(player, "title", titleLines, titleAnimationSpeed, titleReplayDelay);
            this.title = SidebarComponent.animatedLine(titleAnimation.getComponent());
        } else {
            this.title = SidebarComponent.staticLine(ColorUtil.translate(titleLines.get(0)));
        }
    }

    public void reload() {
        this.sidebar.removePlayer(this.player);
        this.setup();
        this.sidebar.addPlayer(this.player);
    }

    public void close() {
        this.closed.set(true);
        this.sidebar.close();
    }

    // Called every tick
    public void tickTitle() {
        if (this.titleAnimation != null) return;

        TitleComponent titleComponent = this.adapter.getTitle(this.player);
        List<String> titleLines = titleComponent.getTitleLines();

        if (!titleComponent.isTitleAnimated() && !titleLines.isEmpty()) {
            this.title = SidebarComponent.staticLine(ColorUtil.translate(titleLines.get(0)));
        }
    }

    public void tickScoreboard() {
        if (this.closed.get() || this.sidebar.closed()) return;

        if (!this.hasTicked) {
            this.hasTicked = true;
        }

        this.ticks++;

        if (this.ticks < this.tickSpeed) {
            return;
        }

        this.ticks = 0;

        this.tickTitle();

        List<String> lines = this.adapter.getLines(this.player);
        if (lines.isEmpty()) {
            if (this.sidebar.players().contains(this.player)) {
                this.sidebar.removePlayer(this.player);
            }
            return;
        } else {
            if (!this.sidebar.players().contains(this.player)) {
                this.sidebar.addPlayer(this.player);
            }
        }

        SidebarComponent component = createComponent(lines);
        ComponentSidebarLayout componentSidebar = new ComponentSidebarLayout(this.title, component);

        // Update sidebar title & lines
        componentSidebar.apply(this.sidebar);
    }

    public void tickAnimation() {
        if (this.closed.get() || this.sidebar.closed()) return;

        // Advance title animation to the next frame
        if (this.titleAnimation != null) {
            this.titleAnimation.tick();
        }

        if (!this.animations.isEmpty()) {
            for ( ScoreboardAnimation animation : this.animations ) {
                animation.tick();
            }
        }
    }

    /**
     * Add an animation to this scoreboard.
     *
     * @param animation {@link SidebarAnimation} The animation to add.
     */
    public void addAnimation(ScoreboardAnimation animation) {
        this.animations.add(animation);
    }

    /**
     * Get an animation from this scoreboard.
     *
     * @param identifier {@link String} The identifier of the animation to get.
     * @return {@link ScoreboardAnimation} The animation with the given identifier.
     */
    public ScoreboardAnimation getAnimation(String identifier) {
        for (ScoreboardAnimation animation : this.animations) {
            if (animation.getIdentifier().equals(identifier)) {
                return animation;
            }
        }
        return null;
    }

    /**
     * Remove an animation from this scoreboard.
     *
     * @param animation {@link ScoreboardAnimation} The animation to remove.
     */
    public void removeAnimation(ScoreboardAnimation animation) {
        this.animations.remove(animation);
    }

    private SidebarComponent createComponent(List<String> lines) {
        SidebarComponent.Builder builder = SidebarComponent.builder();
        for (String line : lines) {
            if (line.isEmpty() || line.equals(" ")) {
                builder.addBlankLine();
                continue;
            }

            // Check if the line contains any animation identifier
            boolean handled = false;
            if (!this.animations.isEmpty()) {
                for (ScoreboardAnimation animation : this.animations) {
                    if (line.contains(animation.getIdentifier())) {
                        // Add the animated line if the identifier is found
                        builder.addAnimatedLine(animation.getComponent());
                        handled = true; // Mark this line as handled
                        break;
                    }
                }
            }

            // If the line wasn't handled by an animation, process it normally
            if (!handled) {
                if (line.contains("<") || line.contains(">") || line.contains("%")) {
                    builder.addDynamicLine(() -> ColorUtil.translate(line));
                } else {
                    builder.addStaticLine(ColorUtil.translate(line));
                }
            }
        }
        return builder.build();
    }
}