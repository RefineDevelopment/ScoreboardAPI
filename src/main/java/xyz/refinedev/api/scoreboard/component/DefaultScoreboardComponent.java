package xyz.refinedev.api.scoreboard.component;

import com.google.common.base.Preconditions;

import lombok.Getter;

import net.kyori.adventure.text.Component;

import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.SidebarAnimation;

import org.bukkit.entity.Player;

import xyz.refinedev.api.scoreboard.adapter.ScoreboardAdapter;
import xyz.refinedev.api.scoreboard.utils.ColorUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DefaultScoreboardComponent {

    private final Sidebar sidebar;
    private final Player player;
    private final ScoreboardAdapter adapter;

    private boolean animated;
    private ComponentSidebarLayout componentSidebar;
    private SidebarComponent title;
    private SidebarAnimation<Component> titleAnimation;

    /**
     * Create a scoreboard for the given player.
     *
     * @param adapter {@link ScoreboardAdapter} The adapter to use for this scoreboard.
     * @param sidebar {@link Sidebar} The sidebar to display the scoreboard on.
     * @param player  {@link Player} The player to display the scoreboard to.
     */
    public DefaultScoreboardComponent(ScoreboardAdapter adapter, Sidebar sidebar, Player player) {
        this.sidebar = sidebar;
        this.adapter = adapter;
        this.player = player;

        this.setup();
    }

    public void setup() {
        List<String> titleLines = this.adapter.getTitle(this.player);
        Preconditions.checkArgument(!titleLines.isEmpty(), "Title lines cannot be empty");

        this.animated = titleLines.size() > 1;
        if (animated) {
            this.titleAnimation = createAnimation(titleLines);
            title = SidebarComponent.animatedLine(titleAnimation);
        } else {
            title = SidebarComponent.staticLine(ColorUtil.translate(titleLines.get(0)));
        }
    }

    public void reload() {
        this.sidebar.removePlayer(this.player);
        this.setup();
        this.sidebar.addPlayer(this.player);
    }

    // Called every tick
    public void tick() {
        if (this.sidebar.closed()) return;

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

        // Advance title animation to the next frame
        if (this.animated && this.titleAnimation != null) {
            this.titleAnimation.nextFrame();
        }

        //TODO: Better implementation so this doesn't spam create stuff
        SidebarComponent component = createComponent(lines);
        this.componentSidebar = new ComponentSidebarLayout(title, component);

        // Update sidebar title & lines
        this.componentSidebar.apply(this.sidebar);
    }

    private SidebarComponent createComponent(List<String> lines) {
        SidebarComponent.Builder builder = SidebarComponent.builder();
        for (String line : lines) {
            if (line.isEmpty() || line.equals(" ")) {
                builder.addBlankLine();
                continue;
            } else if (line.contains("<") || line.contains(">") || line.contains("%")) {
                builder.addDynamicLine(() -> ColorUtil.translate(line));
                continue;
            }

            builder.addStaticLine(ColorUtil.translate(line));
        }
        return builder.build();
    }

    private SidebarAnimation<Component> createAnimation(List<String> title) {
        List<Component> frames = new ArrayList<>(title.size());
        for (String line : title) {
            frames.add(ColorUtil.translate(line));
        }

        return new CollectionSidebarAnimation<>(frames);
    }
}