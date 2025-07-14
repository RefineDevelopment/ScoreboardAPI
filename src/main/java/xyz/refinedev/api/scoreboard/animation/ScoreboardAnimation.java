package xyz.refinedev.api.scoreboard.animation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import net.kyori.adventure.text.Component;

import net.megavex.scoreboardlibrary.api.sidebar.component.animation.FramedSidebarAnimation;

import org.bukkit.entity.Player;
import xyz.refinedev.api.scoreboard.utils.AnimationUtil;

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
 * @since 10/9/2024
 */

@Getter @Setter
@RequiredArgsConstructor
public class ScoreboardAnimation {

    private final String identifier;

    private FramedSidebarAnimation<Component> component;
    private int animationSpeed, replayDelay;
    private int currentTick;
    private int delay;

    /**
     * Create a new scoreboard animation.
     *
     * @param component      The component to animate.
     * @param identifier     The identifier of the animation.
     * @param animationSpeed The speed of the animation. (In ticks)
     * @param replayDelay    The delay before replaying the animation. (In ticks)
     */
    public ScoreboardAnimation(FramedSidebarAnimation<Component> component, String identifier, int animationSpeed, int replayDelay) {
        this.component = component;
        this.identifier = identifier;
        this.animationSpeed = animationSpeed;
        this.replayDelay = replayDelay;

        this.currentTick = 0;
        this.delay = 0;
    }

    /**
     * Create a new scoreboard animation.
     *
     * @param player         The player to create the animation for.
     * @param identifier     The identifier of the animation.
     * @param lines          The lines to animate.
     * @param animationSpeed The speed of the animation. (In ticks)
     * @param replayDelay    The delay before replaying the animation. (In ticks)
     * @return               The created animation.
     */
    public static ScoreboardAnimation create(Player player, String identifier, List<String> lines, int animationSpeed, int replayDelay) {
        return new ScoreboardAnimation(AnimationUtil.createAnimation(player, lines), identifier, animationSpeed * 50, replayDelay * 50);
    }

    public void tick() {
        // Should not proceed if it's a static board.
        if (this.animationSpeed < 0) return;

        if (this.delay > 0) {
            this.delay--;
            return;
        }

        // Only progress further once the animation's refresh rate has elapsed.
        this.currentTick++;

        if (this.currentTick < this.animationSpeed) return;

        // Otherwise progress the current index.
        this.currentTick = 0;
        this.component.nextFrame();
        if (this.component.currentFrameIndex() == 0) {
            this.delay = this.replayDelay;
        }
    }

    /**
     * Set the lines of this animation.
     *
     * @param player The player to set the lines for.
     * @param lines  The new lines to set.
     */
    public void setLines(Player player, List<String> lines) {
        int currentIndex = this.component.currentFrameIndex();
        // Same lines, just with updated PAPI
        if (lines.size() == this.component.frames().size()) {
            this.component = AnimationUtil.createAnimation(player, lines);
            this.component.switchFrame(currentIndex);
        } else {
            // Different lines, reset the animation
            this.component = AnimationUtil.createAnimation(player, lines);
            this.component.switchFrame(0);
            this.currentTick = 0;
        }
    }



    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ScoreboardAnimation)) {
            return false;
        }
        ScoreboardAnimation other = (ScoreboardAnimation) obj;
        return this.identifier.equals(other.identifier);
    }
}
