package xyz.refinedev.api.scoreboard.animation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import net.kyori.adventure.text.Component;

import net.megavex.scoreboardlibrary.api.sidebar.component.animation.SidebarAnimation;

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
    private final SidebarAnimation<Component> component;

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
