package xyz.refinedev.api.scoreboard.component;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This class is the property of Refine Development.<br>
 * Copyright © 2025, All Rights Reserved.<br>
 * Redistribution of this class without permission is not allowed.<br>
 * </p>
 *
 * @author Drizzy
 * @version ScoreboardAPI
 * @since 1/24/2025
 */

@Getter
public class TitleComponent {

    private final List<String> titleLines;
    private final boolean titleAnimated;
    private final int animationSpeed, replayDelay;

    public TitleComponent(String title) {
        this.titleLines = Collections.singletonList(title);
        this.titleAnimated = false;
        this.animationSpeed = 0;
        this.replayDelay = 0;
    }

    public TitleComponent(List<String> titleLines, int animationSpeed, int replayDelay) {
        this.titleLines = titleLines;
        this.titleAnimated = true;
        this.animationSpeed = animationSpeed;
        this.replayDelay = replayDelay;
    }
}
